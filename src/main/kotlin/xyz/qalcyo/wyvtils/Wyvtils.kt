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

package xyz.qalcyo.wyvtils

import gg.essential.api.EssentialAPI
import gg.essential.universal.ChatColor
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.lwjgl.input.Keyboard
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.requisite.core.events.FontRendererEvent
import xyz.qalcyo.requisite.core.integration.hypixel.events.LocrawReceivedEvent
import xyz.qalcyo.requisite.core.keybinds.KeyBind
import xyz.qalcyo.requisite.core.keybinds.KeyBinds
import xyz.qalcyo.wyvtils.commands.WyvtilsCommands
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.listeners.HighlightManager
import xyz.qalcyo.wyvtils.listeners.Listener
import xyz.qalcyo.wyvtils.mixin.AccessorGuiIngame
import xyz.qalcyo.wyvtils.utils.HypixelUtils
import xyz.qalcyo.wyvtils.utils.Updater
import java.io.File


@Suppress("MemberVisibilityCanBePrivate")
@Mod(
    modid = Wyvtils.MODID,
    name = Wyvtils.MOD_NAME,
    version = Wyvtils.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Wyvtils {
    const val MODID = "@ID@"
    const val MOD_NAME = "@NAME@"
    const val VERSION = "@VER@"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()
    lateinit var jarFile: File
    val modDir = File(File(File(File(mc.mcDataDir, "config"), "Qalcyo"), MOD_NAME), "1.8.9")
    private var current: Int = 1
    @JvmField
    var isConfigInitialized = false

    fun sendMessage(message: String?) {
        Requisite.getInstance().chatHelper.send("${EnumChatFormatting.DARK_PURPLE}[$MOD_NAME] ", message)
    }

    val chatKeybind: KeyBind = KeyBinds.from("Chat Swapper", MOD_NAME, Keyboard.KEY_NONE) {
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
    val titleKeybind: KeyBind = KeyBinds.from("Clear Title", MOD_NAME, Keyboard.KEY_NONE) {
        (mc.ingameGUI as AccessorGuiIngame).displayedTitle = ""
        (mc.ingameGUI as AccessorGuiIngame).setDisplayedSubTitle("")
    }
    val sidebarKeybind: KeyBind = KeyBinds.from("Toggle Sidebar Temporarily", MOD_NAME, Keyboard.KEY_NONE) {
        WyvtilsConfig.sidebar = !WyvtilsConfig.sidebar
    }

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    private fun onFMLInitialization(event: FMLInitializationEvent) {
        WyvtilsConfig.preload()
        isConfigInitialized = true
        if (WyvtilsConfig.highlightName) {
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
                15 -> ChatColor.WHITE.toString()
                else -> ""
            }
        }
        EVENT_BUS.register(Listener)
        EVENT_BUS.register(HypixelUtils)
        Requisite.getInstance().eventBus.register(LocrawReceivedEvent::class.java, HypixelUtils::onLocraw)
        Requisite.getInstance().eventBus.register(FontRendererEvent.RenderStringEvent::class.java, HighlightManager::onStringRendered)
        WyvtilsCommands.register()
        Requisite.getInstance().keyBindRegistry.register(chatKeybind)
        Requisite.getInstance().keyBindRegistry.register(titleKeybind)
        Requisite.getInstance().keyBindRegistry.register(sidebarKeybind)
    }

    @Mod.EventHandler
    private fun onFMLLoad(event: FMLLoadCompleteEvent) {
        if (Loader.isModLoaded("bossbar_customizer") && WyvtilsConfig.bossBarCustomization) {
            WyvtilsConfig.bossBarCustomization = false
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
            EssentialAPI.getNotifications().push(
                MOD_NAME,
                "Bossbar Customizer (the mod) has been detected, and so the $MOD_NAME Bossbar related features have been disabled."
            )
        }
        Updater.update()
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
