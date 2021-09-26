package xyz.qalcyo.wyvtils

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import java.io.File


@Suppress("unused")
object Wyvtils : ClientModInitializer {
    var needsToCancel = false
    val modDir = File(File(File(FabricLoader.getInstance().configDir.toFile(), "Qalcyo"), "Wyvtils"), "1.17.1")
    lateinit var jarFile: File

    private val keyBinding: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.wyvtils.keybind", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_Z, // The keycode of the key
            "category.wyvtils.ok" // The translation key of the keybinding's category.
        )
    )

    override fun onInitializeClient() {
        if (!modDir.exists()) {
            modDir.mkdirs()
        }
        jarFile = File(javaClass.protectionDomain.codeSource.location.toURI())
        WyvtilsConfig.preload()
        ClientTickEvents.END_CLIENT_TICK.register {
            while (keyBinding.wasPressed() && it.currentScreen == null) {
                it.setScreen(WyvtilsConfig.gui())
                return@register
            }
        }
    }
}