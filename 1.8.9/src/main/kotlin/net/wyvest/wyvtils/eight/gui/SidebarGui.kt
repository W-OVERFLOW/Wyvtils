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
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtils.core.config.WyvtilsConfig
import net.wyvest.wyvtils.core.config.WyvtilsConfig.sidebarScale
import net.wyvest.wyvtils.core.config.WyvtilsConfig.sidebarX
import net.wyvest.wyvtils.core.config.WyvtilsConfig.sidebarY
import org.lwjgl.input.Keyboard

class SidebarGui : WindowScreen(restoreCurrentGuiOnClose = true, enableRepeatKeys = true, drawDefaultBackground = false) {

    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                sidebarX = mouseX.toInt()
                sidebarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                Keyboard.KEY_UP -> sidebarY -= 5
                Keyboard.KEY_DOWN -> sidebarY += 5
                Keyboard.KEY_LEFT -> sidebarX -= 5
                Keyboard.KEY_RIGHT -> sidebarX += 5
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

    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(mouseX, mouseY, partialTicks)
        mc.mcProfiler.startSection("sidebarGui")
        GlStateManager.pushMatrix()
        val mscale = sidebarScale - 1.0f
        GlStateManager.translate(-sidebarX * mscale, -sidebarY * mscale, 0.0f)
        GlStateManager.scale(sidebarScale, sidebarScale, 1.0f)
        val i: Int = fontRendererObj.getStringWidth("Wyvtils!!!!!!!!")

        val j1: Int = sidebarY
        val l1: Int = sidebarX - i
        var j = 0

        val list = listOf("ok", "Wyvest")

        for (score1 in list) {
            ++j
            val k: Int = j1 - j * this.fontRendererObj.FONT_HEIGHT
            val l = sidebarX
            drawRect(l1 - 2, k, l, k + fontRendererObj.FONT_HEIGHT, WyvtilsConfig.sidebarBackgroundColor.rgb)
            fontRendererObj.drawString(
                list[j - 1],
                l1.toFloat(),
                k.toFloat(),
                553648127,
                WyvtilsConfig.sidebarTextShadow
            )
            if (WyvtilsConfig.sidebarScorePoints) {
                fontRendererObj.drawString(
                    EnumChatFormatting.RED.toString() + j.toString(),
                    (l - fontRendererObj.getStringWidth(j.toString())).toFloat(), k.toFloat(), 553648127,
                    WyvtilsConfig.sidebarTextShadow
                )
            }

            if (j == list.size) {
                val s3 = "Wyvtils!!!!!!!!"
                fontRendererObj.drawString(
                    s3,
                    (l1 + i / 2 - fontRendererObj.getStringWidth(s3) / 2).toFloat(),
                    (k - fontRendererObj.FONT_HEIGHT).toFloat(),
                    553648127, WyvtilsConfig.sidebarTextShadow
                )
            }
        }
        /*/
        if (backgroundBorder) {
            Requisite.getInstance().renderHelper.drawHollowRect(
                sidebarX - i - 2 - borderNumber,
                sidebarY - 4 * fontRendererObj.FONT_HEIGHT - 1 - borderNumber,
                i + borderNumber * 2 + 2,
                4 * fontRendererObj.FONT_HEIGHT + 1 + borderNumber * 2,
                borderNumber,
                borderColor.rgb
            )
        }

         */
        GlStateManager.popMatrix()
        mc.mcProfiler.endSection()
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