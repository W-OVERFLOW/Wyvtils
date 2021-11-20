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

package net.wyvest.wyvtilities.listeners

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import gg.essential.universal.ChatColor
import net.minecraft.client.audio.PositionedSound
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.Wyvtilities.chatKeybind
import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.Wyvtilities.sendMessage
import net.wyvest.wyvtilities.Wyvtilities.titleKeybind
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.mixin.AccessorGuiIngame
import net.wyvest.wyvtilities.mixin.AccessorPositionedSound
import net.wyvest.wyvtilities.utils.APIUtil
import net.wyvest.wyvtilities.utils.HypixelUtils
import net.wyvest.wyvtilities.utils.containsAny
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse


object Listener {
    private var removeTitle = false
    private var current: Int = 1
    private var victoryDetected = false
    var color: String = ""
    var changeTextColor = false

    @SubscribeEvent(receiveCanceled = true)
    fun onChatReceivedEvent(e: ClientChatReceivedEvent) {
        val unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(e.message.unformattedText)
        if (WyvtilsConfig.autoGetAPI) {
            /*/
            Adapted from Moulberry's NotEnoughUpdates, under the Attribution-NonCommercial 3.0 license.
            https://github.com/Moulberry/NotEnoughUpdates
         */
            //Stolen code starts here
            if (unformattedText.startsWith("Your new API key is ")) {
                val tempApiKey = unformattedText.substring("Your new API key is ".length)
                var shouldReturn = false
                Multithreading.runAsync {
                    if (!APIUtil.getJSONResponse("https://api.hypixel.net/key?key=$tempApiKey")
                            .get("success").asBoolean
                    ) {
                        if (!EssentialAPI.getMinecraftUtil().isHypixel()) {
                            sendMessage(EnumChatFormatting.RED.toString() + "You are not running this command on Hypixel! This mod needs an Hypixel API key!")
                        }
                        shouldReturn = true
                    }
                }
                if (shouldReturn) return
                WyvtilsConfig.apiKey = unformattedText.substring("Your new API key is ".length)
                WyvtilsConfig.markDirty()
                WyvtilsConfig.writeData()
                sendMessage(EnumChatFormatting.GREEN.toString() + "Your API Key has been automatically configured.")
                return
            }
            //Stolen code ends here
        }
        if ((WyvtilsConfig.autoGetGEXP) && Wyvtilities.isRegexLoaded && EssentialAPI.getMinecraftUtil().isHypixel()) {
            if (!victoryDetected) {
                for (trigger in Wyvtilities.autoGGRegex) {
                    if (trigger.matches(unformattedText)) {
                        victoryDetected = true
                        Multithreading.runAsync {
                            if (WyvtilsConfig.autoGetGEXP) {
                                if (HypixelUtils.getGEXP()) {
                                    EssentialAPI.getNotifications()
                                        .push("Wyvtilities", "You currently have " + HypixelUtils.gexp + " guild EXP.")
                                } else {
                                    EssentialAPI.getNotifications()
                                        .push("Wyvtilities", "There was a problem trying to get your GEXP.")
                                }
                            }
                        }
                        return
                    }
                }
            }
            if (!victoryDetected) {
                if (EnumChatFormatting.getTextWithoutFormattingCodes((mc.ingameGUI as AccessorGuiIngame).displayedTitle)
                        .containsAny("win", "over", "won", "victory")
                ) {
                    victoryDetected = true
                    removeTitle = true
                    Multithreading.runAsync {
                        if (WyvtilsConfig.autoGetGEXP) {
                            if (HypixelUtils.getGEXP()) {
                                EssentialAPI.getNotifications()
                                    .push("Wyvtilities", "You currently have " + HypixelUtils.gexp + " guild EXP.")
                            } else {
                                EssentialAPI.getNotifications()
                                    .push("Wyvtilities", "There was a problem trying to get your GEXP.")
                            }
                        }
                    }
                    return
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLeave(event: WorldEvent.Unload) {
        victoryDetected = false
    }

    @SubscribeEvent
    fun onWorldJoin(event: WorldEvent.Load) {
        if (removeTitle) {
            removeTitle = false
            (mc.ingameGUI as AccessorGuiIngame).displayedTitle = ""
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (WyvtilsConfig.firstTime && mc.theWorld != null) {
            EssentialAPI.getNotifications().push("Wyvtilities", "Hello! As this is your first time using this mod, type in /wyvtils in the chat to configure the many features in Wyvtilities!")
            WyvtilsConfig.firstTime = false
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
        }
        if (changeTextColor) {
            if (mc.currentScreen != WyvtilsConfig.gui()) {
                color = when (WyvtilsConfig.textColor) {
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
                changeTextColor = false
            }
        }
    }

    @SubscribeEvent
    fun onSoundPlayed(e: PlaySoundEvent) {
        if (e.result is PositionedSound) {
            val positionedSound = (e.result as PositionedSound as AccessorPositionedSound)
            if (Wyvtilities.checkSound(e.name) && WyvtilsConfig.soundBoost) {
                positionedSound.setVolume((e.result as PositionedSound).volume * WyvtilsConfig.soundMultiplier)
            } else if (WyvtilsConfig.soundBoost) {
                positionedSound.setVolume((e.result as PositionedSound).volume / WyvtilsConfig.soundDecrease)
            }
        }
    }

    @SubscribeEvent
    fun onStringRendered(e: FontRendererEvent) {
        if (e.text != null && mc.theWorld != null && e.text!!.contains(mc.thePlayer.gameProfile.name) && WyvtilsConfig.highlightName && !changeTextColor) {
            if (e.text.containsAny("§", "\u00A7")) {
                var number = -1
                var code: String? = null
                val array = e.text!!.split(mc.thePlayer.gameProfile.name).toMutableList()
                for (split in array) {
                    number += 1
                    if (number % 2 == 0 || number == 0) {
                        val subString = split.substringAfterLast("\u00A7", null.toString())
                        code = if (subString != "null") {
                            subString.first().toString()
                        } else {
                            null
                        }
                        continue
                    } else {
                        if (code != null) {
                            array[number] = "\u00A7$code" + array[number]
                        }
                    }
                }
                e.text = join(array, color + mc.thePlayer.gameProfile.name + EnumChatFormatting.RESET)
            } else {
                e.text = e.text!!.replace(
                    mc.thePlayer.gameProfile.name,
                    color + mc.thePlayer.gameProfile.name + EnumChatFormatting.RESET
                )
            }
        }
    }

    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (mc.currentScreen != null) return
        if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == titleKeybind.keyCode) {
            titlePressed()
        }
        if (!EssentialAPI.getMinecraftUtil().isHypixel()) return
        if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == chatKeybind.keyCode) {
            chatPressed()
        }

    }

    @SubscribeEvent
    fun onMouseInput(event: InputEvent.MouseInputEvent) {
        if (mc.currentScreen != null) return
        if (Mouse.getEventButtonState() && Mouse.getEventButton() == titleKeybind.keyCode + 100) {
            titlePressed()
        }
        if (!EssentialAPI.getMinecraftUtil().isHypixel()) return
        if (Mouse.getEventButtonState() && Mouse.getEventButton() == chatKeybind.keyCode + 100) {
            chatPressed()
        }
    }

    private fun titlePressed() {
        (mc.ingameGUI as AccessorGuiIngame).displayedTitle = ""
    }

    private fun chatPressed() {
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

    private fun check(option: Int) {
        when (option) {
            0 -> mc.thePlayer.sendChatMessage("/chat a")
            1 -> mc.thePlayer.sendChatMessage("/chat p")
            2 -> mc.thePlayer.sendChatMessage("/chat g")
            3 -> mc.thePlayer.sendChatMessage("/chat o")
            else -> return
        }
    }

    /**
     * @param iterable The iterable to check all elements of.
     * @param separator The seperator to join with.
     * @return The string joined from the iterable.
     */
    fun join(iterable: Iterable<*>?, separator: String?): String? {
        return if (iterable == null) null else join(iterable.iterator(), separator)
    }

    private fun join(iterator: Iterator<*>?, separator: String?): String? {
        if (iterator == null) return null
        if (!iterator.hasNext()) return ""
        val first = iterator.next()
        if (!iterator.hasNext()) return first?.toString() ?: ""
        val buf = StringBuilder()
        if (first != null) buf.append(first)
        while (iterator.hasNext()) {
            if (separator != null) buf.append(separator)
            val obj = iterator.next()
            if (obj != null) buf.append(obj)
        }
        return buf.toString()
    }
}