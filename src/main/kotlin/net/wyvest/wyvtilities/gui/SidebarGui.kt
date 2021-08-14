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

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.config.WyvtilsConfig.sidebarScale
import net.wyvest.wyvtilities.config.WyvtilsConfig.sidebarX
import net.wyvest.wyvtilities.config.WyvtilsConfig.sidebarY
import org.lwjgl.input.Keyboard
import xyz.matthewtgm.requisite.util.GuiHelper
import java.io.IOException

class SidebarGui : GuiScreen() {

    private var dragging = false
    private var prevX = 0
    private var prevY = 0

    override fun initGui() {
        buttonList.add(GuiButton(0, width / 2 - 50, height - 20, 100, 20, "Close"))
        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> GuiHelper.open(WyvtilsConfig.gui())
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        updatePos(mouseX, mouseY)
        mc.mcProfiler.startSection("sidebarGui")
        GlStateManager.pushMatrix()
        val mscale = sidebarScale - 1.0f
        GlStateManager.translate(-sidebarX * mscale, -sidebarY * mscale, 0.0f)
        GlStateManager.scale(sidebarScale, sidebarScale, 1.0f)

        val i: Int = fontRendererObj.getStringWidth("Wyvtilities")

        val j1: Int = sidebarY
        val l1: Int = sidebarX - i
        var j = 0

        val list = listOf("ok", "Wyvest")

        for (score1 in list) {
            ++j
            val k: Int = j1 - j * this.fontRendererObj.FONT_HEIGHT
            val l = sidebarX
            drawRect(l1 - 2, k, l, k + fontRendererObj.FONT_HEIGHT, WyvtilsConfig.sidebarBackgroundColor.rgb)
            fontRendererObj.drawString(list[j - 1], l1.toFloat(), k.toFloat(), 553648127, WyvtilsConfig.sidebarTextShadow)
            if (WyvtilsConfig.sidebarScorePoints) {
                fontRendererObj.drawString(EnumChatFormatting.RED.toString() + j.toString(),
                    (l - fontRendererObj.getStringWidth(j.toString())).toFloat(), k.toFloat(), 553648127,
                    WyvtilsConfig.sidebarTextShadow)
            }

            if (j == list.size) {
                val s3 = "Wyvtilities"
                drawRect(l1 - 2, k - fontRendererObj.FONT_HEIGHT - 1, l, k - 1, WyvtilsConfig.sidebarBackgroundColor.rgb)
                drawRect(l1 - 2, k - 1, l, k, WyvtilsConfig.sidebarBackgroundColor.rgb)
                fontRendererObj.drawString(
                    s3,
                    (l1 + i / 2 - fontRendererObj.getStringWidth(s3) / 2).toFloat(),
                    (k - fontRendererObj.FONT_HEIGHT).toFloat(),
                    553648127, WyvtilsConfig.sidebarTextShadow
                )
            }
        }
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
            Keyboard.KEY_UP -> sidebarY -= 5
            Keyboard.KEY_DOWN -> sidebarY += 5
            Keyboard.KEY_LEFT -> sidebarX -= 5
            Keyboard.KEY_RIGHT -> sidebarX += 5
        }
    }

    private fun updatePos(x: Int, y: Int) {
        if (dragging) {
            sidebarX = prevX
            sidebarY = prevY
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
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
        super.onGuiClosed()
    }

}