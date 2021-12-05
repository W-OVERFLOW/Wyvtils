/*
 * Wyvtils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Wyvtils
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

package net.wyvest.wyvtils.eight.gui

import gg.essential.api.EssentialAPI
import gg.essential.elementa.WindowScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.wyvest.wyvtils.core.config.WyvtilsConfig
import net.wyvest.wyvtils.core.config.WyvtilsConfig.actionBarBackground
import net.wyvest.wyvtils.core.config.WyvtilsConfig.actionBarBackgroundColor
import net.wyvest.wyvtils.core.config.WyvtilsConfig.actionBarPadding
import net.wyvest.wyvtils.eight.Wyvtils
import net.wyvest.wyvtils.eight.mixin.hud.GuiIngameAccessor
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class ActionBarGui :
    WindowScreen(restoreCurrentGuiOnClose = true, enableRepeatKeys = true, drawDefaultBackground = false) {

    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                WyvtilsConfig.actionBarX = mouseX.toInt()
                WyvtilsConfig.actionBarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                Keyboard.KEY_UP -> WyvtilsConfig.actionBarY -= 5
                Keyboard.KEY_DOWN -> WyvtilsConfig.actionBarY += 5
                Keyboard.KEY_LEFT -> WyvtilsConfig.actionBarX -= 5
                Keyboard.KEY_RIGHT -> WyvtilsConfig.actionBarX += 5
            }
        }
        buttonList.add(GuiButton(0, width / 2 - 50, height - 20, 100, 20, "Close"))
        super.initScreen(width, height)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
        }
    }

    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(mouseX, mouseY, partialTicks)
        Wyvtils.mc.mcProfiler.startSection("actionBarGui")
        val ingameGui = Wyvtils.mc.ingameGUI as GuiIngameAccessor
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        val width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(
            if (ingameGui.recordPlaying.isNullOrEmpty()) {
                "Wyvtils Action Bar"
            } else {
                ingameGui.recordPlaying
            }
        )
        if (actionBarBackground) {
            if (actionBarBackgroundColor.alpha != 0) {
                drawRect(
                    WyvtilsConfig.actionBarX - actionBarPadding,
                    WyvtilsConfig.actionBarY - actionBarPadding,
                    width + WyvtilsConfig.actionBarX + actionBarPadding,
                    Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + WyvtilsConfig.actionBarY + actionBarPadding,
                    actionBarBackgroundColor.rgb
                )
            }
        }
        Wyvtils.mc.fontRendererObj.drawString(
            if (ingameGui.recordPlaying.isNullOrEmpty()) {
                "Wyvtils Action Bar"
            } else {
                ingameGui.recordPlaying
            },
            WyvtilsConfig.actionBarX.toFloat(),
            WyvtilsConfig.actionBarY.toFloat(),
            Color.WHITE.rgb,
            WyvtilsConfig.actionBarShadow
        )
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()

        Wyvtils.mc.mcProfiler.endSection()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun onScreenClose() {
        super.onScreenClose()
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
    }

}