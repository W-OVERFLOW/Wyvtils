package net.wyvest.wyvtilities.utils

import gg.essential.api.utils.WebUtil
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig
import xyz.matthewtgm.json.parser.JsonParser
import xyz.matthewtgm.tgmlib.util.HypixelHelper
import xyz.matthewtgm.tgmlib.util.ServerHelper
import java.text.SimpleDateFormat
import java.util.*


object HypixelUtils {
    lateinit var winstreak: String
    var gexp: String? = null
    private fun getCurrentESTTime(): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("EST")
        return simpleDateFormat.format(Date(System.currentTimeMillis())) //86400000
    }

    fun getGEXP() : Boolean {
        var gexp : String? = null
        val uuid = mc.thePlayer.gameProfile.id.toString().replace("-", "")
        val guildData =
            WebUtil.fetchJSON("https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + uuid)
        val guildMembers = guildData.optJSONObject("guild").optJSONArray("members")
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asString
                break
            }
        }
        if (gexp == null) return false
        this.gexp = gexp
        return true
    }

    fun getGEXP(username: String) : Boolean {
        var gexp : String? = null
        val uuid = getUUID(username)
        val guildData =
            WebUtil.fetchJSON("https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + uuid)
        val guildMembers = guildData.optJSONObject("guild").optJSONArray("members")
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asString
                break
            }
        }
        if (gexp == null) return false
        this.gexp = gexp
        return true
    }

    fun getWinstreak(): Boolean {
        val uuid = mc.thePlayer.gameProfile.id.toString().replace("-", "")
        val playerStats = JsonParser.parse(
            HypixelHelper.HypixelAPI.getPlayer(
                WyvtilsConfig.apiKey,
                uuid
            )
        ).asJsonObject["player"].asJsonObject["stats"]
        if (!ServerHelper.hypixelBedwars()) return false
        try {
            winstreak = playerStats.asJsonObject["Bedwars"].asJsonObject["winstreak"].asInt.toString()
        } catch (e : Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun getWinstreak(username: String): Boolean {
        val uuid = getUUID(username)
        val playerStats = JsonParser.parse(
            HypixelHelper.HypixelAPI.getPlayer(
                WyvtilsConfig.apiKey,
                uuid
            )
        ).asJsonObject["player"].asJsonObject["stats"]
        return when (HypixelHelper.getLocraw().gameType) {
            HypixelHelper.HypixelLocraw.GameType.BEDWARS -> {
                winstreak = playerStats.asJsonObject["Bedwars"].asJsonObject["winstreak"].asInt.toString()
                true
            }
            HypixelHelper.HypixelLocraw.GameType.SKYWARS -> {
                winstreak = playerStats.asJsonObject["SkyWars"].asJsonObject["win_streak"].asInt.toString()
                true
            }
            HypixelHelper.HypixelLocraw.GameType.DUELS -> {
                winstreak = playerStats.asJsonObject["Duels"].asJsonObject["current_winstreak"].asInt.toString()
                true
            }
            else -> false
        }
    }
        //I really didn't want to use this and instead use one of essential's APIs, but then Mojang released an unannounced API change for the 69th time!
        private fun getUUID(username: String): String? {
            val uuidResponse = WebUtil.fetchJSON("https://api.mojang.com/users/profiles/minecraft/$username")
            if (uuidResponse.has("error")) {
                Wyvtilities.sendMessage(
                    EnumChatFormatting.RED.toString() + "Failed with error: ${
                        uuidResponse.optString(
                            "reason"
                        )
                    }"
                )
                return null
            }
            return uuidResponse.optString("id").replace("-", "")
        }

    }
