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

package xyz.qalcyo.wyvtils.seventeen.gui

import com.mojang.blaze3d.systems.RenderSystem
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UMatrixStack
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.hud.BackgroundHelper
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig.actionBarX
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig.actionBarY
import java.awt.Color
import java.util.*

class ActionBarGui : WindowScreen(version = ElementaVersion.V1, restoreCurrentGuiOnClose = true) {
    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                actionBarX = mouseX.toInt()
                actionBarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                InputUtil.GLFW_KEY_UP -> actionBarY -= 5
                InputUtil.GLFW_KEY_DOWN -> actionBarY += 5
                InputUtil.GLFW_KEY_LEFT -> actionBarX -= 5
                InputUtil.GLFW_KEY_RIGHT -> actionBarX += 5
            }
        }
    }
    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
        client!!.profiler.push("overlayGuiMessage")
        matrixStack.push()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        this.drawTextBackground(matrixStack.toMC(), textRenderer)
        textRenderer.draw(
            matrixStack.toMC(),
            "Wyvtils Action Bar",
            actionBarX.toFloat(),
            actionBarY.toFloat(),
            Color.WHITE.rgb
        )
        RenderSystem.disableBlend()
        matrixStack.pop()

        client!!.profiler.pop()
    }


    override fun onClose() {
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
        super.onClose()
    }

    private fun drawTextBackground(
        matrices: MatrixStack,
        textRenderer: TextRenderer
    ) {
        val i = client!!.options.getTextBackgroundColor(0.0f)
        if (i != 0) {
            val j = 2
            val var10001 = j - 2
            val var10002 = -6
            val var10003 = j - 2
            Objects.requireNonNull(textRenderer)
            fill(
                matrices,
                var10001,
                var10002,
                var10003,
                7,
                BackgroundHelper.ColorMixer.mixColor(i, Color.WHITE.rgb)
            )
        }
    }
}