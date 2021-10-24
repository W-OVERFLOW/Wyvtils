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

package xyz.qalcyo.rysm.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import xyz.qalcyo.rysm.Rysm
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
 * Stolen from Skytils under AGPLv3
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
object APIUtil {
    private val parser = JsonParser()

    private val builder: HttpClientBuilder =
        HttpClients.custom().setUserAgent("${Rysm.MOD_NAME}/${Rysm.VERSION}")
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
                if (urlString.startsWithAny(
                        "https://api.ashcon.app/mojang/v2/user/",
                        "https://api.hypixel.net/"
                    )
                ) {
                    val errorStream = entity.content
                    Scanner(errorStream).use { scanner ->
                        scanner.useDelimiter("\\Z")
                        val error = scanner.next()
                        if (error.startsWith("{")) {
                            return parser.parse(error).asJsonObject
                        }
                    }
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Rysm.sendMessage("§cAn error has occured whilst fetching a resource. See logs for more details.")
        } finally {
            client.close()
        }
        return JsonObject()
    }
}