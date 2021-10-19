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

package xyz.qalcyo.wyvtils.core

import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.core.utils.Updater
import java.io.File

object WyvtilsCore {
    var jarFile: File? = null
    var modDir: File? = null
    var currentVersion: MinecraftVersions = MinecraftVersions.UNSET
    var isPatcherLoaded = false

    fun onInitialization(version: MinecraftVersions) {
        when (version) {
            MinecraftVersions.EIGHT -> {
                println("hello 1.8.9")
            }
            MinecraftVersions.SEVENTEEN -> {
                println("hello 1.17.1")
            }
            else -> throw UnknownVersionException("This version is not supported by Wyvtils!")
        }
        currentVersion = version
        WyvtilsConfig.preload()
        Updater.update()
    }
}