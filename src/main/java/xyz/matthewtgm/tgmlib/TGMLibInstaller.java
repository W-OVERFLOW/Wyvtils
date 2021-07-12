package xyz.matthewtgm.tgmlib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TGMLibInstaller {

    private static final Gson gson = new Gson();

    private static final String versions_url = "https://raw.githubusercontent.com/TGMDevelopment/TGMLib-Data/main/versions.json";
    private static final String className = "xyz.matthewtgm.tgmlib.TGMLib";
    private static boolean initialized;
    private static File dataDir;

    public static boolean isInitialized() {
        try {
            Launch.classLoader.clearNegativeEntries(new HashSet<>(Collections.singletonList(className)));
            Field invalidClasses = LaunchClassLoader.class.getDeclaredField("invalidClasses");
            if (!invalidClasses.isAccessible())
                invalidClasses.setAccessible(true);
            Object obj = invalidClasses.get(Launch.classLoader);
            ((Set<String>) obj).remove(className);
            return Class.forName(className) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isLoaded() {
        return initialized;
    }

    public static void load(File gameDir) {
        if (!isLoaded())
            return;

        try {
            Class<?> tgmLib = Class.forName(className);
            Method instance = tgmLib.getDeclaredMethod("getInstance");
            System.out.println(instance);
            Method initialize = tgmLib.getDeclaredMethod("initialize", File.class);
            System.out.println(initialize);
            initialize.invoke(instance.invoke(null), gameDir);
            System.out.println("Loaded TGMLib successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Did NOT load TGMLib successfully.");
    }

    public static ReturnValue initialize(File gameDir) {
        if (isInitialized())
            return ReturnValue.ALREADY_INITIALIZED;

        dataDir = new File(gameDir, "TGMLib");
        if (!dataDir.exists() || !dataDir.isDirectory())
            if (!dataDir.mkdirs())
                return ReturnValue.FAILED;

        JsonObject versionsJson = gson.fromJson(fetchJson(versions_url), JsonObject.class);

        File metaFile = new File(dataDir, "meta.json");

        if (!metaFile.exists() || !metaFile.isFile())
            createAndUpdateMetaFile(metaFile, versionsJson);

        JsonObject localMetadataJson = gson.fromJson(readJsonFromFile(new File(dataDir, "meta.json")), JsonObject.class);

        File tgmLibFile = new File(dataDir, "TGMLib-" + versionsJson.get("latest").getAsString() + ".jar");

        if (!tgmLibFile.exists()) {
            File old = new File(dataDir, "TGMLib-" + localMetadataJson.get("current").getAsString() + ".jar");
            if (old.exists())
                if (!old.delete())
                    return ReturnValue.FAILED;

            if (!download("https://raw.githubusercontent.com/TGMDevelopment/TGMLib-Data/main/downloads/TGMLib-" + versionsJson.get("latest").getAsString() + ".jar", versionsJson.get("latest").getAsString(), tgmLibFile))
                return ReturnValue.FAILED;
        }

        createAndUpdateMetaFile(metaFile, versionsJson);

        addToClasspath(tgmLibFile);

        initialized = true;
        if (!isInitialized())
            return ReturnValue.FAILED;
        return ReturnValue.SUCCESSFUL;
    }

    private static void createAndUpdateMetaFile(File metaFile, JsonObject versionsJson) {
        BufferedWriter writer = null;
        try {
            if (!metaFile.exists() || !metaFile.isFile())
                metaFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(metaFile));
            writer.write("{\"current\": \"" + versionsJson.get("latest").getAsString() + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                writer.flush();
                writer.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private static boolean download(String url, String version, File file) {
        url = url.replace(" ", "%20");

        System.out.println("Downloading TGMLib...\n(URL: " + url + ")");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("TGMLib Installer");
        JProgressBar progressBar = new JProgressBar();
        JLabel label = new JLabel("Downloading TGMLib " + version, SwingConstants.CENTER);
        label.setSize(600, 120);
        frame.getContentPane().add(label);
        frame.getContentPane().add(progressBar);
        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(label, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                .addComponent(progressBar, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        frame.setResizable(false);
        progressBar.setBorderPainted(true);
        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);
        Font font = progressBar.getFont();
        progressBar.setFont(new Font(font.getName(), font.getStyle(), font.getSize() * 2));
        label.setFont(new Font(font.getName(), font.getStyle(), font.getSize() * 2));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        HttpURLConnection connection = null;
        try (FileOutputStream fout = new FileOutputStream(file)) {
            URL theUrl = new URL(url);
            connection = (HttpURLConnection) theUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);

            int contentLength = connection.getContentLength();
            byte[] buffer = new byte[1024];

            progressBar.setMaximum(contentLength);

            int read;
            progressBar.setValue(0);
            while ((read = connection.getInputStream().read(buffer)) > 0) {
                fout.write(buffer, 0, read);
                progressBar.setValue(progressBar.getValue() + 1024);
            }
        } catch (Exception e) {
            if (!(e instanceof UnsupportedOperationException))
                e.printStackTrace();
            frame.dispose();
            return false;
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        System.out.println("Finished downloading TGMLib!");

        frame.dispose();
        return true;
    }

    private static void addToClasspath(File file) {
        try {
            URL url = file.toURI().toURL();
            System.out.println(url);

            ClassLoader classLoader = TGMLibInstaller.class.getClassLoader();
            Method method = classLoader.getClass().getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception...", e);
        }
    }

    private static String readJsonFromFile(File file) {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.lines().forEach(builder::append);
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String fetchJson(String url) {
        url = url.replace(" ", "%20");
        HttpURLConnection connection = null;
        try {
            URL theUrl = new URL(url);
            connection = (HttpURLConnection) theUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            return IOUtils.toString(connection.getInputStream(), Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public enum ReturnValue {
        ALREADY_INITIALIZED,
        FAILED,
        ERRORED,
        SUCCESSFUL
    }

}