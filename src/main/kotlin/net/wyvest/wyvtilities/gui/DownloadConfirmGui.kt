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

package net.wyvest.wyvtilities.gui

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.utils.Updater
import net.wyvest.wyvtilities.utils.Updater.shouldUpdate
import net.wyvest.wyvtilities.utils.Updater.updateUrl
import java.io.File
import kotlin.math.max


class DownloadConfirmGui : GuiScreen() {
    override fun initGui() {
        buttonList.add(GuiButton(0, width / 2 - 100, height - 50, 200, 20, EnumChatFormatting.GREEN.toString() + "Yes"))
        buttonList.add(GuiButton(1, width / 2 - 100, height - 28, 200, 20, EnumChatFormatting.RED.toString() + "No"))
        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> {
                mc.displayGuiScreen(GuiMainMenu())
                Multithreading.runAsync {
                    if (Updater.download(updateUrl, File("mods/Wyvtilities-${Updater.latestTag.substringAfter("v")}.jar")) && Updater.download("https://cdn.discordapp.com/attachments/864029986066137109/874835023268835408/WyvtilitiesDeleter-1.1.jar", File("config/Wyvtilities/WyvtilitiesDeleter.jar"))) {
                        EssentialAPI.getNotifications().push("Wyvtilities", "The ingame updater has successfully installed the newest version.")
                        Updater.addShutdownHook()
                        shouldUpdate = false
                    } else {
                        EssentialAPI.getNotifications().push("Wyvtilities", "The ingame updater has NOT installed the newest version as something went wrong.")
                    }
                }
            }
            1 -> mc.displayGuiScreen(GuiMainMenu())
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        GlStateManager.pushMatrix()
        var scale = 3
        GlStateManager.scale(scale.toFloat(), scale.toFloat(), 0f)
        drawCenteredString(
            fontRendererObj,
            EnumChatFormatting.DARK_PURPLE.toString() + "Wyvtilities",
            width / 2 / scale,
            5 / scale,
            -1
        )
        GlStateManager.popMatrix()
        scale = 1
        GlStateManager.pushMatrix()
        GlStateManager.scale(scale.toFloat(), scale.toFloat(), 0f)
        val lines = listOf("Are you sure you want to update?", "You can download it ingame at any time via the configuration screen.", "(This will update from v${Wyvtilities.VERSION} to ${Updater.latestTag})")
        var offset = max(85 - lines.size * 10, 10)

        for (line in lines) {
            drawCenteredString(mc.fontRendererObj, EnumChatFormatting.RED.toString() + line, width / 2, offset, -1)
            offset += mc.fontRendererObj.FONT_HEIGHT + 2
        }
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}