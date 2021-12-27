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

package cc.woverflow.wyvtils.core

import gg.essential.lib.kbrewster.eventbus.eventbus
import gg.essential.lib.kbrewster.eventbus.invokers.LMFInvoker
import gg.essential.universal.ChatColor
import cc.woverflow.wyvtils.core.config.WyvtilsConfig
import cc.woverflow.wyvtils.core.listener.Listener
import cc.woverflow.wyvtils.core.utils.MinecraftVersions
import cc.woverflow.wyvtils.core.utils.UnknownVersionException
import cc.woverflow.wyvtils.core.utils.Updater
import java.io.File

object WyvtilsCore {
    lateinit var jarFile: File
    lateinit var modDir: File
    var currentVersion: MinecraftVersions = MinecraftVersions.UNSET
    val eventBus = eventbus {
        invoker { LMFInvoker() }
        exceptionHandler { exception -> println("Error occurred in method: ${exception.message}") }
    }

    fun onInitialization(version: MinecraftVersions) {
        if (version != MinecraftVersions.SEVENTEEN && version != MinecraftVersions.EIGHT) throw UnknownVersionException(
            "This version is not supported by Wyvtils!"
        )
        currentVersion = version
        WyvtilsConfig.preload()
        Updater.update()
        eventBus.register(Listener)
        Listener.color = when (WyvtilsConfig.textColor) {
            0 -> ChatColor.BLACK.toString()
            1 -> ChatColor.DARK_BLUE.toString()
            2 -> ChatColor.DARK_GREEN.toString()
            3 -> ChatColor.DARK_AQUA.toString()
            4 -> ChatColor.DARK_RED.toString()
            5 -> ChatColor.DARK_PURPLE.toString()
            6 -> ChatColor.GOLD.toString()
            7 -> ChatColor.GRAY.toString()
            8 -> ChatColor.DARK_GRAY.toString()
            9 -> ChatColor.BLUE.toString()
            10 -> ChatColor.GREEN.toString()
            11 -> ChatColor.AQUA.toString()
            12 -> ChatColor.RED.toString()
            13 -> ChatColor.LIGHT_PURPLE.toString()
            14 -> ChatColor.YELLOW.toString()
            else -> ChatColor.WHITE.toString()
        }
    }
}