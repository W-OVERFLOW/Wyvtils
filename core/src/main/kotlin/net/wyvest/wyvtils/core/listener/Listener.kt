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

package net.wyvest.wyvtils.core.listener

import gg.essential.lib.caffeine.cache.Cache
import gg.essential.lib.caffeine.cache.Caffeine
import gg.essential.lib.kbrewster.eventbus.Subscribe
import gg.essential.universal.ChatColor
import net.wyvest.wyvtils.core.config.WyvtilsConfig
import net.wyvest.wyvtils.core.listener.events.*
import net.wyvest.wyvtils.core.utils.ColorUtils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

/**
 * Handles mostly all the internal handling of Wyvtils.
 */
object Listener {
    var color: String = ""
    private var counter: AtomicInteger = AtomicInteger(0)
    private var POOL: ThreadPoolExecutor = ThreadPoolExecutor(
        50, 50,
        0L, TimeUnit.SECONDS,
        LinkedBlockingQueue()
    ) { r ->
        Thread(
            r,
            "Wyvtils Highlight Thread ${counter.incrementAndGet()}"
        )
    }

    val cache: Cache<String, String> = Caffeine.newBuilder().executor(POOL).maximumSize(5000).build()
    private val regex = Pattern.compile("(?i)§[0-9A-FK-OR]")
    private var cachedUsername = ""

    @Subscribe
    fun onTitle(e: TitleEvent) {
        e.cancelled = !WyvtilsConfig.title
        e.shadow = WyvtilsConfig.titleShadow
        e.titleScale = WyvtilsConfig.titleScale
        e.subtitleScale = WyvtilsConfig.subtitleScale
    }

    @Subscribe
    fun onMessage(e: MessageRenderEvent) {
        if (WyvtilsConfig.hideLocraw) {
            val stripped = strip(e.message)
            if (stripped.startsWith("{") && stripped.contains("server") && stripped.endsWith("}")) {
                e.cancelled = true
            }
        }
    }

    @Subscribe
    fun onMouseScroll(e: MouseScrollEvent) {
        if (WyvtilsConfig.reverseScrolling) {
            if (e.scroll != 0.0) {
                e.scroll = e.scroll * -1
            }
        }
    }

    @Subscribe
    fun onHitbox(e: HitboxRenderEvent) {

        if (!WyvtilsConfig.hitbox) {
            e.cancelled = true
            return
        }
        e.cancelled = when (e.entity) {
            Entity.ARMORSTAND -> !WyvtilsConfig.armorstandHitbox
            Entity.FIREBALL -> !WyvtilsConfig.fireballHitbox
            Entity.FIREWORK -> !WyvtilsConfig.fireworkHitbox
            Entity.ITEM -> !WyvtilsConfig.itemHitbox
            Entity.LIVING -> !WyvtilsConfig.passiveHitbox
            Entity.MONSTER -> !WyvtilsConfig.monsterHitbox
            Entity.MINECART -> !WyvtilsConfig.minecartHitbox
            Entity.PLAYER -> !WyvtilsConfig.playerHitbox
            Entity.SELF -> WyvtilsConfig.disableForSelf || !WyvtilsConfig.playerHitbox
            Entity.PROJECTILE -> !WyvtilsConfig.projectileHitbox
            Entity.WITHERSKULL -> !WyvtilsConfig.witherSkullHitboxes
            Entity.XP -> !WyvtilsConfig.xpOrbHitbox
            Entity.UNDEFINED -> false
        }
        if (e.cancelled) return
        if (WyvtilsConfig.hitboxBox) {
            e.boxColor = if (WyvtilsConfig.hitboxChroma) ColorUtils.timeBasedChroma() else { if (e.distance <= 3.0 && e.distance != -1.0) WyvtilsConfig.hitboxCrosshairColor.rgb else WyvtilsConfig.hitboxColor.rgb }
        } else {
            e.cancelBox = true
        }
        if (WyvtilsConfig.hitboxEyeLine) {
            e.eyeLineColor = if (WyvtilsConfig.hitboxLineChroma) ColorUtils.timeBasedChroma() else WyvtilsConfig.hitboxEyelineColor.rgb
        } else {
            e.cancelEyeLine = true
        }
        if (WyvtilsConfig.hitboxLineOfSight) {
            e.lineOfSightColor = if (WyvtilsConfig.hitboxLineOfSightChroma) ColorUtils.timeBasedChroma() else WyvtilsConfig.hitboxLineOfSightColor.rgb
        } else {
            e.cancelLineOfSight = true
        }
    }

    @Subscribe
    fun onStringRendered(e: StringRenderEvent) {
        if ((e.username != null) && !WyvtilsConfig.chatHightlight && e.string.contains(
                e.username
            ) && WyvtilsConfig.highlightName
        ) {
            if (cachedUsername != e.username) {
                cache.invalidateAll()
                cachedUsername = e.username
            }
            if (e.string.contains("\u00A7")) {
                e.string = cache.getIfPresent(e.string) ?: replaceAndPut(e.string, e.username)
            } else {
                e.string = e.string.replace(
                    e.username,
                    color + e.username + ChatColor.RESET
                )
            }
        }
    }

    private fun replaceAndPut(string: String, username: String): String {
        var number = -1
        var code: String? = null
        val array = string.split(username).toMutableList()
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
            join(array.iterator(), color + username + ChatColor.RESET)
        cache.put(string, joined)
        return joined
    }

    private fun join(iterator: Iterator<*>, separator: String): String {
        if (!iterator.hasNext()) return ""
        val first = iterator.next()
        if (!iterator.hasNext()) return first.toString()
        val buf = StringBuilder()
        if (first != null) buf.append(first)
        while (iterator.hasNext()) {
            buf.append(separator)
            val obj = iterator.next()
            if (obj != null) buf.append(obj)
        }
        return buf.toString()
    }

    private fun strip(string: String): String {
        return regex.matcher(string).replaceAll("")
    }
}