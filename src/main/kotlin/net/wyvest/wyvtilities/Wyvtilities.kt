package net.wyvest.wyvtilities

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.util.InputUtil
import net.wyvest.wyvtilities.config.WyvtilsConfig
import org.lwjgl.glfw.GLFW
import java.io.File

object Wyvtilities : ClientModInitializer {
    override fun onInitializeClient() {
        if (!File(File(FabricLoader.getInstance().configDir.toFile(), "Wyvtilities"), "1.17.1").exists()) {
            File(File(FabricLoader.getInstance().configDir.toFile(), "Wyvtilities"), "1.17.1").mkdirs()
        }
        WyvtilsConfig.initialize()
        ClientTickEvents.END_CLIENT_TICK.register {
            if (it.currentScreen != null) return@register
            if (InputUtil.isKeyPressed(it.window.handle, GLFW.GLFW_KEY_Z)) {
                it.setScreen(WyvtilsConfig.gui())
            }
        }
    }
}