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
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.util.Util
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion
import xyz.qalcyo.qaltils.Qaltils
import xyz.qalcyo.qaltils.Qaltils.mc
import xyz.qalcyo.qaltils.gui.DownloadConfirmGui
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object Updater {
    var updateUrl: String = ""
    lateinit var latestTag: String
    var shouldUpdate = false

    /**
     * Adapted from SimpleToggleSprint under AGPLv3
     * https://github.com/My-Name-Is-Jeff/SimpleToggleSprint/blob/1.8.9/LICENSE
     */
    fun update() {
        CoroutineScope(Dispatchers.IO + CoroutineName("${Qaltils.MOD_NAME}-UpdateChecker")).launch {
            val latestRelease =
                APIUtil.getJSONResponse("https://api.github.com/repos/Qalcyo/${Qaltils.MODID}/releases/latest")
            latestTag = latestRelease.get("tag_name").asString

            val currentVersion = DefaultArtifactVersion(Qaltils.VERSION.substringBefore("-"))
            val latestVersion = DefaultArtifactVersion(latestTag.substringAfter("v").substringBefore("-"))

            if ((Qaltils.VERSION.contains("BETA") && currentVersion >= latestVersion)) {
                return@launch
            } else if (currentVersion < latestVersion) {
                updateUrl = latestRelease["assets"].asJsonArray[0].asJsonObject["browser_download_url"].asString
            }
            if (updateUrl.isNotEmpty()) {
                EssentialAPI.getNotifications()
                    .push(
                        "Mod Update",
                        "${Qaltils.MOD_NAME} $latestTag is available!\nClick here to download it!",
                        5f
                    ) {
                        EssentialAPI.getGuiUtil().openScreen(DownloadConfirmGui(mc.currentScreen))
                    }
                shouldUpdate = true
            }
        }
    }

    /**
     * Adapted from RequisiteLaunchwrapper under LGPLv2.1
     * https://github.com/TGMDevelopment/RequisiteLaunchwrapper/blob/main/LICENSE
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
        EssentialAPI.getShutdownHookUtil().register(Thread {
            println("Deleting old ${Qaltils.MOD_NAME} jar file...")
            try {
                val runtime = getJavaRuntime()
                if (Util.getOSType() == Util.EnumOS.OSX) {
                    println("On Mac, trying to open mods folder")
                    Desktop.getDesktop().open(Qaltils.jarFile.parentFile)
                }
                println("Using runtime $runtime")
                val file = File("config/Wyvest/Deleter-1.2.jar")
                println("\"$runtime\" -jar \"${file.absolutePath}\" \"${Qaltils.jarFile.absolutePath}\"")
                Runtime.getRuntime()
                    .exec("\"$runtime\" -jar \"${file.absolutePath}\" \"${Qaltils.jarFile.absolutePath}\"")
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            Thread.currentThread().interrupt()
        })
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