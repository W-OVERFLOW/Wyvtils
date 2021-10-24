package xyz.qalcyo.rysm.utils

import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack

object GlUtil {
    private fun drawRectangle(matrices: MatrixStack, xPosition: Int,
                              yPosition: Int,
                              width: Int,
                              height: Int,
                              color: Int) {
        DrawableHelper.fill(matrices, xPosition, yPosition, xPosition + width, yPosition + height, color)
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
    fun drawHollowRectangle(matrices: MatrixStack, xPosition: Int,
                            yPosition: Int,
                            width: Int,
                            height: Int,
                            thickness: Int,
                            color: Int) {
        drawRectangle(matrices, xPosition, yPosition, thickness, height, color)
        drawRectangle(matrices, xPosition + thickness, yPosition, width - thickness - thickness, thickness, color)
        drawRectangle(matrices, xPosition + thickness, yPosition + height - thickness, width - thickness - thickness, thickness, color)
        drawRectangle(matrices, xPosition + width - thickness, yPosition, thickness, height, color)
    }
}