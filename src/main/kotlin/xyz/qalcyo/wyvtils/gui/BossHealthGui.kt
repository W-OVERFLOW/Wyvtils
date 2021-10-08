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
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.boss.BossStatus
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import xyz.qalcyo.wyvtils.Wyvtils
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import java.awt.Color


class BossHealthGui : WindowScreen(restoreCurrentGuiOnClose = true, enableRepeatKeys = true) {
    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 100) {
                WyvtilsConfig.bossBarX = mouseX.toInt()
                WyvtilsConfig.bossBarX = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                Keyboard.KEY_UP -> WyvtilsConfig.bossBarX -= 5
                Keyboard.KEY_DOWN -> WyvtilsConfig.bossBarX += 5
                Keyboard.KEY_LEFT -> WyvtilsConfig.bossBarX -= 5
                Keyboard.KEY_RIGHT -> WyvtilsConfig.bossBarX += 5
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
        Wyvtils.mc.textureManager.bindTexture(icons)
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        Wyvtils.mc.mcProfiler.startSection("bossHealthGui")
        GlStateManager.pushMatrix()
        val iHaveNoIdeaWhatToNameThisFloat = WyvtilsConfig.bossbarScale - 1.0f
        GlStateManager.translate(
            -WyvtilsConfig.bossBarX * iHaveNoIdeaWhatToNameThisFloat,
            -WyvtilsConfig.bossBarY * iHaveNoIdeaWhatToNameThisFloat,
            0.0f
        )
        GlStateManager.scale(WyvtilsConfig.bossbarScale, WyvtilsConfig.bossbarScale, 1.0f)
        GlStateManager.enableBlend()
        val fontrenderer: FontRenderer = Wyvtils.mc.fontRendererObj
        if (WyvtilsConfig.firstLaunchBossbar) {
            WyvtilsConfig.firstLaunchBossbar = false
            WyvtilsConfig.bossBarX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2
            WyvtilsConfig.bossBarY = 12
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
        }
        val s = if (BossStatus.bossName == null && Wyvtils.mc.currentScreen != null) {
            "Example Text"
        } else {
            BossStatus.bossName
        }
        if (WyvtilsConfig.bossBarBar) {
            Wyvtils.mc.ingameGUI?.drawTexturedModalRect(WyvtilsConfig.bossBarX - 91, WyvtilsConfig.bossBarY, 0, 74, 182, 5)
            Wyvtils.mc.ingameGUI?.drawTexturedModalRect(WyvtilsConfig.bossBarX - 91, WyvtilsConfig.bossBarY, 0, 74, 182, 5)
            Wyvtils.mc.ingameGUI?.drawTexturedModalRect(WyvtilsConfig.bossBarX - 91, WyvtilsConfig.bossBarY, 0, 79, 1, 5)
        }
        if (WyvtilsConfig.bossBarText) {
            fontrenderer.drawString(
                s,
                (WyvtilsConfig.bossBarX - Wyvtils.mc.fontRendererObj.getStringWidth(s) / 2).toString().toFloat(),
                WyvtilsConfig.bossBarY.toFloat() - 10,
                Color.WHITE.rgb, WyvtilsConfig.bossBarShadow
            )
        }
        if (WyvtilsConfig.bossBarBar) Wyvtils.mc.textureManager.bindTexture(Gui.icons)
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