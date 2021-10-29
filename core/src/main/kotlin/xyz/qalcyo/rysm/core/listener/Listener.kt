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

package xyz.qalcyo.rysm.core.listener

import gg.essential.lib.caffeine.cache.Cache
import gg.essential.lib.caffeine.cache.Caffeine
import gg.essential.lib.kbrewster.eventbus.Subscribe
import gg.essential.universal.ChatColor
import xyz.qalcyo.mango.Strings
import xyz.qalcyo.rysm.core.config.RysmConfig
import xyz.qalcyo.rysm.core.listener.events.*
import xyz.qalcyo.rysm.core.utils.ColorUtils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

/**
 * Handles mostly all the internal handling of Rysm.
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
            String.format("Rysm Highlight Thread %s", counter.incrementAndGet())
        )
    }

    val cache: Cache<String, String> = Caffeine.newBuilder().executor(POOL).maximumSize(5000).build()
    private val regex = Pattern.compile("(?i)§[0-9A-FK-OR]")
    private var cachedUsername = ""

    @Subscribe
    fun onTitle(e: TitleEvent) {
        e.cancelled = !RysmConfig.title
        e.shadow = RysmConfig.titleShadow
        e.titleScale = RysmConfig.titleScale
        e.subtitleScale = RysmConfig.subtitleScale
    }

    @Subscribe
    fun onMessage(e: MessageRenderEvent) {
        if (RysmConfig.hideLocraw) {
            val stripped = strip(e.message)
            if (stripped.startsWith("{") && stripped.contains("server") && stripped.endsWith("}")) {
                e.cancelled = true
            }
        }
    }

    @Subscribe
    fun onMouseScroll(e: MouseScrollEvent) {
        if (RysmConfig.reverseScrolling) {
            if (e.scroll != 0.0) {
                e.scroll = e.scroll * -1
            }
        }
    }

    @Subscribe
    fun onHitbox(e: HitboxRenderEvent) {

        if (!RysmConfig.hitbox) {
            e.cancelled = true
            return
        }
        e.cancelled = when (e.entity) {
            Entity.ARMORSTAND -> !RysmConfig.armorstandHitbox
            Entity.FIREBALL -> !RysmConfig.fireballHitbox
            Entity.FIREWORK -> !RysmConfig.fireworkHitbox
            Entity.ITEM -> !RysmConfig.itemHitbox
            Entity.LIVING -> !RysmConfig.passiveHitbox
            Entity.MONSTER -> !RysmConfig.monsterHitbox
            Entity.MINECART -> !RysmConfig.minecartHitbox
            Entity.PLAYER -> !RysmConfig.playerHitbox
            Entity.SELF -> RysmConfig.disableForSelf || !RysmConfig.playerHitbox
            Entity.PROJECTILE -> !RysmConfig.projectileHitbox
            Entity.WITHERSKULL -> !RysmConfig.witherSkullHitboxes
            Entity.XP -> !RysmConfig.xpOrbHitbox
            Entity.UNDEFINED -> false
        }
        if (e.cancelled) return
        if (RysmConfig.hitboxBox) {
            e.boxColor = if (RysmConfig.hitboxChroma) ColorUtils.timeBasedChroma() else { if (e.distance <= 3.0 && e.distance != -1.0) RysmConfig.hitboxCrosshairColor.rgb else RysmConfig.hitboxColor.rgb }
        } else {
            e.cancelBox = true
        }
        if (RysmConfig.hitboxEyeLine) {
            e.eyeLineColor = if (RysmConfig.hitboxLineChroma) ColorUtils.timeBasedChroma() else RysmConfig.hitboxEyelineColor.rgb
        } else {
            e.cancelEyeLine = true
        }
        if (RysmConfig.hitboxLineOfSight) {
            e.lineOfSightColor = if (RysmConfig.hitboxLineOfSightChroma) ColorUtils.timeBasedChroma() else RysmConfig.hitboxLineOfSightColor.rgb
        } else {
            e.cancelEyeLine = true
        }
    }

    @Subscribe
    fun onStringRendered(e: StringRenderEvent) {
        if (e.username != null && !RysmConfig.chatHightlight && e.string.contains(
                e.username
            ) && RysmConfig.highlightName
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
            Strings.join(array, color + username + ChatColor.RESET)
        cache.put(string, joined)
        return joined
    }

    private fun strip(string: String): String {
        return regex.matcher(string).replaceAll("")
    }
}