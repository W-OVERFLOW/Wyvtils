package net.wyvest.wyvtilities.actionbar

import net.minecraft.client.renderer.GlStateManager
import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBar
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarCustomization
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarPosition
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarShadow
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarX
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarY
import org.lwjgl.opengl.GL11
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import xyz.matthewtgm.tgmlib.tweaker.hooks.TGMLibGuiIngameAccessor
import java.awt.Color

object ActionBar {
    fun renderActionBar(
        width: Int,
        height: Int,
        partialTicks: Float,
        ci: CallbackInfo
    ) {
        if (actionBarCustomization) {
            mc.mcProfiler.startSection("overlayMessage")
            val guiIngame = mc.ingameGUI as TGMLibGuiIngameAccessor
            val hue: Float = guiIngame.recordPlayingUpFor.toFloat() - partialTicks
            var opacity = (hue * 256.0f / 20.0f).toInt()
            if (opacity > 255) opacity = 255
            if (opacity > 0) {
                GlStateManager.pushMatrix()
                if (!actionBarPosition) {
                    GlStateManager.translate((width / 2).toFloat(), (height - 68).toFloat(), 0.0f)
                    GlStateManager.enableBlend()
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
                }
                val color = if (guiIngame.isRecordIsPlaying) Color.HSBtoRGB(
                    hue / 50.0f,
                    0.7f,
                    0.6f
                ) and Color.WHITE.rgb else Color.WHITE.rgb
                if (actionBar) {
                    val x: Int
                    val y: Int
                    if (actionBarPosition) {
                        x = actionBarX - mc.fontRendererObj.getStringWidth(guiIngame.recordPlaying) / 2
                        y = actionBarY
                    } else {
                        x = -mc.fontRendererObj.getStringWidth(guiIngame.recordPlaying) / 2
                        y = -4
                    }
                    mc.fontRendererObj.drawString(guiIngame.recordPlaying,
                        x.toFloat(), y.toFloat(), color or (opacity shl 24), actionBarShadow)
                }
                if (!actionBarPosition) GlStateManager.disableBlend()
                GlStateManager.popMatrix()
            }
            mc.mcProfiler.endSection()
            ci.cancel()
        }
    }
}