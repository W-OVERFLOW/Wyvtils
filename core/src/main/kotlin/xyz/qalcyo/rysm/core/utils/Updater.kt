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

package xyz.qalcyo.rysm.core.utils

import gg.essential.api.EssentialAPI
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import xyz.qalcyo.mango.Multithreading
import xyz.qalcyo.rysm.core.RysmCore
import xyz.qalcyo.rysm.core.RysmInfo
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Future

object Updater {
    var latestTag = ""
    var shouldUpdate = false
    var shouldShowNotification = false
    var updateUrl = ""
    var updateFuture: Future<*>? = null

    /**
     * Stolen from SimpleTimeChanger under AGPLv3
     * https://github.com/My-Name-Is-Jeff/SimpleTimeChanger/blob/master/LICENSE
     */
    fun update() {
        updateFuture = Multithreading.submit {
            val latestRelease =
                APIUtil.getJSONResponse("https://api.github.com/repos/Qalcyo/${RysmInfo.ID}/releases/latest")
            latestTag = latestRelease.get("tag_name").asString

            val currentVersion = RysmVersion.CURRENT
            latestTag = latestRelease.get("tag_name").asString.substringAfter("v")
            val latestVersion = RysmVersion.fromString(latestTag)
            if (currentVersion < latestVersion) {
                updateUrl = latestRelease["assets"].asJsonArray[0].asJsonObject["browser_download_url"].asString
                shouldUpdate = true
                shouldShowNotification = true
            }
        }
    }

    /**
     * Adapted from RequisiteLaunchwrapper under LGPLv3
     * https://github.com/Qalcyo/RequisiteLaunchwrapper/blob/main/LICENSE
     */
    fun download(url: String, file: File): Boolean {
        if (file.exists()) return true
        var newUrl = url
        newUrl = newUrl.replace(" ", "%20")
        val downloadClient: HttpClient =
            HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(10000).build())
                .build()
        try {
            FileOutputStream(file).use { fileOut ->
                val downloadResponse: HttpResponse = downloadClient.execute(HttpGet(newUrl))
                val buffer = ByteArray(1024)
                var read: Int
                while (downloadResponse.entity.content.read(buffer).also { read = it } > 0) {
                    fileOut.write(buffer, 0, read)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     * Adapted from Skytils under AGPLv3
     * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
     */
    fun addShutdownHook() {
        EssentialAPI.getShutdownHookUtil().register {
            println("Deleting old ${RysmInfo.NAME} jar file...")
            try {
                val runtime = getJavaRuntime()
                if (System.getProperty("os.name").lowercase(Locale.ENGLISH).contains("mac")) {
                    val sipStatus = Runtime.getRuntime().exec("csrutil status")
                    sipStatus.waitFor()
                    if (!sipStatus.inputStream.use { it.bufferedReader().readText() }
                            .contains("System Integrity Protection status: disabled.")) {
                        println("SIP is NOT disabled, opening Finder.")
                        Desktop.getDesktop().open(RysmCore.jarFile!!.parentFile)
                    }
                }
                println("Using runtime $runtime")
                val file = File("config/Qalcyo/Deleter-1.2.jar")
                println("\"$runtime\" -jar \"${file.absolutePath}\" \"${RysmCore.jarFile!!.absolutePath}\"")
                if (System.getProperty("os.name").lowercase(Locale.ENGLISH).containsAny("linux", "unix")) {
                    println("On Linux, giving Deleter jar execute permissions...")
                    Runtime.getRuntime()
                        .exec("chmod +x \"${file.absolutePath}\"")
                }
                Runtime.getRuntime()
                    .exec("\"$runtime\" -jar \"${file.absolutePath}\" \"${RysmCore.jarFile!!.absolutePath}\"")
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Gets the current Java runtime being used.
     * @link https://stackoverflow.com/a/47925649
     */
    @Throws(IOException::class)
    fun getJavaRuntime(): String {
        val os = System.getProperty("os.name")
        val java = "${System.getProperty("java.home")}${File.separator}bin${File.separator}${
            if (os != null && os.lowercase().startsWith("windows")) "java.exe" else "java"
        }"
        if (!File(java).isFile) {
            throw IOException("Unable to find suitable java runtime at $java")
        }
        return java
    }

}