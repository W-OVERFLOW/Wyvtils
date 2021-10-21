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

package xyz.qalcyo.wyvtils.eight.listener

import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import xyz.qalcyo.wyvtils.core.WyvtilsCore.changeTextColor
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.eight.Wyvtils.mc
import xyz.qalcyo.wyvtils.eight.listener.HighlightManager.color


object Listener {

    @SubscribeEvent
    fun onChatReceivedEvent(e: ClientChatReceivedEvent) {
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