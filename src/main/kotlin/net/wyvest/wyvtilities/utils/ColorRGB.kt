package net.wyvest.wyvtilities.utils

import java.awt.Color

/**
 * Adapted from TGMLib under GPLv3
 * https://github.com/TGMDevelopment/TGMLib/blob/main/LICENSE
 */
@Suppress("unused")
object ColorRGB {
    private var r = 0
    private var g = 0
    private var b = 0
    private var a = 0

    fun ColorRGB(r: Int, g: Int, b: Int, a: Int) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }

    fun ColorRGB(r: Int, g: Int, b: Int) {
        this.r = r
        this.g = g
        this.b = b
        this.a = 255
    }

    fun ColorRGB(color: Color) {
        ColorRGB(color.red, color.green, color.blue, color.alpha)
    }

    fun ColorRGB(rgba: Int) {
        val color = Color(rgba)
        r = color.red
        this.g = color.green
        this.b = color.blue
        this.a = color.alpha
    }

    fun getR(): Int {
        return r
    }

    fun setR(r: Int) {
        this.r = r
    }

    fun getG(): Int {
        return g
    }

    fun setG(g: Int) {
        this.g = g
    }

    fun getB(): Int {
        return b
    }

    fun setB(b: Int) {
        this.b = b
    }

    fun getA(): Int {
        return a
    }

    fun setA(a: Int) {
        this.a = a
    }

    fun getRGB(): Int {
        return toJavaColor().rgb
    }

    fun getRGBA(): Int {
        return toJavaColor().rgb
    }

    fun toJavaColor(): Color {
        return Color(r, g, b, a)
    }

    override fun toString(): String {
        return "ColorRGB{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", a=" + a +
                '}'
    }
}