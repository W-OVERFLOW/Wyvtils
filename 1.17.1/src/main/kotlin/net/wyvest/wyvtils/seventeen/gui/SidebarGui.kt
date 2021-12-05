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

package net.wyvest.wyvtils.seventeen.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UMatrixStack
import net.minecraft.client.util.InputUtil
import net.wyvest.wyvtils.core.config.WyvtilsConfig

class SidebarGui : WindowScreen(
    restoreCurrentGuiOnClose = true,
    enableRepeatKeys = true,
    drawDefaultBackground = false,
    version = ElementaVersion.V1
) {

    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                WyvtilsConfig.sidebarX = mouseX.toInt()
                WyvtilsConfig.sidebarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                InputUtil.GLFW_KEY_UP -> WyvtilsConfig.sidebarY -= 5
                InputUtil.GLFW_KEY_DOWN -> WyvtilsConfig.sidebarY += 5
                InputUtil.GLFW_KEY_LEFT -> WyvtilsConfig.sidebarX -= 5
                InputUtil.GLFW_KEY_RIGHT -> WyvtilsConfig.sidebarX += 5
            }
        }
        super.initScreen(width, height)
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
        client!!.profiler.push("sidebarGui")
        matrixStack.push()
        val iHaveNoIdeaWhatToNameThisFloat = WyvtilsConfig.sidebarScale - 1.0f
        matrixStack.translate(
            (-WyvtilsConfig.sidebarX * iHaveNoIdeaWhatToNameThisFloat).toDouble(),
            (-WyvtilsConfig.sidebarY * iHaveNoIdeaWhatToNameThisFloat).toDouble(),
            0.0
        )
        matrixStack.scale(WyvtilsConfig.sidebarScale, WyvtilsConfig.sidebarScale, 1.0f)
        val x = WyvtilsConfig.sidebarX - textRenderer.getWidth("WYVTILSS!!!") - 3
        val y = WyvtilsConfig.sidebarY
        var p = 0
        val list = listOf("ok", "Wyvest", "WYVTILSS!!!")
        for (s in list) {
            ++p
            val t: Int = y - p * 9
            val var10001: Int = x - 2
            if (WyvtilsConfig.sidebarBackground) fill(
                matrixStack.toMC(),
                var10001,
                t,
                WyvtilsConfig.sidebarX - 1,
                t + 9,
                WyvtilsConfig.sidebarBackgroundColor.rgb
            )
            if (WyvtilsConfig.sidebarTextShadow) {
                textRenderer.drawWithShadow(matrixStack.toMC(), s, x.toFloat(), t.toFloat(), -1)
            } else {
                textRenderer.draw(matrixStack.toMC(), s, x.toFloat(), t.toFloat(), -1)
            }
            if (p != 3 && WyvtilsConfig.sidebarScorePoints) {
                if (WyvtilsConfig.sidebarTextShadow) {
                    textRenderer.drawWithShadow(
                        matrixStack.toMC(),
                        p.toString(),
                        (WyvtilsConfig.sidebarX - 1 - textRenderer.getWidth(p.toString())).toFloat(),
                        t.toFloat(),
                        -1
                    )
                } else {
                    textRenderer.draw(
                        matrixStack.toMC(),
                        p.toString(),
                        (WyvtilsConfig.sidebarX - 1 - textRenderer.getWidth(p.toString())).toFloat(),
                        t.toFloat(),
                        -1
                    )
                }
            }
        }
        matrixStack.pop()

        client!!.profiler.pop()
    }

    override fun onScreenClose() {
        super.onScreenClose()
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
    }

}