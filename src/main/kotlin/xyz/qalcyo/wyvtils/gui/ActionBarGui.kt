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

package xyz.qalcyo.wyvtils.gui

import gg.essential.api.EssentialAPI
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UMatrixStack
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import xyz.qalcyo.wyvtils.Wyvtils
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.mixin.AccessorGuiIngame
import java.awt.Color

class ActionBarGui : WindowScreen(restoreCurrentGuiOnClose = true, enableRepeatKeys = true) {

    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 100) {
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
        super.initScreen(width, height)
        buttonList.add(GuiButton(0, width / 2 - 50, height - 20, 100, 20, "Close"))
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
        }
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
        Wyvtils.mc.mcProfiler.startSection("actionBarGui")
        val ingameGui = Wyvtils.mc.ingameGUI as AccessorGuiIngame
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
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