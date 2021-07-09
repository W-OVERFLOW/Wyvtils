package xyz.matthewtgm.lib

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import org.apache.commons.io.IOUtils
import java.awt.Font
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import javax.swing.*


object TGMLibInstaller {
    private val gson = Gson()
    private const val versions_url = "https://raw.githubusercontent.com/TGMDevelopment/TGMLib-Data/main/versions.json"
    private const val className = "xyz.matthewtgm.tgmlib.TGMLib"
    var isLoaded = false
        private set
    private var dataDir: File? = null
    fun isInitialized(): Boolean {
        try {
            Launch.classLoader.clearNegativeEntries(HashSet(Collections.singletonList(className)))
            val invalidClasses = LaunchClassLoader::class.java.getDeclaredField("invalidClasses")
            if (!invalidClasses.isAccessible) invalidClasses.isAccessible = true
            val obj = invalidClasses[Launch.classLoader]
            (obj as MutableSet<*>).remove(className)
            return Class.forName(className) != null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun load(gameDir: File?) {
        if (!isLoaded) return
        try {
            val tgmLib = Class.forName(className)
            val instance = tgmLib.getDeclaredMethod("getInstance")
            println(instance)
            val initialize = tgmLib.getDeclaredMethod("initialize", File::class.java)
            println(initialize)
            initialize.invoke(instance.invoke(null), gameDir)
            println("Loaded TGMLib successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println("Did NOT load TGMLib successfully.")
    }

    fun initialize(gameDir: File?): ReturnValue {
        if (isInitialized()) return ReturnValue.ALREADY_INITIALIZED
        dataDir = File(gameDir, "TGMLib")
        if (!dataDir!!.exists() || !dataDir!!.isDirectory) if (!dataDir!!.mkdirs()) return ReturnValue.FAILED
        val versionsJson = gson.fromJson(
            fetchJson(versions_url),
            JsonObject::class.java
        )
        val metaFile = File(dataDir, "meta.json")
        if (!metaFile.exists() || !metaFile.isFile) createAndUpdateMetaFile(metaFile, versionsJson)
        val localMetadataJson = gson.fromJson(
            readJsonFromFile(File(dataDir, "meta.json")),
            JsonObject::class.java
        )
        val tgmLibFile = File(dataDir, "TGMLib-" + versionsJson["latest"].asString + ".jar")
        if (!tgmLibFile.exists()) {
            val old = File(dataDir, "TGMLib-" + localMetadataJson["current"].asString + ".jar")
            if (old.exists()) if (!old.delete()) return ReturnValue.FAILED
            if (!download(
                    "https://raw.githubusercontent.com/TGMDevelopment/TGMLib-Data/main/downloads/TGMLib-" + versionsJson["latest"].asString + ".jar",
                    versionsJson["latest"].asString,
                    tgmLibFile
                )
            ) return ReturnValue.FAILED
        }
        createAndUpdateMetaFile(metaFile, versionsJson)
        addToClasspath(tgmLibFile)
        isLoaded = true
        return if (!isInitialized()) ReturnValue.FAILED else ReturnValue.SUCCESSFUL
    }

    private fun createAndUpdateMetaFile(metaFile: File, versionsJson: JsonObject) {
        var writer: BufferedWriter? = null
        try {
            if (!metaFile.exists() || !metaFile.isFile) metaFile.createNewFile()
            writer = BufferedWriter(FileWriter(metaFile))
            writer.write("{\"current\": \"" + versionsJson["latest"].asString + "\"}")
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                writer!!.flush()
                writer.close()
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        } finally {
            try {
                writer!!.flush()
                writer.close()
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
    }

    private fun download(url: String, version: String, file: File): Boolean {
        var url = url
        url = url.replace(" ", "%20")
        println("Downloading TGMLib...\n(URL: $url)")
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val frame = JFrame("TGMLib Installer")
        val progressBar = JProgressBar()
        val label = JLabel("Downloading TGMLib $version", SwingConstants.CENTER)
        label.setSize(600, 120)
        frame.contentPane.add(label)
        frame.contentPane.add(progressBar)
        val layout = GroupLayout(frame.contentPane)
        frame.contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(
                                    label,
                                    GroupLayout.Alignment.TRAILING,
                                    GroupLayout.DEFAULT_SIZE,
                                    376,
                                    Short.MAX_VALUE.toInt()
                                )
                                .addComponent(
                                    progressBar,
                                    GroupLayout.Alignment.TRAILING,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    Short.MAX_VALUE.toInt()
                                )
                        )
                        .addContainerGap()
                )
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE.toInt())
                )
        )
        frame.isResizable = false
        progressBar.isBorderPainted = true
        progressBar.minimum = 0
        progressBar.isStringPainted = true
        val font = progressBar.font
        progressBar.font = Font(font.name, font.style, font.size * 2)
        label.font = Font(font.name, font.style, font.size * 2)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
        var connection: HttpURLConnection? = null
        try {
            FileOutputStream(file).use { fout ->
                val theUrl = URL(url)
                connection = theUrl.openConnection() as HttpURLConnection
                connection!!.requestMethod = "GET"
                connection!!.useCaches = true
                connection!!.readTimeout = 20000
                connection!!.connectTimeout = 15000
                connection!!.doOutput = true
                val contentLength = connection!!.contentLength
                val buffer = ByteArray(1024)
                progressBar.maximum = contentLength
                var read: Int
                progressBar.value = 0
                while (connection!!.inputStream.read(buffer).also { read = it } > 0) {
                    fout.write(buffer, 0, read)
                    progressBar.value = progressBar.value + 1024
                }
            }
        } catch (e: Exception) {
            if (e !is UnsupportedOperationException) e.printStackTrace()
            frame.dispose()
            return false
        } finally {
            if (connection != null) connection!!.disconnect()
        }
        println("Finished downloading TGMLib!")
        frame.dispose()
        return true
    }

    private fun addToClasspath(file: File) {
        try {
            val url = file.toURI().toURL()
            println(url)
            val classLoader = TGMLibInstaller::class.java.classLoader
            val method = classLoader.javaClass.getDeclaredMethod("addURL", URL::class.java)
            method.isAccessible = true
            method.invoke(classLoader, url)
        } catch (e: Exception) {
            throw RuntimeException("Unexpected exception...", e)
        }
    }

    private fun readJsonFromFile(file: File): String? {
        return try {
            val builder = StringBuilder()
            val reader = BufferedReader(FileReader(file))
            reader.lines().forEach { str: String? ->
                builder.append(
                    str
                )
            }
            builder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun fetchJson(url: String): String? {
        var url = url
        url = url.replace(" ", "%20")
        var connection: HttpURLConnection? = null
        return try {
            val theUrl = URL(url)
            connection = theUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.useCaches = true
            connection.readTimeout = 20000
            connection.connectTimeout = 15000
            connection.doOutput = true
            IOUtils.toString(connection.inputStream, Charset.defaultCharset())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }

    enum class ReturnValue {
        ALREADY_INITIALIZED, FAILED, ERRORED, SUCCESSFUL
    }
}