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

import com.mojang.blaze3d.systems.RenderSystem
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UMatrixStack
import net.minecraft.client.gui.hud.ClientBossBar
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.boss.BossBar
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import xyz.qalcyo.rysm.core.config.RysmConfig
import java.util.*

class BossHealthGui : WindowScreen(restoreCurrentGuiOnClose = true, enableRepeatKeys = true, drawDefaultBackground = false, version = ElementaVersion.V1) {
    private val bossBar = ClientBossBar(
        UUID.fromString("cd899a14-de78-4de8-8d31-9d42fff31d7a"),
        Text.of("Rysm"),
        1.0F,
        BossBar.Color.PURPLE,
        BossBar.Style.NOTCHED_20,
        false,
        false,
        false
    ) //cd899a14-de78-4de8-8d31-9d42fff31d7a is the UUID of EssentialBot which should hopefully never appear ingame
    private val barsTexture = Identifier("textures/gui/bars.png")

    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                RysmConfig.bossBarX = mouseX.toInt()
                RysmConfig.bossBarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                InputUtil.GLFW_KEY_UP -> RysmConfig.bossBarY -= 5
                InputUtil.GLFW_KEY_DOWN -> RysmConfig.bossBarY += 5
                InputUtil.GLFW_KEY_LEFT -> RysmConfig.bossBarX -= 5
                InputUtil.GLFW_KEY_RIGHT -> RysmConfig.bossBarX += 5
            }
        }
        super.initScreen(width, height)
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
        client!!.profiler.push("bossBarGui")
        matrixStack.push()
        val iHaveNoIdeaWhatToNameThisFloat = RysmConfig.bossbarScale.toDouble() - 1.0f
        matrixStack.translate(
            -RysmConfig.bossBarX * iHaveNoIdeaWhatToNameThisFloat / 2,
            -RysmConfig.bossBarY * iHaveNoIdeaWhatToNameThisFloat,
            0.0
        )
        matrixStack.scale(RysmConfig.bossbarScale, RysmConfig.bossbarScale, 1f)
        val i = RysmConfig.bossBarX
        val j = RysmConfig.bossBarY

        val k = i / 2 - 91
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, barsTexture)

        if (RysmConfig.bossBarBar) {
            renderBossBar(matrixStack.toMC(), k, j, bossBar)
        }

        if (RysmConfig.bossBarText) {
            val text = bossBar.name
            val m = client!!.textRenderer.getWidth(text)
            val n = i / 2 - m / 2
            val o = j - 9
            if (RysmConfig.bossBarShadow) {
                client!!.textRenderer.drawWithShadow(matrixStack.toMC(), text, n.toFloat(), o.toFloat(), 16777215)
            } else {
                client!!.textRenderer.draw(matrixStack.toMC(), text, n.toFloat(), o.toFloat(), 16777215)
            }
        }
        client!!.profiler.pop()
        matrixStack.pop()
    }

    private fun renderBossBar(matrices: MatrixStack, x: Int, y: Int, bossBar: BossBar) {
        this.drawTexture(matrices, x, y, 0, bossBar.color.ordinal * 5 * 2, 182, 5)
        if (bossBar.style != BossBar.Style.PROGRESS) {
            this.drawTexture(matrices, x, y, 0, 80 + (bossBar.style.ordinal - 1) * 5 * 2, 182, 5)
        }
        val i = (bossBar.percent * 183.0f).toInt()
        if (i > 0) {
            this.drawTexture(matrices, x, y, 0, bossBar.color.ordinal * 5 * 2 + 5, i, 5)
            if (bossBar.style != BossBar.Style.PROGRESS) {
                this.drawTexture(matrices, x, y, 0, 80 + (bossBar.style.ordinal - 1) * 5 * 2 + 5, i, 5)
            }
        }
    }

    override fun onScreenClose() {
        super.onScreenClose()
        RysmConfig.markDirty()
        RysmConfig.writeData()
    }

}