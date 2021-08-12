/*
 * Wyvtilities - Utilities for Hypixel 1.8.9.
 * Copyright (C) 2021 Wyvtilities
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

package net.wyvest.wyvtilities.utils

import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig
import xyz.matthewtgm.requisite.events.LocrawReceivedEvent
import xyz.matthewtgm.requisite.util.ApiHelper
import xyz.matthewtgm.requisite.util.HypixelHelper
import java.text.SimpleDateFormat
import java.util.*


object HypixelUtils {
    lateinit var winstreak: String
    var gexp: String? = null
    private var currentGame: HypixelHelper.HypixelLocraw.GameType? = null

    private fun getCurrentESTTime(): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("EST")
        return simpleDateFormat.format(Date(System.currentTimeMillis())) //86400000
    }

    fun getGEXP(): Boolean {
        var gexp: String? = null
        val uuid = mc.thePlayer.gameProfile.id.toString().replace("-", "")
        val guildData =
            Wyvtilities.jsonParser.parse(ApiHelper.getJsonOnline("https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + uuid)).asJsonObject
        val guildMembers = guildData["guild"].asJsonObject["members"].asJsonArray
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asInt.toString()
                break
            }
        }
        if (gexp == null) return false
        this.gexp = gexp
        return true
    }

    fun getGEXP(username: String): Boolean {
        var gexp: String? = null
        val uuid = getUUID(username)
        val guildData = Wyvtilities.jsonParser.parse(
            ApiHelper.getJsonOnline(
                "https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + uuid
            )
        ).asJsonObject
        val guildMembers = guildData["guild"].asJsonObject["members"].asJsonArray
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asInt.toString()
                break
            }
        }
        if (gexp == null) return false
        this.gexp = gexp
        return true
    }

    fun getWinstreak(): Boolean {
        val uuid = mc.thePlayer.gameProfile.id.toString().replace("-", "")
        val playerStats = Wyvtilities.jsonParser.parse(
            HypixelHelper.HypixelAPI.getPlayer(
                WyvtilsConfig.apiKey,
                uuid
            )
        ).asJsonObject["player"].asJsonObject["stats"]
        when (currentGame) {
            HypixelHelper.HypixelLocraw.GameType.BEDWARS -> {
                try {
                    winstreak = playerStats.asJsonObject["Bedwars"].asJsonObject["winstreak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            HypixelHelper.HypixelLocraw.GameType.SKYWARS -> {
                try {
                    winstreak = playerStats.asJsonObject["SkyWars"].asJsonObject["win_streak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            HypixelHelper.HypixelLocraw.GameType.DUELS -> {
                try {
                    winstreak = playerStats.asJsonObject["Duels"].asJsonObject["current_winstreak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            else -> return false
        }
        return true
    }

    fun getWinstreak(username: String): Boolean {
        val uuid = getUUID(username)
        val playerStats = Wyvtilities.jsonParser.parse(
            HypixelHelper.HypixelAPI.getPlayer(
                WyvtilsConfig.apiKey,
                uuid
            )
        ).asJsonObject["player"].asJsonObject["stats"]
        when (currentGame) {
            HypixelHelper.HypixelLocraw.GameType.BEDWARS -> {
                try {
                    winstreak = playerStats.asJsonObject["Bedwars"].asJsonObject["winstreak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            HypixelHelper.HypixelLocraw.GameType.SKYWARS -> {
                try {
                    winstreak = playerStats.asJsonObject["SkyWars"].asJsonObject["win_streak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            HypixelHelper.HypixelLocraw.GameType.DUELS -> {
                try {
                    winstreak = playerStats.asJsonObject["Duels"].asJsonObject["current_winstreak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            else -> return false
        }
        return true
    }

    fun getWinstreak(username: String, game: String): Boolean {
        val uuid = getUUID(username)
        val playerStats = Wyvtilities.jsonParser.parse(
            HypixelHelper.HypixelAPI.getPlayer(
                WyvtilsConfig.apiKey,
                uuid
            )
        ).asJsonObject["player"].asJsonObject["stats"]
        when (game.lowercase()) {
            "bedwars" -> {
                try {
                    winstreak = playerStats.asJsonObject["Bedwars"].asJsonObject["winstreak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            "skywars" -> {
                try {
                    winstreak = playerStats.asJsonObject["SkyWars"].asJsonObject["win_streak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            "duels" -> {
                try {
                    winstreak = playerStats.asJsonObject["Duels"].asJsonObject["current_winstreak"].asInt.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
            else -> return false
        }
        return true
    }

    //I really didn't want to use this and instead use one of essential's APIs, but then Mojang released an unannounced API change for the 69th time!
    private fun getUUID(username: String): String? {
        val uuidResponse =
            Wyvtilities.jsonParser.parse(ApiHelper.getJsonOnline("https://api.mojang.com/users/profiles/minecraft/$username")).asJsonObject
        if (uuidResponse.has("error")) {
            Wyvtilities.sendMessage(
                EnumChatFormatting.RED.toString() + "Failed with error: ${
                    uuidResponse["reason"].asString
                }"
            )
            return null
        }
        return uuidResponse["id"].asString
    }

    @SubscribeEvent
    fun onLocraw(event: LocrawReceivedEvent) {
        currentGame = event.locraw.gameType
    }

}