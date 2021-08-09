package net.wyvest.wyvtilities.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.boss.BossStatus
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.config.WyvtilsConfig.bossBarX
import net.wyvest.wyvtilities.config.WyvtilsConfig.bossBarY
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