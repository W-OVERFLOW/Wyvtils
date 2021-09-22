/*
 * Qaltils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Qaltils
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

package xyz.qalcyo.qaltils.utils

import gg.essential.api.EssentialAPI
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import xyz.matthewtgm.requisite.events.LocrawReceivedEvent
import xyz.matthewtgm.requisite.util.HypixelHelper
import xyz.qalcyo.qaltils.Qaltils
import xyz.qalcyo.qaltils.Qaltils.mc
import xyz.qalcyo.qaltils.config.QaltilsConfig
import java.text.SimpleDateFormat
import java.util.*


object HypixelUtils {
    lateinit var winstreak: String
    var gexp: String? = null
    private var locraw: HypixelHelper.HypixelLocraw? = null
    @Suppress("ObjectPropertyName") private var `troll age` = false
    val skyblock
    get() = EssentialAPI.getMinecraftUtil().isHypixel() && mc.theWorld.scoreboard.getObjectiveInDisplaySlot(1)
        ?.let { it -> it.displayName.withoutFormattingCodes().toCharArray().filter { it.code in 21..126 }.joinToString(separator = "").contains("SKYBLOCK") } ?: false
    val lobby
    get() = if (EssentialAPI.getMinecraftUtil().isHypixel()) {
        locraw?.gameMode.isNullOrBlank() || locraw?.gameType == null
    } else {
        false
    }

    private fun getCurrentESTTime(): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("EST")
        return simpleDateFormat.format(Date(System.currentTimeMillis())) //86400000
    }

    fun getGEXP(): Boolean {
        var gexp: String? = null
        val uuid = mc.thePlayer.gameProfile.id.toString().replace("-", "")
        val guildData =
            APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + QaltilsConfig.apiKey + ";player=" + uuid)["guild"].asJsonObject
        val guildMembers = guildData["members"].asJsonArray
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asInt.toString()
                break
            }
        }
        if (gexp == null) return false
        HypixelUtils.gexp = gexp
        return true
    }

    fun getGEXP(username: String): Boolean {
        var gexp: String? = null
        val uuid = getUUID(username)
        val guildData =
            APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + QaltilsConfig.apiKey + ";player=" + uuid)["guild"].asJsonObject
        val guildMembers = guildData["members"].asJsonArray
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asInt.toString()
                break
            }
        }
        if (gexp == null) return false
        HypixelUtils.gexp = gexp
        return true
    }

    fun getWeeklyGEXP(): Boolean {
        var gexp: String? = null
        val uuid = mc.thePlayer.gameProfile.id.toString().replace("-", "")
        val guildData =
            APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + QaltilsConfig.apiKey + ";player=" + uuid)["guild"].asJsonObject
        val guildMembers = guildData["members"].asJsonArray
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                var addGEXP = 0
                for (set in e.asJsonObject["expHistory"].asJsonObject.entrySet()) {
                    addGEXP += set.value.asInt
                }
                gexp = addGEXP.toString()
                break
            }
        }
        if (gexp == null) return false
        HypixelUtils.gexp = gexp
        return true
    }

    fun getWeeklyGEXP(username: String): Boolean {
        var gexp: String? = null
        val uuid = getUUID(username)
        val guildData =
            APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + QaltilsConfig.apiKey + ";player=" + uuid)["guild"].asJsonObject
        val guildMembers = guildData["members"].asJsonArray
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                var addGEXP = 0
                for (set in e.asJsonObject["expHistory"].asJsonObject.entrySet()) {
                    addGEXP += set.value.asInt
                }
                gexp = addGEXP.toString()
                break
            }
        }
        if (gexp == null) return false
        HypixelUtils.gexp = gexp
        return true
    }

    fun getWinstreak(): Boolean {
        val uuid = mc.thePlayer.gameProfile.id.toString().replace("-", "")
        val playerStats = APIUtil.getJSONResponse("https://api.hypixel.net/player?key=${QaltilsConfig.apiKey};uuid=$uuid").asJsonObject["player"].asJsonObject["stats"]
        when (locraw?.gameType) {
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
        val playerStats = APIUtil.getJSONResponse("https://api.hypixel.net/player?key=${QaltilsConfig.apiKey};uuid=$uuid").asJsonObject["player"].asJsonObject["stats"]
        when (locraw?.gameType) {
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
        val playerStats = APIUtil.getJSONResponse("https://api.hypixel.net/player?key=${QaltilsConfig.apiKey};uuid=$uuid").asJsonObject["player"].asJsonObject["stats"]
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

    fun isValidKey(apiKey: String): Boolean {
        val json = APIUtil.getJSONResponse("https://api.hypixel.net/key?key=$apiKey")
        return json.has("success") && json["success"].asBoolean
    }

    private fun getUUID(username: String): String? {
        val uuidResponse =
            APIUtil.getJSONResponse("https://api.mojang.com/users/profiles/minecraft/$username")
        if (uuidResponse.has("error")) {
            Qaltils.sendMessage(
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
        locraw = event.locraw
        if (QaltilsConfig.autoBossbarLobby) {
            println(event.locraw.gameMode)
            println(event.locraw.gameType)
            if (event.locraw.gameMode.isNullOrBlank() || event.locraw.gameType == null) {
                QaltilsConfig.bossBar = false
                QaltilsConfig.markDirty()
                QaltilsConfig.writeData()
                `troll age` = true
            } else {
                if (`troll age`) {
                    QaltilsConfig.bossBar = true
                    QaltilsConfig.markDirty()
                    QaltilsConfig.writeData()
                    `troll age` = false
                }
            }
        }
    }

    @SubscribeEvent
    fun onLeave(event: FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        if (`troll age`) {
            QaltilsConfig.bossBar = true
            QaltilsConfig.markDirty()
            QaltilsConfig.writeData()
            `troll age` = false
        }
    }

}