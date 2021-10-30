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

package xyz.qalcyo.rysm.seventeen.listener

import gg.essential.lib.caffeine.cache.Cache
import gg.essential.lib.caffeine.cache.Caffeine
import net.minecraft.text.LiteralText
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object TextCacheManager {
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

    private val cache: Cache<String, LiteralText> = Caffeine.newBuilder().executor(POOL).maximumSize(5000).build()

    fun handleCache(text: LiteralText, string: String): LiteralText {
        return cache.getIfPresent(text.toString()) ?: run {
            cache.put(text.toString(), copy(text, string))
            return cache.getIfPresent(text.toString()) ?: throw AssertionError("This shouldn't have happened...")
        }
    }

    private fun copy(text: LiteralText, string: String): LiteralText {
        val baseText = LiteralText(string)
        baseText.siblings.addAll(text.siblings)
        baseText.style = text.style
        return baseText
    }
}