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

package xyz.qalcyo.rysm.listeners

import gg.essential.lib.caffeine.cache.Cache
import gg.essential.lib.caffeine.cache.Caffeine
import net.minecraft.util.EnumChatFormatting
import xyz.qalcyo.eventbus.SubscribeEvent
import xyz.qalcyo.mango.Strings
import xyz.qalcyo.requisite.core.events.FontRendererEvent
import xyz.qalcyo.rysm.Rysm
import xyz.qalcyo.rysm.config.RysmConfig
import java.lang.String.format
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object HighlightManager {

    private var counter: AtomicInteger = AtomicInteger(0)
    private var POOL: ThreadPoolExecutor = ThreadPoolExecutor(
        50, 50,
        0L, TimeUnit.SECONDS,
        LinkedBlockingQueue()
    ) { r ->
        Thread(
            r,
            format("Rysm Highlight Thread %s", counter.incrementAndGet())
        )
    }

    private val cache: Cache<String, String> = Caffeine.newBuilder().executor(POOL).maximumSize(5000).build()
    private var cachedUsername = ""

    @SubscribeEvent
    fun onStringRendered(e: FontRendererEvent.RenderStringEvent) {
        if (!RysmConfig.chatHightlight && e.string != null && Rysm.mc.theWorld != null && e.string.contains(
                Rysm.mc.thePlayer.gameProfile.name, true
            ) && RysmConfig.highlightName && !Listener.changeTextColor
        ) {
            if (cachedUsername != Rysm.mc.thePlayer.gameProfile.name) {
                println("Username has changed, invalidating cache.")
                cache.invalidateAll()
                cachedUsername = Rysm.mc.thePlayer.gameProfile.name
            }
            if (e.string.contains("\u00A7")) {
                e.string = cache.getIfPresent(e.string) ?: replaceAndPut(e.string)
            } else {
                e.string = e.string.replace(
                    Rysm.mc.thePlayer.gameProfile.name,
                    Listener.color + Rysm.mc.thePlayer.gameProfile.name + EnumChatFormatting.RESET, ignoreCase = true
                )
            }
        }
    }

    private fun replaceAndPut(string: String): String {
        var number = -1
        var code: String? = null
        val array = string.split(Rysm.mc.thePlayer.gameProfile.name, ignoreCase = true).toMutableList()
        for (split in array) {
            number += 1
            if (number % 2 == 0 || number == 0) {
                val subString = split.substringAfterLast("\u00A7", null.toString())
                code = if (subString != "null") {
                    subString.first().toString()
                } else {
                    null
                }
                continue
            } else {
                if (code != null) {
                    array[number] = "\u00A7$code" + array[number]
                }
            }
        }
        val joined =
            Strings.join(array, Listener.color + Rysm.mc.thePlayer.gameProfile.name + EnumChatFormatting.RESET)
        cache.put(string, joined)
        return joined
    }
}