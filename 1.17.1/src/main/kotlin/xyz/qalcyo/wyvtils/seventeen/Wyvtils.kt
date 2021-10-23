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

package xyz.qalcyo.wyvtils.seventeen

import gg.essential.api.EssentialAPI
import gg.essential.lib.kbrewster.eventbus.Subscribe
import gg.essential.universal.ChatColor
import gg.essential.universal.UDesktop
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import xyz.qalcyo.wyvtils.core.WyvtilsCore
import xyz.qalcyo.wyvtils.core.WyvtilsInfo
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.core.listener.Listener
import xyz.qalcyo.wyvtils.core.listener.events.ChatRefreshEvent
import xyz.qalcyo.wyvtils.core.utils.MinecraftVersions
import xyz.qalcyo.wyvtils.core.utils.Updater
import xyz.qalcyo.wyvtils.seventeen.gui.DownloadGui
import xyz.qalcyo.wyvtils.seventeen.mixin.ChatHudAccessor
import java.io.File
import java.net.URI

object Wyvtils : ClientModInitializer {

    var needsToCancel = false
    private val keyBinding: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.wyvtils.keybind", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_Z, // The keycode of the key
            "category.wyvtils.ok" // The translation key of the keybinding's category.
        )
    )

    override fun onInitializeClient() {
        WyvtilsCore.modDir =
            File(File(File(FabricLoader.getInstance().configDir.toFile(), "Qalcyo"), "Wyvtils"), "1.17.1")
        if (!WyvtilsCore.modDir!!.exists()) {
            WyvtilsCore.modDir!!.mkdirs()
        }
        WyvtilsCore.jarFile = File(javaClass.protectionDomain.codeSource.location.toURI())
        WyvtilsCore.onInitialization(MinecraftVersions.SEVENTEEN)
        ClientTickEvents.END_CLIENT_TICK.register {
            while (keyBinding.wasPressed() && it.currentScreen == null) {
                EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
                return@register
            }
        }
        Listener.color = when (WyvtilsConfig.textColor) {
            0 -> ChatColor.BLACK.toString()
            1 -> ChatColor.DARK_BLUE.toString()
            2 -> ChatColor.DARK_GREEN.toString()
            3 -> ChatColor.DARK_AQUA.toString()
            4 -> ChatColor.DARK_RED.toString()
            5 -> ChatColor.DARK_PURPLE.toString()
            6 -> ChatColor.GOLD.toString()
            7 -> ChatColor.GRAY.toString()
            8 -> ChatColor.DARK_GRAY.toString()
            9 -> ChatColor.BLUE.toString()
            10 -> ChatColor.GREEN.toString()
            11 -> ChatColor.AQUA.toString()
            12 -> ChatColor.RED.toString()
            13 -> ChatColor.LIGHT_PURPLE.toString()
            14 -> ChatColor.YELLOW.toString()
            else -> ChatColor.WHITE.toString()
        }
        WorldRenderEvents.END.register {
            if (Updater.shouldShowNotification) {
                EssentialAPI.getNotifications()
                    .push(
                        "Mod Update",
                        "${WyvtilsInfo.NAME} ${Updater.latestTag} is available!\nClick here to download it!",
                        5f
                    ) {
                        EssentialAPI.getGuiUtil().openScreen(DownloadGui())
                    }
                Updater.shouldShowNotification = false
            }
        }
        WyvtilsCore.eventBus.register(this)
    }

    @Subscribe
    fun onChatRefresh(e: ChatRefreshEvent) {
        val chat = MinecraftClient.getInstance().inGameHud.chatHud as ChatHudAccessor
        try {
            MinecraftClient.getInstance().inGameHud.chatHud.reset()
        } catch (e: Exception) {
            e.printStackTrace()
            EssentialAPI.getNotifications().push(
                WyvtilsInfo.NAME,
                "There was a critical error while trying to refresh the chat. Please go to inv.wtf/qalcyo or click on this notification to fix this issue."
            ) {
                UDesktop.browse(URI.create("https://inv.wtf/qalcyo"))
            }
            chat.visibleMessages.clear()
            MinecraftClient.getInstance().inGameHud.chatHud.resetScroll()
            for (i in chat.messages.asReversed()) {
                chat.invokeAddMessage(i.text, i.id, i.creationTick, true)
            }
        }
    }

}