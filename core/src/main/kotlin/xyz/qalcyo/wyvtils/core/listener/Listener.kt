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

package xyz.qalcyo.wyvtils.core.listener

import gg.essential.lib.caffeine.cache.Cache
import gg.essential.lib.caffeine.cache.Caffeine
import gg.essential.universal.ChatColor
import gg.essential.universal.wrappers.UPlayer
import me.kbrewster.eventbus.Subscribe
import xyz.qalcyo.mango.Strings
import xyz.qalcyo.wyvtils.core.WyvtilsCore
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.core.listener.events.HitboxRenderEvent
import xyz.qalcyo.wyvtils.core.listener.events.MessageReceivedEvent
import xyz.qalcyo.wyvtils.core.listener.events.StringRenderEvent
import xyz.qalcyo.wyvtils.core.listener.events.entity.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern


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
            java.lang.String.format("Wyvtils Highlight Thread %s", counter.incrementAndGet())
        )
    }

    private val cache: Cache<String, String> = Caffeine.newBuilder().executor(POOL).maximumSize(5000).build()
    private val regex = Pattern.compile("(?i)§[0-9A-FK-OR]")

    @Subscribe
    fun onMessage(e: MessageReceivedEvent) {
        if (WyvtilsConfig.hideLocraw) {
            val stripped = strip(e.message)
            if (stripped.startsWith("{") && stripped.contains("server") && stripped.endsWith("}")) {
                e.cancelled = true
            }
        }
    }

    @Subscribe
    fun onHitbox(e: HitboxRenderEvent) {
        if (WyvtilsConfig.hitbox) {
            e.cancelled = true
            return
        }
        when (e.entity) {
            is ArmorstandEntity -> e.cancelled = !WyvtilsConfig.armorstandHitbox
            is FireballEntity -> e.cancelled = !WyvtilsConfig.fireballHitbox
            is FireworkEntity -> e.cancelled = !WyvtilsConfig.fireworkHitbox
            is ItemEntity -> e.cancelled = !WyvtilsConfig.itemHitbox
            is LivingEntity -> e.cancelled = if (e.entity.isHostile) !WyvtilsConfig.monsterHitbox else !WyvtilsConfig.passiveHitbox
            is MinecartEntity -> e.cancelled = !WyvtilsConfig.minecartHitbox
            is PlayerEntity -> e.cancelled = if (e.entity.isThePlayer) WyvtilsConfig.disableForSelf || !WyvtilsConfig.playerHitbox else !WyvtilsConfig.playerHitbox
            is ProjectileEntity -> e.cancelled = !WyvtilsConfig.projectileHitbox
            is WitherSkullEntity -> e.cancelled = !WyvtilsConfig.witherSkullHitboxes
            is XPEntity -> e.cancelled = !WyvtilsConfig.xpOrbHitbox
        }
        if (e.cancelled) return
        if (WyvtilsConfig.hitboxBox) {
            e.boxColor = if (e.distance <= 3.0) WyvtilsConfig.hitboxCrosshairColor else WyvtilsConfig.hitboxColor
        } else {
            e.cancelBox = true
        }
        if (WyvtilsConfig.hitboxEyeLine) {
            e.eyeLineColor = WyvtilsConfig.hitboxEyelineColor
        } else {
            e.cancelEyeLine = true
        }
        if (WyvtilsConfig.hitboxLineOfSight) {
            e.lineOfSightColor = WyvtilsConfig.hitboxLineOfSightColor
        } else {
            e.cancelEyeLine = true
        }
    }

    @Subscribe
    fun onStringRendered(e: StringRenderEvent) {
        if (UPlayer.hasPlayer()) {
            if (!WyvtilsConfig.chatHightlight && e.string.contains(
                    WyvtilsCore.username
                ) && WyvtilsConfig.highlightName
            ) {
                if (e.string.contains("\u00A7")) {
                    e.string = cache.getIfPresent(e.string) ?: replaceAndPut(e.string)
                } else {
                    e.string = e.string.replace(
                        WyvtilsCore.username,
                        color + WyvtilsCore.username + ChatColor.RESET
                    )
                }
            }
        }
    }

    private fun replaceAndPut(string: String): String {
        var number = -1
        var code: String? = null
        val array = string.split(Regex.fromLiteral(WyvtilsCore.username)).toMutableList()
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
            Strings.join(array, color + WyvtilsCore.username + ChatColor.RESET)
        cache.put(string, joined)
        return joined
    }

    private fun strip(string: String): String {
        return regex.matcher(string).replaceAll("")
    }
}