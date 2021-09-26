package xyz.qalcyo.wyvtils.utils

import net.minecraft.client.gui.Gui


object GlUtil {
    fun drawRectangle(xPosition: Int,
                      yPosition: Int,
                      width: Int,
                      height: Int,
    color: Int) {
        Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, color)
    }

    /**
     * Draw a hollow rectangle on screen.
     *
     * @param xPosition x start location
     * @param yPosition y start location
     * @param width width of rectangle
     * @param height height of rectangle
     * @param thickness width of the border
     * @param color color of rectangle
     */
    fun drawHollowRectangle(
        xPosition: Int,
        yPosition: Int,
        width: Int,
        height: Int,
        thickness: Int,
        color: Int
    ) {
        drawRectangle(xPosition, yPosition, thickness, height, color)
        drawRectangle(xPosition + thickness, yPosition, width - thickness - thickness, thickness, color)
        drawRectangle(xPosition + thickness, yPosition + height - thickness, width - thickness - thickness, thickness, color)
        drawRectangle(xPosition + width - thickness, yPosition, thickness, height, color)
    }
}