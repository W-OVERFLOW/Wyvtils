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

package xyz.qalcyo.wyvtils.eight

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import gg.essential.universal.ChatColor
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.lwjgl.input.Keyboard
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.requisite.core.events.FontRendererEvent
import xyz.qalcyo.requisite.core.integration.hypixel.events.LocrawReceivedEvent
import xyz.qalcyo.requisite.core.keybinds.KeyBind
import xyz.qalcyo.requisite.core.keybinds.KeyBinds
import xyz.qalcyo.wyvtils.core.utils.MinecraftVersions
import xyz.qalcyo.wyvtils.core.WyvtilsCore
import xyz.qalcyo.wyvtils.core.WyvtilsInfo
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.core.utils.Updater
import xyz.qalcyo.wyvtils.eight.commands.WyvtilsCommand
import xyz.qalcyo.wyvtils.eight.gui.DownloadGui
import xyz.qalcyo.wyvtils.eight.listener.HighlightManager
import xyz.qalcyo.wyvtils.eight.listener.Listener
import xyz.qalcyo.wyvtils.eight.mixin.AccessorGuiIngame
import xyz.qalcyo.wyvtils.eight.utils.HypixelUtils
import java.io.File

@Mod(
    modid = WyvtilsInfo.ID,
    name = WyvtilsInfo.NAME,
    version = WyvtilsInfo.VER,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Wyvtils {

    val mc: Minecraft
    get() = Minecraft.getMinecraft()

    fun sendMessage(message: String?) {
        Requisite.getInstance().chatHelper.send("${EnumChatFormatting.DARK_PURPLE}[${WyvtilsInfo.NAME}] ", message)
    }
    private var current: Int = 1
    val chatKeybind: KeyBind = KeyBinds.from("Chat Swapper", WyvtilsInfo.NAME, Keyboard.KEY_NONE) {
        when (current) {
            1 -> {
                check(WyvtilsConfig.chatType2)
                current += 1
            }
            2 -> {
                check(WyvtilsConfig.chatType1)
                current -= 1
            }
        }
    }
    val titleKeybind: KeyBind = KeyBinds.from("Clear Title", WyvtilsInfo.NAME, Keyboard.KEY_NONE) {
        (mc.ingameGUI as AccessorGuiIngame).displayedTitle = ""
        (mc.ingameGUI as AccessorGuiIngame).setDisplayedSubTitle("")
    }
    val sidebarKeybind: KeyBind = KeyBinds.from("Toggle Sidebar Temporarily", WyvtilsInfo.NAME, Keyboard.KEY_NONE) {
        WyvtilsConfig.sidebar = !WyvtilsConfig.sidebar
    }

    @Mod.EventHandler
    fun onPreInit(e: FMLPreInitializationEvent) {
        WyvtilsCore.modDir = File(File(File(File(Minecraft.getMinecraft().mcDataDir, "config"), "Qalcyo"), WyvtilsInfo.NAME), "1.8.9")
        if (!WyvtilsCore.modDir!!.exists()) WyvtilsCore.modDir!!.mkdirs()
        WyvtilsCore.jarFile = e.sourceFile
    }

    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        WyvtilsCore.onInitialization(MinecraftVersions.EIGHT)
        WyvtilsCommand.register()
        EVENT_BUS.register(Listener)
        if (WyvtilsConfig.highlightName) {
            HighlightManager.color = when (WyvtilsConfig.textColor) {
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
                15 -> ChatColor.WHITE.toString()
                else -> ""
            }
        }
        Requisite.getInstance().eventBus.register(FontRendererEvent.RenderStringEvent::class.java, HighlightManager::onStringRendered)
        Requisite.getInstance().eventBus.register(LocrawReceivedEvent::class.java, HypixelUtils::onLocraw)
        Requisite.getInstance().keyBindRegistry.register(chatKeybind)
        Requisite.getInstance().keyBindRegistry.register(titleKeybind)
        Requisite.getInstance().keyBindRegistry.register(sidebarKeybind)
    }

    @Mod.EventHandler
    fun onPostInit(e: FMLPostInitializationEvent) {
        WyvtilsCore.isPatcherLoaded = Loader.isModLoaded("patcher")
    }

    @Mod.EventHandler
    fun onLoadComplete(e: FMLLoadCompleteEvent) {
        Multithreading.runAsync {
            Updater.updateFuture!!.get()
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
    }

    private fun check(option: Int) {
        when (option) {
            0 -> mc.thePlayer.sendChatMessage("/chat a")
            1 -> mc.thePlayer.sendChatMessage("/chat p")
            2 -> mc.thePlayer.sendChatMessage("/chat g")
            3 -> mc.thePlayer.sendChatMessage("/chat o")
            else -> return
        }
    }
}