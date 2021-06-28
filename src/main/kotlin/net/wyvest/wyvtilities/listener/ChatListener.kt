package net.wyvest.wyvtilities.listener

import gg.essential.universal.ChatColor
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.APIUtil
import net.wyvest.wyvtilities.utils.GexpUtils
import net.wyvest.wyvtilities.utils.Notifications
import net.wyvest.wyvtilities.utils.Utils
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean


object ChatListener {
    private var victoryDetected = false
    @SubscribeEvent
    fun onMessageReceived(event: ClientChatReceivedEvent) {

        val unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.unformattedText)
        /*/
            Adapted from Moulberry's NotEnoughUpdates, under the Attribution-NonCommercial 3.0 license.
            https://github.com/Moulberry/NotEnoughUpdates
         */
        if (Minecraft.getMinecraft().ingameGUI.displayedTitle.contains("victory", true)) {
            if (WyvtilsConfig.autoGetGEXP && !victoryDetected) {
                victoryDetected = true
                if (WyvtilsConfig.apiKey == "") {
                    Wyvtilities.sendMessage(ChatColor.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
                } else {
                    var gexp : String? = null
                    Wyvtilities.threadPool.submit{
                        try {
                            gexp = GexpUtils.getGEXP().toString()
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (gexp != null) {
                        Notifications.push("Wyvtilities", "You currently have $gexp guild EXP.")
                    } else {
                        Notifications.push("Wyvtilities", "There was an error trying to get your guild EXP.")
                    }
                }
            }
        }
        if (unformattedText.startsWith("Your new API key is ")) {
            val tempApiKey = unformattedText.substring("Your new API key is ".length)
            val shouldReturn = AtomicBoolean(false)
            Wyvtilities.threadPool.submit{
                if (!APIUtil.getJSONResponse("https://api.hypixel.net/key?key=$tempApiKey").get("success")
                        .asBoolean
                ) {
                    Utils.checkForHypixel()
                    if (!Utils.isOnHypixel) {
                        Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "You are not running this command on Hypixel! This mod needs an Hypixel API key!")
                    }
                    shouldReturn.set(true)
                }
            }
            if (shouldReturn.get()) return
            WyvtilsConfig.apiKey = unformattedText.substring("Your new API key is ".length)
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
            Wyvtilities.sendMessage(EnumChatFormatting.GREEN.toString() + "Your API Key has been automatically configured.")

        }
    }

    @SubscribeEvent
    fun onWorldEnter(event : WorldEvent.Load) {
        victoryDetected = false
    }

}