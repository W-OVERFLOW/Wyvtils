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

package xyz.qalcyo.rysm

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
import xyz.qalcyo.requisite.core.keybinds.KeyBind
import xyz.qalcyo.requisite.core.keybinds.KeyBinds
import xyz.qalcyo.rysm.commands.RysmCommands
import xyz.qalcyo.rysm.config.RysmConfig
import xyz.qalcyo.rysm.listeners.HighlightManager
import xyz.qalcyo.rysm.listeners.Listener
import xyz.qalcyo.rysm.mixin.AccessorGuiIngame
import xyz.qalcyo.rysm.utils.HypixelUtils
import xyz.qalcyo.rysm.utils.Updater
import java.io.File


@Suppress("MemberVisibilityCanBePrivate")
@Mod(
    modid = Rysm.MODID,
    name = Rysm.MOD_NAME,
    version = Rysm.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Rysm {
    const val MODID = "@ID@"
    const val MOD_NAME = "@NAME@"
    const val VERSION = "@VER@"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()
    lateinit var jarFile: File
    val modDir = File(File(File(File(mc.mcDataDir, "config"), "Qalcyo"), MOD_NAME), "1.8.9")
    @JvmField
    var isConfigInitialized = false

    fun sendMessage(message: String?) {
        Requisite.getInstance().chatHelper.send("${EnumChatFormatting.DARK_PURPLE}[$MOD_NAME] ", message)
    }

    val titleKeybind: KeyBind = KeyBinds.from("Clear Title", MOD_NAME, Keyboard.KEY_NONE) {
        (mc.ingameGUI as AccessorGuiIngame).displayedTitle = ""
        (mc.ingameGUI as AccessorGuiIngame).setDisplayedSubTitle("")
    }
    val sidebarKeybind: KeyBind = KeyBinds.from("Toggle Sidebar Temporarily", MOD_NAME, Keyboard.KEY_NONE) {
        RysmConfig.sidebar = !RysmConfig.sidebar
    }

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    private fun onFMLInitialization(event: FMLInitializationEvent) {
        RysmConfig.preload()
        isConfigInitialized = true
        if (RysmConfig.highlightName) {
            Listener.color = when (RysmConfig.textColor) {
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
        Requisite.getInstance().eventBus.register(HighlightManager)
        Requisite.getInstance().eventBus.register(HypixelUtils)
        RysmCommands.register()
        Requisite.getInstance().keyBindRegistry.register(titleKeybind)
        Requisite.getInstance().keyBindRegistry.register(sidebarKeybind)
    }

    @Mod.EventHandler
    private fun onFMLLoad(event: FMLLoadCompleteEvent) {
        if (Loader.isModLoaded("bossbar_customizer") && RysmConfig.bossBarCustomization) {
            RysmConfig.bossBarCustomization = false
            RysmConfig.markDirty()
            RysmConfig.writeData()
            Requisite.getInstance().notifications.push(
                MOD_NAME,
                "Bossbar Customizer (the mod) has been detected, and so the $MOD_NAME Bossbar related features have been disabled."
            )
        }
        Updater.update()
    }

}
