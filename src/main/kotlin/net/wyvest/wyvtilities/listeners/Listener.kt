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
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.HypixelUtils
import xyz.matthewtgm.json.util.JsonApiHelper
import xyz.matthewtgm.tgmlib.events.StringRenderedEvent
import xyz.matthewtgm.tgmlib.tweaker.hooks.TGMLibPositionedSoundAccessor
import xyz.matthewtgm.tgmlib.util.ServerHelper
import java.util.regex.Pattern

object Listener {
    private var victoryDetected = false
    lateinit var color: String
    var changeTextColor = false

    @SubscribeEvent
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
                    if (!JsonApiHelper.getJsonObject("https://api.hypixel.net/key?key=$tempApiKey")
                            .get("success").asBoolean
                    ) {
                        if (!ServerHelper.hypixel()) {
                            Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "You are not running this command on Hypixel! This mod needs an Hypixel API key!")
                        }
                        shouldReturn = true
                    }
                }
                if (shouldReturn) return
                WyvtilsConfig.apiKey = unformattedText.substring("Your new API key is ".length)
                WyvtilsConfig.markDirty()
                WyvtilsConfig.writeData()
                Wyvtilities.sendMessage(EnumChatFormatting.GREEN.toString() + "Your API Key has been automatically configured.")
                return
            }
            //Stolen code ends here
        }
        if ((WyvtilsConfig.autoGetGEXP || WyvtilsConfig.autoGetWinstreak) && Wyvtilities.isRegexLoaded) {
            if (!victoryDetected) {
                for (trigger in Wyvtilities.autoGGRegex) {
                    val triggerPattern = Pattern.compile(trigger.toString())
                    if (triggerPattern.matcher(unformattedText).matches()) {
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
                            if (WyvtilsConfig.autoGetWinstreak) {
                                if (HypixelUtils.getWinstreak()) {
                                    EssentialAPI.getNotifications().push(
                                        "Wyvtilities",
                                        "You currently have a " + HypixelUtils.winstreak + " winstreak."
                                    )
                                } else {
                                    EssentialAPI.getNotifications()
                                        .push("Wyvtilities", "There was a problem trying to get your winstreak.")
                                }
                            }
                        }
                        return
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLeave(event: WorldEvent.Unload) {
        victoryDetected = false
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
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
                    15 -> ChatColor.WHITE.toString()
                    else -> ""
                }
                changeTextColor = false
            }
        }
    }

    @SubscribeEvent
    fun onSoundPlayed(e : PlaySoundEvent) {
        if (e.result is PositionedSound && Wyvtilities.checkSound(e.name) && WyvtilsConfig.soundBoost) {
            (e.result as PositionedSound as TGMLibPositionedSoundAccessor).setVolume((e.result as PositionedSound).volume * WyvtilsConfig.soundMultiplier)
        }
    }
    @SubscribeEvent
    fun onStringRendered(e : StringRenderedEvent) {
        if (e.text != null && mc.theWorld != null && e.text.contains(mc.thePlayer.gameProfile.name) && WyvtilsConfig.highlightName) {
            e.text = e.text.replace(mc.thePlayer.gameProfile.name, color + mc.thePlayer.gameProfile.name + EnumChatFormatting.RESET)
        }
    }
}