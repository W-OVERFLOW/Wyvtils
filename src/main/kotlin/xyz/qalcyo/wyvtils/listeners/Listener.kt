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
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.wyvtils.Wyvtils
import xyz.qalcyo.wyvtils.Wyvtils.mc
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.utils.HypixelUtils
import xyz.qalcyo.wyvtils.utils.withoutFormattingCodes


object Listener {

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


}
