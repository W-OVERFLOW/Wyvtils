package net.wyvest.wyvtilities.bossbar

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiIngame
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.boss.BossStatus

object BossHealth {
    //if making a fork, do not touch this right now as i am currently working on this feature!
    private var mc: Minecraft = Minecraft.getMinecraft()
    private var ingameGui: GuiIngame = Minecraft.getMinecraft().ingameGUI
    fun renderBossHealth() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime
            val fontrenderer: FontRenderer = mc.fontRendererObj
            val scaledresolution = ScaledResolution(mc)
            val i = scaledresolution.scaledWidth
            val j = 182
            val x = i / 2 - j / 2
            val l = (BossStatus.healthScale * (j + 1).toFloat()).toInt()
            val y = 12
            ingameGui.drawTexturedModalRect(x, y, 0, 74, j, 5)
            ingameGui.drawTexturedModalRect(x, y, 0, 74, j, 5)
            if (l > 0) {
                ingameGui.drawTexturedModalRect(x, y, 0, 79, l, 5)
            }
            val s = BossStatus.bossName
            fontrenderer.drawStringWithShadow(
                s,
                (i / 2 - fontrenderer.getStringWidth(s) / 2).toFloat(), (y - 10).toFloat(), 16777215
            )
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(Gui.icons)
        }
    }
}