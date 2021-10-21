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

package xyz.qalcyo.wyvtils.eight.utils

import gg.essential.api.EssentialAPI
import net.minecraft.util.EnumChatFormatting
import xyz.qalcyo.requisite.core.integration.hypixel.events.LocrawReceivedEvent
import xyz.qalcyo.requisite.core.integration.hypixel.locraw.HypixelLocraw
import xyz.qalcyo.wyvtils.core.utils.APIUtil
import xyz.qalcyo.wyvtils.core.utils.withoutFormattingCodes
import xyz.qalcyo.wyvtils.eight.Wyvtils
import xyz.qalcyo.wyvtils.eight.Wyvtils.mc


object HypixelUtils {
    private var locraw: HypixelLocraw? = null
    val skyblock
        get() = EssentialAPI.getMinecraftUtil().isHypixel() && mc.theWorld.scoreboard.getObjectiveInDisplaySlot(1)
            ?.let { it ->
                it.displayName.withoutFormattingCodes().toCharArray().filter { it.code in 21..126 }
                    .joinToString(separator = "").contains("SKYBLOCK")
            } ?: false
    val lobby
        get() = if (EssentialAPI.getMinecraftUtil().isHypixel()) {
            locraw?.gameMode.isNullOrBlank() || locraw?.gameType == null
        } else {
            false
        }

    private fun getUUID(username: String): String? {
        val uuidResponse =
            APIUtil.getJSONResponse("https://api.mojang.com/users/profiles/minecraft/$username")
        if (uuidResponse.has("error")) {
            Wyvtils.sendMessage(
                EnumChatFormatting.RED.toString() + "Failed with error: ${
                    uuidResponse["reason"].asString
                }"
            )
            return null
        }
        return uuidResponse["id"].asString
    }

    fun onLocraw(event: LocrawReceivedEvent) {
        locraw = event.locraw
    }

}