/*
 * Rysm, a utility mod for 1.8.9.
 * Copyright (C) 2021 Rysm
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

package xyz.qalcyo.rysm.core.utils

import java.awt.Color

object ColorUtils {

    fun timeBasedChroma(): Int {
        val l = System.currentTimeMillis()
        return Color.HSBtoRGB(l % 2000L / 2000.0f, 1.0f, 1.0f)
    }

    fun getRed(rgba: Int): Int {
        return (rgba shr 16) and 0xFF
    }

    fun getGreen(rgba: Int): Int {
        return (rgba shr 8) and 0xFF
    }

    fun getBlue(rgba: Int): Int {
        return (rgba shr 0) and 0xFF
    }

    fun getAlpha(rgba: Int): Int {
        return (rgba shr 24) and 0xFF
    }
}