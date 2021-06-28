package net.wyvest.wyvtilities.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.client.Minecraft
import net.wyvest.wyvtilities.Wyvtilities
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
        Wyvtilities.threadPool.submit{
            apiObj = try {
                APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + Minecraft.getMinecraft().session.playerID) //Hypixel API only supports short uuids
            } catch (e: Exception) {
                e.printStackTrace()
                return@submit
            }
        }
        return try {
            guildMembers = apiObj!!["guild"].asJsonObject["members"].asJsonArray
            for (e in guildMembers) {
                if (e.asJsonObject["uuid"].asString.equals(
                        Minecraft.getMinecraft().session.playerID
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
//HAVE MOZZILLA / 5.3252 WYVEST THING YES
    fun getGEXP(username: String?): String? {
        var gexp: String? = null
        var uuid: String? = null
        try {
            Wyvtilities.threadPool.submit {
                uuid = APIUtil.getUUID(username.toString())
                apiObj = APIUtil.getJSONResponse(
                    "https://api.hypixel.net/guild?key=" + WyvtilsConfig.apiKey + ";player=" + uuid)
                    }
            } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        println(username)
        println(uuid)
        return try {
            guildMembers = apiObj!!["guild"].asJsonObject["members"].asJsonArray
            for (e in guildMembers) {
                if (e.asJsonObject["uuid"].asString.equals(uuid)) {
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