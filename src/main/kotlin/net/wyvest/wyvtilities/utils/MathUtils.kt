package net.wyvest.wyvtilities.utils

/**
 * Adapted from XanderLib under GPLv3
 * https://github.com/isXander/XanderLib/blob/main/LICENSE
 */
object MathUtils {

    /**
     * Clamps value between 0 and 1 and returns value.
     *
     * @author isXander
     */
    fun clamp01(value: Float): Float {
        if (value < 0) return 0.0f
        return if (value.toDouble() > 1.0) 1f else value
    }

    /**
     * Clamps value between min & max
     *
     * @param `val` value to clamp
     * @param min min value
     * @param max max value
     * @return clamped value
     * @author isXander
     */
    fun clamp(`val`: Float, min: Int, max: Int): Float {
        var `val` = `val`
        if (`val` > max) `val` = max.toFloat() else if (`val` < min) `val` = min.toFloat()
        return `val`
    }

    /**
     * Linearly interpolates between a and b by t.
     *
     * @param start Start value
     * @param end End value
     * @param interpolation Interpolation between two floats
     * @return interpolated value between a - b
     * @author isXander
     */
    fun lerp(start: Float, end: Int, interpolation: Float): Float {
        return start + (end - start) * clamp01(interpolation)
    }
}