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

package net.wyvest.wyvtilities

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.wyvest.wyvtilities.utils.Updater
import java.io.File


@Suppress("MemberVisibilityCanBePrivate")
@Mod(
    modid = Wyvtilities.MODID,
    name = Wyvtilities.MOD_NAME,
    version = Wyvtilities.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Wyvtilities {
    const val MODID = "wyvtilities"
    const val MOD_NAME = "Wyvtilities"
    const val VERSION = "1.1.3"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()
    lateinit var jarFile: File

    val modDir = File(File(mc.mcDataDir, "config"), "W-OVERFLOW")

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    private fun onFMLInitialization(event: FMLInitializationEvent) {
        Updater.download(
            "https://github.com/Wyvest/Wyvtils/releases/download/v2.0.0/Wyvtils-1.8.9-2.0.0.jar",
            File(
                "mods/Wyvtils-1.8.9-2.0.0.jar"
            )
        )
        Updater.download(
            "https://github.com/Wyvest/Deleter/releases/download/v1.2/Deleter-1.2.jar",
            File(File(modDir, "Libraries"), "Deleter-1.2.jar")
        )
        Updater.addShutdownHook()
    }

}