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

package xyz.qalcyo.wyvtils.listeners

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import gg.essential.universal.ChatColor
import net.minecraft.client.audio.PositionedSound
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import xyz.qalcyo.mango.Objects
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.requisite.core.events.FontRendererEvent
import xyz.qalcyo.wyvtils.Wyvtils
import xyz.qalcyo.wyvtils.Wyvtils.mc
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.mixin.PositionedSoundAccessor
import xyz.qalcyo.wyvtils.utils.HypixelUtils
import xyz.qalcyo.wyvtils.utils.containsAny
import xyz.qalcyo.wyvtils.utils.withoutFormattingCodes


object Listener {

    //private var current: Int = 1
    private var victoryDetected = false
    var color: String = ""
    var changeTextColor = false

    private val gameEndList = listOf(
        "Winner #1 (",
        "Top Survivors",
        "Winners - ",
        "Winners: ",
        "Winner: ",
        "Winning Team: ",
        " won the game!",
        "Top Seeker: ",
        "Last team standing!",
        "1st Place: ",
        "1st Killer - ",
        "1st Place - ",
        "Winner: ",
        " - Damage Dealt - ",
        "Winning Team -",
        "1st - ",
        " Duel - "
    )

    @SubscribeEvent
    fun onChatReceivedEvent(e: ClientChatReceivedEvent) {
        val unformattedText = e.message.unformattedText.withoutFormattingCodes()

        if (WyvtilsConfig.autoGetAPI) {
            /*/
            Adapted from Moulberry's NotEnoughUpdates, under the Attribution-NonCommercial 3.0 license.
            https://github.com/Moulberry/NotEnoughUpdates
            */
            //Stolen code starts here
            if (unformattedText.startsWith("Your new API key is ")) {
                val tempApiKey = unformattedText.substring("Your new API key is ".length)
                Multithreading.runAsync {
                    if (!HypixelUtils.isValidKey(tempApiKey)
                    ) {
                        if (!Requisite.getInstance().hypixelHelper.isOnHypixel) {
                            Wyvtils.sendMessage(EnumChatFormatting.RED.toString() + "You are not running this command on Hypixel! This mod needs an Hypixel API key!")
                        } else {
                            Wyvtils.sendMessage(EnumChatFormatting.RED.toString() + "The API Key was invalid! Please try running the command again.")
                        }
                    } else {
                        WyvtilsConfig.apiKey = unformattedText.substring("Your new API key is ".length)
                        WyvtilsConfig.markDirty()
                        WyvtilsConfig.writeData()
                        Wyvtils.sendMessage(EnumChatFormatting.GREEN.toString() + "Your API Key has been automatically configured.")
                    }
                }
            }
            //Stolen code ends here
        }
        if ((WyvtilsConfig.autoGetGEXP || WyvtilsConfig.autoGetWinstreak) && Requisite.getInstance().hypixelHelper.isOnHypixel) {
            if (!victoryDetected) {
                Multithreading.runAsync {
                    if (unformattedText.startsWith(" ")) {
                        for (triggers in gameEndList) {
                            if (unformattedText.contains(triggers)) {
                                victoryDetected = true
                                if (WyvtilsConfig.autoGetGEXP) {
                                    if (HypixelUtils.getGEXP()) {
                                        EssentialAPI.getNotifications()
                                            .push(
                                                "Wyvtils",
                                                "You currently have " + HypixelUtils.gexp + " guild EXP."
                                            )
                                    } else {
                                        EssentialAPI.getNotifications()
                                            .push("Wyvtils", "There was a problem trying to get your GEXP.")
                                    }
                                }
                                if (WyvtilsConfig.autoGetWinstreak) {
                                    if (HypixelUtils.getWinstreak()) {
                                        EssentialAPI.getNotifications().push(
                                            "Wyvtils",
                                            "You currently have a " + HypixelUtils.winstreak + " winstreak."
                                        )
                                    } else {
                                        EssentialAPI.getNotifications()
                                            .push("Wyvtils", "There was a problem trying to get your winstreak.")
                                    }
                                }
                                break
                            }
                        }
                    }
                }
            }
        }
        if (WyvtilsConfig.chatHightlight && e.message.formattedText != null && mc.theWorld != null && e.message.formattedText.contains(
                mc.thePlayer.gameProfile.name
            ) && WyvtilsConfig.highlightName && !changeTextColor
        ) {
            if (e.message is ChatComponentText) {
                mc.ingameGUI.chatGUI.printChatMessage(
                    replaceMessage(e.message, mc.thePlayer.name, color + mc.thePlayer.name + EnumChatFormatting.RESET)
                )
            } else {
                mc.ingameGUI.chatGUI.printChatMessage(
                    ChatComponentText(
                        e.message.formattedText.replace(
                            mc.thePlayer.name,
                            color + mc.thePlayer.name + EnumChatFormatting.RESET.toString(),
                            true
                        )
                    )
                )
            }
            e.isCanceled = true
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onChatLow(e : ClientChatReceivedEvent) {
        val unformattedText = e.message.unformattedText.withoutFormattingCodes()
        if (unformattedText.startsWith("{") && unformattedText.contains("server") && unformattedText.endsWith("}")) {
            e.isCanceled = WyvtilsConfig.hideLocraw
        }
    }

    @SubscribeEvent
    fun onWorldLeave(event: WorldEvent.Unload) {
        victoryDetected = false
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (WyvtilsConfig.firstTime && mc.theWorld != null) {
            EssentialAPI.getNotifications().push(
                "Wyvtils",
                "Hello! As this is your first time using this mod, type in /wyvtils in the chat to configure the many features in Wyvtils!"
            )
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
            val positionedSound = (e.result as PositionedSound as PositionedSoundAccessor)
            if (Wyvtils.checkSound(e.name) && WyvtilsConfig.soundBoost) {
                positionedSound.setVolume((e.result as PositionedSound).volume * WyvtilsConfig.soundMultiplier)
            } else if (WyvtilsConfig.soundBoost && e.name != "game.player.hurt") {
                positionedSound.setVolume((e.result as PositionedSound).volume / WyvtilsConfig.soundDecrease)
            }
        }
    }

    fun onStringRendered(e: FontRendererEvent.RenderEvent) {
        if (!WyvtilsConfig.chatHightlight && e.text != null && mc.theWorld != null && e.text.contains(mc.thePlayer.gameProfile.name) && WyvtilsConfig.highlightName && !changeTextColor) {
            if (e.text.containsAny("§", "\u00A7")) {
                e.text = smartReplaceStringWithName(e.text)
            } else {
                e.text = e.text.replace(
                    mc.thePlayer.gameProfile.name,
                    color + mc.thePlayer.gameProfile.name + EnumChatFormatting.RESET
                )
            }
        }
    }

    /*/
    @SubscribeEvent
    fun onKeyInput(event: BetterInputEvent.KeyboardInputEvent?) {
        if (mc.currentScreen != null) return
        if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == titleKeybind.keyCode) {
            titlePressed()
        }
        if (!ServerHelper.hypixel()) return
        if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == chatKeybind.keyCode) {
            chatPressed()
        }

    }

    @SubscribeEvent
    fun onMouseInput(event: BetterInputEvent.MouseInputEvent?) {
        if (mc.currentScreen != null) return
        if (Mouse.getEventButtonState() && Mouse.getEventButton() == titleKeybind.keyCode + 100) {
            titlePressed()
        }
        if (!ServerHelper.hypixel()) return
        if (Mouse.getEventButtonState() && Mouse.getEventButton() == chatKeybind.keyCode + 100) {
            chatPressed()
        }
    }

    private fun titlePressed() {
        (mc.ingameGUI as AccessorGuiIngame).displayedTitle = ""
        (mc.ingameGUI as AccessorGuiIngame).setDisplayedSubTitle("")
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

     */

    private fun replaceMessage(
        message: IChatComponent,
        username: String,
        replacement: String
    ): ChatComponentText {
        val originalText = message.unformattedTextForChat
        val copy = ChatComponentText(originalText).setChatStyle(message.chatStyle) as ChatComponentText
        for (sibling in message.siblings) {
            copy.appendSibling(
                ChatComponentText(
                    sibling.unformattedTextForChat.replace(
                        username,
                        replacement
                    )
                ).setChatStyle(sibling.chatStyle)
            )
        }
        return copy
    }

    private fun smartReplaceStringWithName(string: String): String {
        var number = -1
        var code: String? = null
        val array = string.split(Regex.fromLiteral(mc.thePlayer.gameProfile.name)).toMutableList()
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
        return join(array, color + mc.thePlayer.gameProfile.name + EnumChatFormatting.RESET)
    }
    private fun join(iterable: Iterable<*>, separator: String): String {
        return join(iterable.iterator(), separator)
    }

    private fun join(iterator: Iterator<*>, separator: String): String {
        if (!iterator.hasNext()) return ""
        val first = iterator.next()
        if (!iterator.hasNext()) return Objects.stringify(first)
        val buf = StringBuilder()
        if (first != null) buf.append(first)
        while (iterator.hasNext()) {
            buf.append(separator)
            val obj = iterator.next()
            if (obj != null) buf.append(obj)
        }
        return buf.toString()
    }
}
