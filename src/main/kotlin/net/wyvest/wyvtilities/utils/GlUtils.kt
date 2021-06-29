package net.wyvest.wyvtilities.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

/**
 * Adapted from TGMLib under GPLv3
 * https://github.com/TGMDevelopment/TGMLib/blob/main/LICENSE
 */
object GlUtils {

    fun totalScissor(xPosition: Float, yPosition: Float, width: Float, height: Float) {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        GL11.glScissor(
            (xPosition * Minecraft.getMinecraft().displayWidth / scaledResolution.scaledWidth).toInt(),
            ((scaledResolution.scaledHeight - (yPosition + height)) * Minecraft.getMinecraft().displayHeight / scaledResolution.scaledHeight).toInt(),
            (width * Minecraft.getMinecraft().displayWidth / scaledResolution.scaledWidth).toInt(),
            (height * Minecraft.getMinecraft().displayHeight / scaledResolution.scaledHeight).toInt()
        )
    }

    fun drawRectangle(xPosition: Float, yPosition: Float, width: Float, height: Float, colour: Pair<Unit, ColorRGB>) {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val worldrenderer = Tessellator.getInstance().worldRenderer
        GlStateManager.color(
            colour.second.getR() / 255.0f,
            colour.second.getG() / 255.0f,
            colour.second.getB() / 255.0f,
            colour.second.getA() / 255.0f
        )
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(xPosition.toDouble(), (yPosition + height).toDouble(), 0.0).endVertex()
        worldrenderer.pos((xPosition + width).toDouble(), (yPosition + height).toDouble(), 0.0).endVertex()
        worldrenderer.pos((xPosition + width).toDouble(), yPosition.toDouble(), 0.0).endVertex()
        worldrenderer.pos(xPosition.toDouble(), yPosition.toDouble(), 0.0).endVertex()
        Tessellator.getInstance().draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.bindTexture(0)
        GlStateManager.color(1f, 1f, 1f, 1f)
    }
}