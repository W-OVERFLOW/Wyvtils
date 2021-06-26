package net.wyvest.wyvtilities.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse

/**
 * Adapted from TGMLib under GPLv3
 * https://github.com/TGMDevelopment/TGMLib/blob/main/LICENSE
 */
object MouseUtils {
    private val mc: Minecraft = Minecraft.getMinecraft()

    fun getMouseX(): Int {
        val res = ScaledResolution(mc)
        return Mouse.getX() * res.scaledWidth / mc.displayWidth
    }

    fun getMouseY(): Int {
        val res = ScaledResolution(mc)
        return res.scaledHeight - Mouse.getY() * res.scaledHeight / mc.displayHeight - 1
    }

    fun isMouseDown(): Boolean {
        return Mouse.getEventButtonState()
    }
}