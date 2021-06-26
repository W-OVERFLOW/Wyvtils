package net.wyvest.wyvtilities.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.client.Minecraft
import net.wyvest.wyvtilities.config.WyvtilsConfig
import java.text.SimpleDateFormat
import java.util.*


object GexpUtils {
    private var apiObj: JsonObject? = null
    private lateinit var guildMembers: JsonArray
    private var lastRequest = System.currentTimeMillis()

    private fun getCurrentESTTime(): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("EST")
        return simpleDateFormat.format(Date())
    }

    fun getGEXP(): String? {
        var gexp: String? = null
        apiObj = try {
            APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + Minecraft.getMinecraft().session.playerID) //Hypixel API only supports short uuids
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return try {
            guildMembers = apiObj!!["guild"].asJsonObject["members"].asJsonArray
            for (e in guildMembers) {
                if (e.asJsonObject["uuid"].asString.equals(
                        Minecraft.getMinecraft().session.playerID,
                        ignoreCase = true
                    )
                ) {
                    gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asString
                    break
                }
            }
            lastRequest = System.currentTimeMillis()
            gexp
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getGEXP(username: String?): String? {
        var gexp: String? = null
        val uuid: String
        try {
            apiObj = APIUtil.getJSONResponse(
                "https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + username?.let {
                    APIUtil.getUUID(
                        it
                    )
                }
            )
            uuid = username?.let { APIUtil.getUUID(it).toString() }.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return try {
            guildMembers = apiObj!!["guild"].asJsonObject["members"].asJsonArray
            for (e in guildMembers) {
                if (e.asJsonObject["uuid"].asString.equals(uuid, ignoreCase = true)) {
                    gexp = e.asJsonObject["expHistory"].asJsonObject[getCurrentESTTime()].asString
                    break
                }
            }
            gexp
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}