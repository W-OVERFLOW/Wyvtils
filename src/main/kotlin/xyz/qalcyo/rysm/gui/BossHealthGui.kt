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

package xyz.qalcyo.rysm.gui

import gg.essential.api.EssentialAPI
import gg.essential.elementa.WindowScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.boss.BossStatus
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import xyz.qalcyo.rysm.Rysm
import xyz.qalcyo.rysm.config.RysmConfig
import java.awt.Color


class BossHealthGui : WindowScreen(restoreCurrentGuiOnClose = true, enableRepeatKeys = true, drawDefaultBackground = false) {
    override fun initScreen(width: Int, height: Int) {
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                RysmConfig.bossBarX = mouseX.toInt()
                RysmConfig.bossBarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                Keyboard.KEY_UP -> RysmConfig.bossBarY -= 5
                Keyboard.KEY_DOWN -> RysmConfig.bossBarY += 5
                Keyboard.KEY_LEFT -> RysmConfig.bossBarX -= 5
                Keyboard.KEY_RIGHT -> RysmConfig.bossBarX += 5
            }
        }
        super.initScreen(width, height)
        buttonList.add(GuiButton(0, width / 2 - 50, height - 20, 100, 20, "Close"))
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> EssentialAPI.getGuiUtil().openScreen(RysmConfig.gui())
        }
    }

    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(mouseX, mouseY, partialTicks)
        Rysm.mc.textureManager.bindTexture(icons)
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        Rysm.mc.mcProfiler.startSection("bossHealthGui")
        GlStateManager.pushMatrix()
        val iHaveNoIdeaWhatToNameThisFloat = RysmConfig.bossbarScale - 1.0f
        GlStateManager.translate(
            -RysmConfig.bossBarX * iHaveNoIdeaWhatToNameThisFloat,
            -RysmConfig.bossBarY * iHaveNoIdeaWhatToNameThisFloat,
            0.0f
        )
        GlStateManager.scale(RysmConfig.bossbarScale, RysmConfig.bossbarScale, 1.0f)
        GlStateManager.enableBlend()
        val fontrenderer: FontRenderer = Rysm.mc.fontRendererObj
        if (RysmConfig.firstLaunchBossbar) {
            RysmConfig.firstLaunchBossbar = false
            RysmConfig.bossBarX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2
            RysmConfig.bossBarY = 12
            RysmConfig.markDirty()
            RysmConfig.writeData()
        }
        val s = if (BossStatus.bossName == null && Rysm.mc.currentScreen != null) {
            "Example Text"
        } else {
            BossStatus.bossName
        }
        if (RysmConfig.bossBarBar) {
            Rysm.mc.ingameGUI?.drawTexturedModalRect(RysmConfig.bossBarX - 91, RysmConfig.bossBarY, 0, 74, 182, 5)
            Rysm.mc.ingameGUI?.drawTexturedModalRect(RysmConfig.bossBarX - 91, RysmConfig.bossBarY, 0, 74, 182, 5)
            Rysm.mc.ingameGUI?.drawTexturedModalRect(RysmConfig.bossBarX - 91, RysmConfig.bossBarY, 0, 79, 1, 5)
        }
        if (RysmConfig.bossBarText) {
            fontrenderer.drawString(
                s,
                (RysmConfig.bossBarX - Rysm.mc.fontRendererObj.getStringWidth(s) / 2).toString().toFloat(),
                RysmConfig.bossBarY.toFloat() - 10,
                Color.WHITE.rgb, RysmConfig.bossBarShadow
            )
        }
        if (RysmConfig.bossBarBar) Rysm.mc.textureManager.bindTexture(Gui.icons)
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
        Rysm.mc.mcProfiler.endSection()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun onScreenClose() {
        super.onScreenClose()
        RysmConfig.markDirty()
        RysmConfig.writeData()
    }

}