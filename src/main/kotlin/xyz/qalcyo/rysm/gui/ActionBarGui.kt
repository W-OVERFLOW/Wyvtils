/*
 * Rysm, a utility mod for 1.8.9.
 * Copyright (C) 2021 Rysm
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

package xyz.qalcyo.rysm.gui

import gg.essential.api.EssentialAPI
import gg.essential.elementa.WindowScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.rysm.Rysm
import xyz.qalcyo.rysm.config.RysmConfig
import xyz.qalcyo.rysm.config.RysmConfig.actionBarBackground
import xyz.qalcyo.rysm.config.RysmConfig.actionBarBackgroundColor
import xyz.qalcyo.rysm.config.RysmConfig.actionBarPadding
import xyz.qalcyo.rysm.config.RysmConfig.actionBarRound
import xyz.qalcyo.rysm.config.RysmConfig.actionBarRoundRadius
import xyz.qalcyo.rysm.mixin.AccessorGuiIngame
import java.awt.Color

class ActionBarGui : WindowScreen(restoreCurrentGuiOnClose = true, enableRepeatKeys = true, drawDefaultBackground = false) {

    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            println(mouseButton)
            if (mouseButton == 0) {
                RysmConfig.actionBarX = mouseX.toInt()
                RysmConfig.actionBarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                Keyboard.KEY_UP -> RysmConfig.actionBarY -= 5
                Keyboard.KEY_DOWN -> RysmConfig.actionBarY += 5
                Keyboard.KEY_LEFT -> RysmConfig.actionBarX -= 5
                Keyboard.KEY_RIGHT -> RysmConfig.actionBarX += 5
            }
        }
        buttonList.add(GuiButton(0, width / 2 - 50, height - 20, 100, 20, "Close"))
        super.initScreen(width, height)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> EssentialAPI.getGuiUtil().openScreen(RysmConfig.gui())
        }
    }

    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(mouseX, mouseY, partialTicks)
        Rysm.mc.mcProfiler.startSection("actionBarGui")
        val ingameGui = Rysm.mc.ingameGUI as AccessorGuiIngame
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        val width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(
            if (ingameGui.recordPlaying.isNullOrEmpty()) {
                "Rysm Action Bar"
            } else {
                ingameGui.recordPlaying
            }
        )
        if (actionBarBackground) {
            if (actionBarBackgroundColor.alpha != 0) {
                if (actionBarRound) {
                    Requisite.getInstance().renderHelper.drawRoundedRect(
                        RysmConfig.actionBarX - actionBarPadding,
                        RysmConfig.actionBarY - actionBarPadding,
                        width + actionBarPadding,
                        Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + actionBarPadding,
                        actionBarRoundRadius,
                        actionBarBackgroundColor.rgb
                    )
                } else {
                    drawRect(
                        RysmConfig.actionBarX - actionBarPadding,
                        RysmConfig.actionBarY - actionBarPadding,
                        width + RysmConfig.actionBarX + actionBarPadding,
                        Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + RysmConfig.actionBarY + actionBarPadding,
                        actionBarBackgroundColor.rgb
                    )
                }
            }
        }
        Rysm.mc.fontRendererObj.drawString(
            if (ingameGui.recordPlaying.isNullOrEmpty()) {
                "Rysm Action Bar"
            } else {
                ingameGui.recordPlaying
            },
            RysmConfig.actionBarX.toFloat(),
            RysmConfig.actionBarY.toFloat(),
            Color.WHITE.rgb,
            RysmConfig.actionBarShadow
        )
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()

        Rysm.mc.mcProfiler.endSection()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun onScreenClose() {
        super.onScreenClose()
        RysmConfig.markDirty()
        RysmConfig.writeData()
    }

}