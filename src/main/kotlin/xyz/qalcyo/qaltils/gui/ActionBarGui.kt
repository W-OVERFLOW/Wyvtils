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

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import xyz.qalcyo.qaltils.config.QaltilsConfig
import xyz.qalcyo.qaltils.config.QaltilsConfig.actionBarX
import xyz.qalcyo.qaltils.config.QaltilsConfig.actionBarY
import xyz.qalcyo.qaltils.mixin.AccessorGuiIngame
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import xyz.matthewtgm.requisite.util.GuiHelper
import java.awt.Color
import java.io.IOException

class ActionBarGui : GuiScreen() {

    private var dragging = false
    private var prevX = 0
    private var prevY = 0

    override fun initGui() {
        buttonList.add(GuiButton(0, width / 2 - 50, height - 20, 100, 20, "Close"))
        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> GuiHelper.open(QaltilsConfig.gui())
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        updatePos(mouseX, mouseY)
        mc.mcProfiler.startSection("actionBarGui")
        val ingameGui = mc.ingameGUI as AccessorGuiIngame
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        mc.fontRendererObj.drawString(
            if (ingameGui.recordPlaying.isNullOrEmpty()) {
                "Qaltils Action Bar"
            } else {
                ingameGui.recordPlaying
            },
            actionBarX.toFloat(),
            actionBarY.toFloat(),
            Color.WHITE.rgb,
            QaltilsConfig.actionBarShadow
        )
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()

        mc.mcProfiler.endSection()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        prevX = mouseX
        prevY = mouseY
        if (mouseButton == 0) {
            dragging = true
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        when (keyCode) {
            Keyboard.KEY_UP -> actionBarY -= 5
            Keyboard.KEY_DOWN -> actionBarY += 5
            Keyboard.KEY_LEFT -> actionBarX -= 5
            Keyboard.KEY_RIGHT -> actionBarX += 5
        }
    }

    private fun updatePos(x: Int, y: Int) {
        if (dragging) {
            actionBarX = prevX
            actionBarY = prevY
        }
        prevX = x
        prevY = y
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        dragging = false
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun onGuiClosed() {
        QaltilsConfig.markDirty()
        QaltilsConfig.writeData()
        super.onGuiClosed()
    }

}