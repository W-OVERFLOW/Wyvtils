package net.wyvest.wyvtilities

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.wyvest.wyvtilities.config.WyvtilsConfig
import java.io.File

@Suppress("unused")
object Wyvtilities : ClientModInitializer {
    override fun onInitializeClient() {
        if (!File(File(FabricLoader.getInstance().configDir.toFile(), "Wyvtilities"), "1.17.1").exists()) {
            File(File(FabricLoader.getInstance().configDir.toFile(), "Wyvtilities"), "1.17.1").mkdirs()
        }
        WyvtilsConfig.initialize()
    }
}