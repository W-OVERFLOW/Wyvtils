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

package xyz.qalcyo.qaltils

import gg.essential.api.EssentialAPI
import gg.essential.universal.ChatColor
import gg.essential.vigilance.data.PropertyData
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import xyz.matthewtgm.requisite.util.ChatHelper
import xyz.matthewtgm.requisite.util.ForgeHelper
import xyz.qalcyo.qaltils.commands.QaltilsCommands
import xyz.qalcyo.qaltils.config.QaltilsConfig
import xyz.qalcyo.qaltils.listeners.Listener
import xyz.qalcyo.qaltils.utils.HypixelUtils
import xyz.qalcyo.qaltils.utils.Updater
import xyz.qalcyo.qaltils.utils.equalsAny
import xyz.qalcyo.qaltils.utils.startsWithAny
import java.io.File


@Suppress("MemberVisibilityCanBePrivate")
@Mod(
    modid = Qaltils.MODID,
    name = Qaltils.MOD_NAME,
    version = Qaltils.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Qaltils {
    const val MODID = "qaltils"
    const val MOD_NAME = "Qaltils"
    const val VERSION = "1.2.0-BETA6"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()
    lateinit var jarFile: File
    val modDir = File(File(File(File(mc.mcDataDir, "config"), "Qalcyo"), "Qaltils"), "1.8.9")

    @JvmField
    var isConfigInitialized = false

    fun sendMessage(message: String?) {
        ChatHelper.sendMessage(EnumChatFormatting.DARK_PURPLE.toString() + "[Qaltils] ", message)
    }

    /*/
    val chatKeybind = KeyBinding("Chat Swapper", Keyboard.KEY_V, "Qaltils")
    val titleKeybind = KeyBinding("Clear Title", Keyboard.KEY_Z, "Qaltils")
     */

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    private fun onFMLInitialization(event: FMLInitializationEvent) {
        QaltilsConfig.preload()
        //EntityConfig.preload()
        isConfigInitialized = true
        if (QaltilsConfig.highlightName) {
            Listener.color = when (QaltilsConfig.textColor) {
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
        QaltilsCommands.register()
        /*/
        ClientRegistry.registerKeyBinding(chatKeybind)
        ClientRegistry.registerKeyBinding(titleKeybind)
         */
    }

    @Mod.EventHandler
    private fun onFMLLoad(event: FMLLoadCompleteEvent) {
        if (ForgeHelper.isModLoaded("bossbar_customizer")) {
            QaltilsConfig.bossBarCustomization = false
            QaltilsConfig.markDirty()
            QaltilsConfig.writeData()
            EssentialAPI.getNotifications().push(
                "Qaltils",
                "Bossbar Customizer (the mod) has been detected, and so the Qaltils Bossbar related features have been disabled."
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
