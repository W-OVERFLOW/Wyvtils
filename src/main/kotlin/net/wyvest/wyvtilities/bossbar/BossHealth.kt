package net.wyvest.wyvtilities.bossbar

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiIngame
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.boss.BossStatus
import net.wyvest.wyvtilities.config.WyvtilsConfig

object BossHealth {
    // bad practice - this is completely overwriting boss health rendering, will be redone in the future
    private var mc: Minecraft = Minecraft.getMinecraft()
    private var ingameGui: GuiIngame = Minecraft.getMinecraft().ingameGUI
    fun renderBossHealth() {
        if ((BossStatus.bossName != null && BossStatus.statusBarTime > 0) || mc.currentScreen is BossHealthGui) {
            --BossStatus.statusBarTime
            val fontrenderer: FontRenderer = mc.fontRendererObj
            val scaledresolution = ScaledResolution(mc)
            val i = scaledresolution.scaledWidth
            val j = 182
            val x : Int = if (WyvtilsConfig.firstLaunch) {
                WyvtilsConfig.firstLaunch = false
                WyvtilsConfig.bossBarX = i / 2 - j / 2
                WyvtilsConfig.markDirty()
                WyvtilsConfig.writeData()
                i / 2 - j / 2
            } else {
                WyvtilsConfig.bossBarX
            }
            val l = (BossStatus.healthScale * (j + 1).toFloat()).toInt()
            val y = WyvtilsConfig.bossBarY
            ingameGui.drawTexturedModalRect(x, y, 0, 74, j, 5)
            ingameGui.drawTexturedModalRect(x, y, 0, 74, j, 5)
            if (l > 0) {
                ingameGui.drawTexturedModalRect(x, y, 0, 79, l, 5)
            }
            val s = BossStatus.bossName
            fontrenderer.drawString(
                s,
                (i / 2 - fontrenderer.getStringWidth(s) / 2).toFloat(), (y - 10).toFloat(),
                WyvtilsConfig.bossBarColor.rgb, WyvtilsConfig.bossBarShadow
            )
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(Gui.icons)
        }
    }
}