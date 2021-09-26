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
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import xyz.matthewtgm.requisite.util.ChatHelper
import xyz.matthewtgm.requisite.util.ForgeHelper
import xyz.qalcyo.wyvtils.commands.WyvtilsCommands
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.listeners.Listener
import xyz.qalcyo.wyvtils.utils.HypixelUtils
import xyz.qalcyo.wyvtils.utils.Updater
import xyz.qalcyo.wyvtils.utils.equalsAny
import xyz.qalcyo.wyvtils.utils.startsWithAny
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
    const val MODID = "wyvtils"
    const val MOD_NAME = "Wyvtils"
    const val VERSION = "1.2.0-BETA7"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()
    lateinit var jarFile: File
    val modDir = File(File(File(File(mc.mcDataDir, "config"), "Qalcyo"), "Wyvtils"), "1.8.9")

    @JvmField
    var isConfigInitialized = false

    fun sendMessage(message: String?) {
        ChatHelper.sendMessage("${EnumChatFormatting.DARK_PURPLE}[Wyvtils] ", message)
    }

    /*/
    val chatKeybind = KeyBinding("Chat Swapper", Keyboard.KEY_V, "Wyvtils")
    val titleKeybind = KeyBinding("Clear Title", Keyboard.KEY_Z, "Wyvtils")
     */

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
        ForgeHelper.registerEventListeners(Listener, HypixelUtils)
        WyvtilsCommands.register()
        /*/
        ClientRegistry.registerKeyBinding(chatKeybind)
        ClientRegistry.registerKeyBinding(titleKeybind)
         */
    }

    @Mod.EventHandler
    private fun onFMLLoad(event: FMLLoadCompleteEvent) {
        if (ForgeHelper.isModLoaded("bossbar_customizer") && WyvtilsConfig.bossBarCustomization) {
            WyvtilsConfig.bossBarCustomization = false
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
            EssentialAPI.getNotifications().push(
                "Wyvtils",
                "Bossbar Customizer (the mod) has been detected, and so the Wyvtils Bossbar related features have been disabled."
            )
        }
        Updater.update()
    }

    fun checkSound(name: String): Boolean {
        if (name.equalsAny(
                "random.successful_hit",
                "random.break",
                "random.drink",
                "random.eat",
                "random.bow",
                "random.bowhit",
                "mob.ghast.fireball",
                "mob.ghast.charge"
            ) || (name.startsWithAny("dig.", "step.", "game.player.") && name != "game.player.hurt")
        ) {
            return true
        }
        return false
    }

}
