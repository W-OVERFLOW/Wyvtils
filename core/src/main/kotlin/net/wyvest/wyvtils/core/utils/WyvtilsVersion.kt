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

import net.wyvest.wyvtils.core.WyvtilsInfo
import java.text.ParseException

/**
 * A class which represents a version of Wyvtils.
 *
 * <p>
 *
 * Adapted from Kotlin under the Apache License 2.0
 * https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt
 */
class WyvtilsVersion(val major: Int, val minor: Int, val patch: Int, val beta: Int) : Comparable<WyvtilsVersion> {

    private val version = versionOf(major, minor, patch, beta)

    private fun versionOf(major: Int, minor: Int, patch: Int, beta: Int): Int {
        require(major in 0..MAX_COMPONENT_VALUE && minor in 0..MAX_COMPONENT_VALUE && patch in 0..MAX_COMPONENT_VALUE && beta in 0..MAX_COMPONENT_VALUE) {
            "Version components are out of range: $major.$minor.$patch-beta$beta"
        }
        return major.shl(16) + minor.shl(8) + patch + beta
    }

    override fun toString(): String = "$major.$minor.$patch-beta$beta"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherVersion = (other as? WyvtilsVersion) ?: return false
        return this.version == otherVersion.version
    }

    override fun hashCode(): Int = version

    override fun compareTo(other: WyvtilsVersion): Int = version - other.version

    companion object {
        const val MAX_COMPONENT_VALUE = 255
        val regex =
            Regex("^(?<major>[0|1-9\\d*])\\.(?<minor>[0|1-9\\d*])\\.(?<patch>[0|1-9\\d*])(?:-beta)?(?<beta>.*)?\$")

        val CURRENT: WyvtilsVersion = fromString(WyvtilsInfo.VER)

        fun fromString(version: String): WyvtilsVersion {
            val match = regex.matchEntire(version)
            if (match != null) {
                return WyvtilsVersion(
                    match.groups["major"]!!.value.toInt(),
                    match.groups["minor"]!!.value.toInt(),
                    match.groups["patch"]!!.value.toInt(),
                    if (match.groups["beta"]?.value.isNullOrBlank()) 0 else match.groups["beta"]!!.value.toInt()
                )
            } else {
                throw ParseException("The string ($version) provided did not match the Wyvtils Version regex!", -1)
            }
        }
    }
}