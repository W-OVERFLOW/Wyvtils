package net.wyvest.wyvtilities.utils

import net.wyvest.wyvtilities.Wyvtilities.Companion.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig
import java.text.SimpleDateFormat
import java.util.*


object GexpUtils {
    lateinit var gexp : String
    private fun getCurrentESTTime(): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("EST")
        return simpleDateFormat.format(Date())
    }

    fun getGEXP() {
        lateinit var gexp : String
        val uuid = mc.thePlayer.gameProfile.id.toString().replace("[\\-]".toRegex(), "")
        val guildData = APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + uuid)
        val guildMembers = guildData["guild"].asJsonObject["members"].asJsonArray
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asString
                break
            }
        }
        this.gexp = gexp
    }

    fun getGEXP(username : String) {
        lateinit var gexp : String
        val uuid = APIUtil.getUUID(username)
        val guildData = APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + uuid)
        val guildMembers = guildData["guild"].asJsonObject["members"].asJsonArray
        for (e in guildMembers) {
            if (e.asJsonObject["uuid"].asString.equals(uuid)) {
                gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asString
                break
            }
        }
        this.gexp = gexp
    }

}