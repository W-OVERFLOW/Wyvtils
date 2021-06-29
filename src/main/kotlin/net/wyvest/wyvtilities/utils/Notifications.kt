package net.wyvest.wyvtilities.utils

import lombok.Getter
import lombok.Setter
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent
import net.wyvest.wyvtilities.utils.ColorRGB.ColorRGB
import net.wyvest.wyvtilities.utils.GlUtils.drawRectangle
import net.wyvest.wyvtilities.utils.GlUtils.totalScissor
import org.lwjgl.opengl.GL11
import java.awt.Color


/**
 * Adapted from TGMLib under GPLv3
 * https://github.com/TGMDevelopment/TGMLib/blob/main/LICENSE
 */
object Notifications {
    private val mc = Minecraft.getMinecraft()

    @Getter
    @Setter
    private val width = 200

    @Getter
    @Setter
    private val paddingWidth = 5

    @Getter
    @Setter
    private val paddingHeight = 3

    @Getter
    @Setter
    private val textDistance = 2

    private val current: MutableList<Notification> = ArrayList()

    fun push(title: String, description: String) {
        push(title, description, null)
    }

    fun push(title: String, description: String, runnable: Runnable?) {
        current.add(Notification(title, description, runnable))
    }

    @SubscribeEvent
    fun onRenderTick(event: RenderTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        val res = ScaledResolution(mc)
        if (current.size == 0) return
        val notification = current[0]
        val time = notification.time
        var opacity = 200f
        if (time <= 1 || time >= 10) {
            val easeTime = time.coerceAtMost(1f)
            opacity = easeTime * 200
        }
        val wrappedTitle =
            wrapTextLines(EnumChatFormatting.BOLD.toString() + notification.title, mc.fontRendererObj, width, " ")
        val wrappedText = wrapTextLines(notification.description, mc.fontRendererObj, width, " ")
        val textLines = wrappedText.size + wrappedTitle.size
        notification.width = MathUtils.lerp(notification.width, width + paddingWidth * 2, event.renderTickTime / 4f)
        val rectWidth = notification.width
        val rectHeight =
            (paddingHeight * 2 + textLines * mc.fontRendererObj.FONT_HEIGHT + (textLines - 1) * textDistance).toFloat()
        val rectX = res.scaledWidth / 2f - rectWidth / 2f
        val rectY = 5f
        val mouseX: Float = MouseUtils.getMouseX().toFloat()
        val mouseY: Float = MouseUtils.getMouseY().toFloat()
        val mouseOver =
            mouseX >= rectX && mouseX <= rectX + rectWidth && mouseY >= rectY && mouseY <= rectY + rectHeight
        notification.mouseOverAdd =
            MathUtils.lerp(notification.mouseOverAdd, if (mouseOver) 40 else 0, event.renderTickTime / 4f)
        opacity += notification.mouseOverAdd
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableDepth()
        drawRectangle(rectX, rectY, rectWidth, rectHeight,
            ColorRGB(0, 0, 0, MathUtils.clamp(opacity, 5, 255).toInt()) to ColorRGB
        )
        if (notification.time > 0.1f) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST)
            totalScissor(rectX, rectY, rectWidth, rectHeight)
            val color = Color(255, 255, 255, MathUtils.clamp(opacity, 2, 255).toInt()).rgb
            var i = 0
            for (line in wrappedTitle) {
                mc.fontRendererObj.drawString(
                    EnumChatFormatting.BOLD.toString() + line,
                    res.scaledWidth / 2f - mc.fontRendererObj.getStringWidth(line) / 2f,
                    rectY + paddingHeight + textDistance * i + mc.fontRendererObj.FONT_HEIGHT * i,
                    color,
                    true
                )
                i++
            }
            for (line in wrappedText) {
                mc.fontRendererObj.drawString(
                    line,
                    res.scaledWidth / 2f - mc.fontRendererObj.getStringWidth(line) / 2f,
                    rectY + paddingHeight + textDistance * i + mc.fontRendererObj.FONT_HEIGHT * i,
                    color,
                    false
                )
                i++
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST)
        }
        GlStateManager.popMatrix()
        if (notification.time >= 3f) notification.closing = true
        if (!notification.clicked && mouseOver && MouseUtils.isMouseDown()) {
            notification.clicked = true
            if (notification.runnable != null) notification.runnable!!.run()
            notification.closing = true
            if (notification.time > 1f) notification.time = 1f
        }
        if (!(mouseOver && notification.clicked && notification.time > 1f)) notification.time += (if (notification.closing) -0.02f else 0.02f) * (event.renderTickTime * 3f)
        if (notification.closing && notification.time <= 0) current.remove(notification)
    }

    private fun wrapTextLines(text: String, fontRenderer: FontRenderer, lineWidth: Int, split: String): List<String> {
        val wrapped = wrapText(text, fontRenderer, lineWidth, split)
        return if (wrapped == "") emptyList() else wrapped.split("\n".toRegex()).toList()
    }

    private fun wrapText(text: String, fontRenderer: FontRenderer, lineWidth: Int, split: String): String {
        val words = text.split("($split|\n)".toRegex()).toTypedArray()
        var lineLength = 0
        val output = StringBuilder()
        for (i in words.indices) {
            var word = words[i]
            if (i != words.size - 1) word += split
            val wordLength = fontRenderer.getStringWidth(word)
            if (lineLength + wordLength <= lineWidth) {
                output.append(word)
                lineLength += wordLength
            } else if (wordLength <= lineWidth) {
                output.append("\n").append(word)
                lineLength = wordLength
            } else output.append(wrapText(word, fontRenderer, lineWidth, "")).append(split)
        }
        return output.toString()
    }

    private class Notification(
        var title: String,
        var description: String,
        var runnable: Runnable?
    ) {
        var time = 0f
        var width = 0f
        var mouseOverAdd = 0f
        var closing = false
        var clicked = false
    }
}