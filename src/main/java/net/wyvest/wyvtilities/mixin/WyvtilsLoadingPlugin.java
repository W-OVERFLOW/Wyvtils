/*
 * Wyvtilities - Utilities for Hypixel 1.8.9.
 * Copyright (C) 2021 Wyvtilities
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.wyvest.wyvtilities.mixin;

import kotlin.KotlinVersion;
import kotlin.text.StringsKt;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Adapted from Skytils under AGPLv3
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
public class WyvtilsLoadingPlugin implements IFMLLoadingPlugin {

    public WyvtilsLoadingPlugin() throws URISyntaxException {
        if (!KotlinVersion.CURRENT.isAtLeast(1, 5, 0)) {
            final File file = new File(KotlinVersion.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File realFile = file;
            for (int i = 0; i < 5; i++) {
                if (realFile == null) {
                    realFile = file;
                    break;
                }
                if (!realFile.getName().endsWith(".jar!") && !realFile.getName().endsWith(".jar")) {
                    realFile = realFile.getParentFile();
                } else break;
            }

            String name = realFile.getName().contains(".jar") ? realFile.getName() : StringsKt.substringAfterLast(StringsKt.substringBeforeLast(file.getAbsolutePath(), ".jar", "unknown"), "/", "Unknown");

            if (name.endsWith("!")) name = name.substring(0, name.length() - 1);
            showMessage(name);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private void showMessage(String file) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // This makes the JOptionPane show on taskbar and stay on top
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        JButton discordLink = new JButton("Join the Discord");
        discordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI("https://discord.gg/b6twapxC3T"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        JButton close = new JButton("Close");
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                exit();
            }
        });

        Object[] options = new Object[]{discordLink, close};
        JOptionPane.showOptionDialog(
                frame,
                "<html><p>Wyvtilities has detected a mod with an older version of Kotlin.<br>The culprit is " + file + ".<br>It packages version " + KotlinVersion.CURRENT + ".<br>In order to resolve this conflict you must make Wyvtilities be<br>above this mod alphabetically in your mods folder.<br>This tricks Forge into loading Wyvtilities first.<br>You can do this by renaming your Wyvtilities jar to !Wyvtilities.jar,<br>or by renaming the other mod's jar to start with a Z.<br>If you have already done this and are still getting this error,<br>ask for support in the Discord.</p></html>",
                "Wyvtilities Error",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]
        );
        exit();
    }

    /**
     * Bypasses forges security manager to exit the jvm
     */
    private void exit() {
        try {
            Class<?> clazz = Class.forName("java.lang.Shutdown");
            Method m_exit = clazz.getDeclaredMethod("exit", int.class);
            m_exit.setAccessible(true);
            m_exit.invoke(null, 0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
