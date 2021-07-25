package net.wyvest.wyvtilities.bossbar

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiIngame
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.boss.BossStatus
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.config.WyvtilsConfig


object BossHealth {
    private var mc: Minecraft = Minecraft.getMinecraft()
    private var ingameGui: GuiIngame? = Minecraft.getMinecraft().ingameGUI
    fun renderBossHealth() {
        if ((BossStatus.bossName != null && BossStatus.statusBarTime > 0) || mc.currentScreen != null && mc.currentScreen is BossHealthGui) {
            if (BossStatus.statusBarTime > 0) --BossStatus.statusBarTime
            val fontrenderer: FontRenderer = mc.fontRendererObj
            val scaledresolution = ScaledResolution(mc)
            val i = scaledresolution.scaledWidth
            val j = 182
            val x : Int = if (WyvtilsConfig.firstLaunchBossbar) {
                WyvtilsConfig.firstLaunchBossbar = false
                WyvtilsConfig.bossBarX = i / 2 - j / 2
                WyvtilsConfig.markDirty()
                WyvtilsConfig.writeData()
                i / 2 - j / 2
            } else {
                WyvtilsConfig.bossBarX
            }
            val l = if (mc.currentScreen is BossHealthGui) {
                1
            } else {
                (BossStatus.healthScale * (j + 1).toFloat()).toInt()
            }
            val y = WyvtilsConfig.bossBarY
            val s = if (BossStatus.bossName == null && mc.currentScreen != null && mc.currentScreen is BossHealthGui) {
                "Example Text"
            } else {
                BossStatus.bossName
            }
            val textX = WyvtilsConfig.bossBarX + (j / 2) - (fontrenderer.getStringWidth(s) / 2)
            if (WyvtilsConfig.bossBarBar) {
                ingameGui?.drawTexturedModalRect(x, y, 0, 74, j, 5)
                ingameGui?.drawTexturedModalRect(x, y, 0, 74, j, 5)
                if (l > 0) {
                    ingameGui?.drawTexturedModalRect(x, y, 0, 79, l, 5)
                }
            }
            if (WyvtilsConfig.bossBarText) {
                fontrenderer.drawString(
                    EnumChatFormatting.getTextWithoutFormattingCodes(s),
                    textX.toFloat(), (y - 10).toFloat(),
                    WyvtilsConfig.bossBarColor.rgb, WyvtilsConfig.bossBarShadow
                )
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            if (WyvtilsConfig.bossBarBar) mc.textureManager.bindTexture(Gui.icons)
        }
    }
}