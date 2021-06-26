package net.wyvest.wyvtilities.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText
import net.wyvest.wyvtilities.Wyvtilities
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.HttpVersion
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils
import java.net.URL
import java.util.*

/**
 * Adapted from Skytils under AGPLv3
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
object APIUtil {
    private val parser = JsonParser()
    private val mc = Minecraft.getMinecraft()

    private val builder: HttpClientBuilder =
        HttpClients.custom().setUserAgent("Wyvtilities/" + Wyvtilities.VERSION)
            .addInterceptorFirst { request: HttpRequest, _: HttpContext? ->
                if (!request.containsHeader("Pragma")) request.addHeader("Pragma", "no-cache")
                if (!request.containsHeader("Cache-Control")) request.addHeader("Cache-Control", "no-cache")
            }

    fun getJSONResponse(urlString: String): JsonObject {
        val client = builder.build()
        try {
            val request = HttpGet(URL(urlString).toURI())
            request.protocolVersion = HttpVersion.HTTP_1_1
            val response: HttpResponse = client.execute(request)
            val entity = response.entity
            if (response.statusLine.statusCode == 200) {
                return parser.parse(EntityUtils.toString(entity)).asJsonObject
            } else {
                    val errorStream = entity.content
                    Scanner(errorStream).use { scanner ->
                        scanner.useDelimiter("\\Z")
                        val error = scanner.next()
                        if (error.startsWith("{")) {
                            return parser.parse(error).asJsonObject
                        }
                    }
                }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText("§cAn error has occured whilst fetching a resource. See logs for more details."))
        } finally {
            client.close()
        }
        return JsonObject()
    }
    fun getUUID(username: String): String? {
        val uuidResponse = getJSONResponse("https://api.mojang.com/users/profiles/minecraft/$username")
        return uuidResponse["id"].asString
    }
}