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

package xyz.qalcyo.rysm.seventeen.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UMatrixStack
import net.minecraft.client.util.InputUtil
import xyz.qalcyo.rysm.core.config.RysmConfig

class SidebarGui : WindowScreen(restoreCurrentGuiOnClose = true, enableRepeatKeys = true, drawDefaultBackground = false, version = ElementaVersion.V1) {

    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                RysmConfig.sidebarX = mouseX.toInt()
                RysmConfig.sidebarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                InputUtil.GLFW_KEY_UP -> RysmConfig.sidebarY -= 5
                InputUtil.GLFW_KEY_DOWN -> RysmConfig.sidebarY += 5
                InputUtil.GLFW_KEY_LEFT -> RysmConfig.sidebarX -= 5
                InputUtil.GLFW_KEY_RIGHT -> RysmConfig.sidebarX += 5
            }
        }
        super.initScreen(width, height)
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
        client!!.profiler.push("sidebarGui")
        matrixStack.push()
        val iHaveNoIdeaWhatToNameThisFloat = RysmConfig.sidebarScale - 1.0f
        matrixStack.translate(
            (-RysmConfig.sidebarX * iHaveNoIdeaWhatToNameThisFloat).toDouble(),
            (-RysmConfig.sidebarY * iHaveNoIdeaWhatToNameThisFloat).toDouble(),
            0.0
        )
        matrixStack.scale(RysmConfig.sidebarScale, RysmConfig.sidebarScale, 1.0f)
        val x = RysmConfig.sidebarX - textRenderer.getWidth("Qalcyo!!!") - 3
        val y = RysmConfig.sidebarY
        var p = 0
        val list = listOf("ok", "Wyvest", "Qalcyo!!!")
        for (s in list) {
            ++p
            val t: Int = y - p * 9
            val var10001: Int = x - 2
            if (RysmConfig.sidebarBackground) fill(
                matrixStack.toMC(),
                var10001,
                t,
                RysmConfig.sidebarX - 1,
                t + 9,
                RysmConfig.sidebarBackgroundColor.rgb
            )
            if (RysmConfig.sidebarTextShadow) {
                textRenderer.drawWithShadow(matrixStack.toMC(), s, x.toFloat(), t.toFloat(), -1)
            } else {
                textRenderer.draw(matrixStack.toMC(), s, x.toFloat(), t.toFloat(), -1)
            }
            if (p != 3 && RysmConfig.sidebarScorePoints) {
                if (RysmConfig.sidebarTextShadow) {
                    textRenderer.drawWithShadow(
                        matrixStack.toMC(),
                        p.toString(),
                        (RysmConfig.sidebarX - 1 - textRenderer.getWidth(p.toString())).toFloat(),
                        t.toFloat(),
                        -1
                    )
                } else {
                    textRenderer.draw(
                        matrixStack.toMC(),
                        p.toString(),
                        (RysmConfig.sidebarX - 1 - textRenderer.getWidth(p.toString())).toFloat(),
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
        RysmConfig.markDirty()
        RysmConfig.writeData()
    }

}