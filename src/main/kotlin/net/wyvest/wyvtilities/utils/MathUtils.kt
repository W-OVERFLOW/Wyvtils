package net.wyvest.wyvtilities.utils

import java.math.BigDecimal
import java.util.Collections
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Adapted from TGMLib under GPLv3
 * https://github.com/TGMDevelopment/TGMLib/blob/main/LICENSE
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

    /**
     * Returns number between 0 - 1 depending on the range and value given
     *
     * @param val the value
     * @param min minimum of what the value can be
     * @param max maximum of what the value can be
     * @return converted percentage
     * @author isXander
     */
    fun getPercent(`val`: Float, min: Float, max: Float): Float {
        return (`val` - min) / (max - min)
    }

    /**
     * Returns the percentile of list of longs
     *
     * @param nums the list on which to calculate the percentile
     * @param percentile what percentile the calculation will output
     * @return the percentile of the nums
     * @author isXander
     */
    fun percentile(nums: List<Long>, percentile: Double): Long {
        Collections.sort(nums)
        val index = ceil(percentile / 100.0 * nums.size).toInt()
        return nums[index - 1]
    }

    /**
     * @param val value to change
     * @param places how many decimal places the number should have
     * @return x amount of places of precision of a value
     */
    fun precision(`val`: Float, places: Int): Float {
        val mod = places.coerceAtLeast(0)
        if (places(`val`) <= mod) return `val`
        return if (mod == 0) `val`.roundToInt().toFloat() else (`val` * mod).roundToInt().toFloat() / mod
    }

    /**
     * @param val value to check
     * @return the number of decimal places in a number
     */
    fun places(variable: Float): Int {
        return BigDecimal.valueOf(variable.toDouble()).scale()
    }
}