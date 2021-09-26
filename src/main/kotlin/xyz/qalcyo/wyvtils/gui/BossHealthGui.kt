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

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.boss.BossStatus
import net.minecraft.util.EnumChatFormatting
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.config.WyvtilsConfig.bossBarX
import xyz.qalcyo.wyvtils.config.WyvtilsConfig.bossBarY
import xyz.qalcyo.wyvtils.config.WyvtilsConfig.bossbarScale
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import xyz.matthewtgm.requisite.util.GuiHelper
import java.awt.Color
import java.io.IOException


class BossHealthGui : GuiScreen() {

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
        mc.textureManager.bindTexture(icons)
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        mc.mcProfiler.startSection("bossHealthGui")
        GlStateManager.pushMatrix()
        val iHaveNoIdeaWhatToNameThisFloat = bossbarScale - 1.0f
        GlStateManager.translate(
            -bossBarX * iHaveNoIdeaWhatToNameThisFloat,
            -bossBarY * iHaveNoIdeaWhatToNameThisFloat,
            0.0f
        )
        GlStateManager.scale(bossbarScale, bossbarScale, 1.0f)
        GlStateManager.enableBlend()
        val fontrenderer: FontRenderer = mc.fontRendererObj
        if (WyvtilsConfig.firstLaunchBossbar) {
            WyvtilsConfig.firstLaunchBossbar = false
            bossBarX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2
            bossBarY = 12
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
        }
        val s = if (BossStatus.bossName == null && mc.currentScreen != null) {
            "Example Text"
        } else {
            BossStatus.bossName
        }
        if (WyvtilsConfig.bossBarBar) {
            mc.ingameGUI?.drawTexturedModalRect(bossBarX - 91, bossBarY, 0, 74, 182, 5)
            mc.ingameGUI?.drawTexturedModalRect(bossBarX - 91, bossBarY, 0, 74, 182, 5)
            mc.ingameGUI?.drawTexturedModalRect(bossBarX - 91, bossBarY, 0, 79, 1, 5)
        }
        if (WyvtilsConfig.bossBarText) {
            fontrenderer.drawString(
                s,
                (bossBarX - mc.fontRendererObj.getStringWidth(s) / 2).toString().toFloat(),
                bossBarY.toFloat() - 10,
                Color.WHITE.rgb, WyvtilsConfig.bossBarShadow
            )
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        if (WyvtilsConfig.bossBarBar) mc.textureManager.bindTexture(Gui.icons)
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
        mc.mcProfiler.endSection()
        val scale = 1
        GlStateManager.pushMatrix()
        GlStateManager.scale(scale.toFloat(), scale.toFloat(), 0f)
        drawCenteredString(
            fontRendererObj,
            EnumChatFormatting.WHITE.toString() + "(drag bossbar to edit position!)",
            width / 2 / scale,
            5 / scale + 55,
            -1
        )
        GlStateManager.popMatrix()
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
            Keyboard.KEY_UP -> bossBarY -= 5
            Keyboard.KEY_DOWN -> bossBarY += 5
            Keyboard.KEY_LEFT -> bossBarX -= 5
            Keyboard.KEY_RIGHT -> bossBarX += 5
        }
    }

    private fun updatePos(x: Int, y: Int) {
        if (dragging) {
            bossBarX = prevX
            bossBarY = prevY
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