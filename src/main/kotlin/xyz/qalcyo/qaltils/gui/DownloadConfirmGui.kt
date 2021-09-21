/*
 * Qaltils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Qaltils
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

package xyz.qalcyo.qaltils.gui

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import xyz.qalcyo.qaltils.Qaltils
import xyz.qalcyo.qaltils.utils.Updater
import xyz.qalcyo.qaltils.utils.Updater.shouldUpdate
import xyz.qalcyo.qaltils.utils.Updater.updateUrl
import java.io.File
import kotlin.math.max


class DownloadConfirmGui(private val parent: GuiScreen?) : GuiScreen() {
    override fun initGui() {
        buttonList.add(GuiButton(0, width / 2 - 100, height - 50, 200, 20, EnumChatFormatting.GREEN.toString() + "Yes"))
        buttonList.add(GuiButton(1, width / 2 - 100, height - 28, 200, 20, EnumChatFormatting.RED.toString() + "No"))
        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> {
                mc.displayGuiScreen(parent)
                Multithreading.runAsync {
                    if (Updater.download(
                            updateUrl,
                            File("mods/${Qaltils.MOD_NAME} [1.8.9]-${Updater.latestTag.substringAfter("v")}.jar")
                        ) && Updater.download(
                            "https://github.com/Wyvest/Deleter/releases/download/v1.2/Deleter-1.2.jar",
                            File("config/Wyvest/Deleter-1.2.jar")
                        )
                    ) {
                        EssentialAPI.getNotifications()
                            .push(Qaltils.MOD_NAME, "The ingame updater has successfully installed the newest version.")
                        Updater.addShutdownHook()
                        shouldUpdate = false
                    } else {
                        EssentialAPI.getNotifications().push(
                            Qaltils.MOD_NAME,
                            "The ingame updater has NOT installed the newest version as something went wrong."
                        )
                    }
                }
            }
            1 -> {
                if (parent == null) {
                    mc.displayGuiScreen(parent)
                } else {
                    EssentialAPI.getGuiUtil().openScreen(parent)
                }
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        GlStateManager.pushMatrix()
        GlStateManager.scale(2f, 2f, 0f)
        drawCenteredString(
            fontRendererObj,
            EnumChatFormatting.DARK_PURPLE.toString() + Qaltils.MOD_NAME,
            width / 4,
            3,
            -1
        )
        GlStateManager.popMatrix()
        GlStateManager.pushMatrix()
        GlStateManager.scale(1f, 1f, 0f)
        val lines = listOf(
            "Are you sure you want to update?",
            "You can download it ingame at any time via the configuration screen.",
            "(This will update from v${Qaltils.VERSION} to ${Updater.latestTag})"
        )
        var offset = max(85 - lines.size * 10, 10)

        for (line in lines) {
            drawCenteredString(mc.fontRendererObj, EnumChatFormatting.RED.toString() + line, width / 2, offset, -1)
            offset += mc.fontRendererObj.FONT_HEIGHT + 2
        }
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}