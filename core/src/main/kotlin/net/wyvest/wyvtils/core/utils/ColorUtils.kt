/*
 * Wyvtils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Wyvtils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.wyvest.wyvtils.core.utils

import java.awt.Color

/**
 * Color based utilities for general usage.
 */
object ColorUtils {

    /**
     * @return A changing colour based on the users' computer time. Simulates a "chroma" colour.
     */
    fun timeBasedChroma(): Int {
        val l = System.currentTimeMillis()
        return Color.HSBtoRGB(l % 2000L / 2000.0f, 1.0f, 1.0f)
    }

    /**
     * @return The red value of the provided RGBA value.
     */
    fun getRed(rgba: Int): Int {
        return (rgba shr 16) and 0xFF
    }

    /**
     * @return The green value of the provided RGBA value.
     */
    fun getGreen(rgba: Int): Int {
        return (rgba shr 8) and 0xFF
    }

    /**
     * @return The blue value of the provided RGBA value.
     */
    fun getBlue(rgba: Int): Int {
        return (rgba shr 0) and 0xFF
    }

    /**
     * @return The alpha value of the provided RGBA value.
     */
    fun getAlpha(rgba: Int): Int {
        return (rgba shr 24) and 0xFF
    }
}