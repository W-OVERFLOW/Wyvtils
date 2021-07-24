 package net.wyvest.wyvtilities.listeners

import club.chachy.event.forge.on
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import gg.essential.universal.ChatColor
import net.minecraft.client.audio.PositionedSound
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.mixin.AccessorPositionedSound
import net.wyvest.wyvtilities.utils.HypixelUtils
import xyz.matthewtgm.tgmlib.events.ActionBarEvent
import xyz.matthewtgm.tgmlib.events.StringRenderedEvent
import xyz.matthewtgm.tgmlib.util.HypixelHelper
import xyz.matthewtgm.tgmlib.util.ServerHelper
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern


 object Listener {
    private var worldLeft: Boolean = false
    private var victoryDetected = false
    lateinit var color: String
    var changeTextColor = false

    fun listen() {
        val chatEvent = on<ClientChatReceivedEvent>()
        val worldLeaveEvent = on<WorldEvent.Unload>()
        val tickEvent = on<TickEvent.ClientTickEvent>()
        val soundEvent = on<PlaySoundEvent>()
        val stringRenderedEvent = on<StringRenderedEvent>()
        if (WyvtilsConfig.autoGetAPI) {
            val shouldReturn = AtomicBoolean(false)
            /*/
            Adapted from Moulberry's NotEnoughUpdates, under the Attribution-NonCommercial 3.0 license.
            https://github.com/Moulberry/NotEnoughUpdates
            */
            chatEvent.filter { EnumChatFormatting.getTextWithoutFormattingCodes(it.message.unformattedText).startsWith("Your new API key is ") }
                .subscribe {
                    val unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(it.message.unformattedText)
                    val tempApiKey = unformattedText.substring("Your new API key is ".length)
                    Multithreading.runAsync {
                        if (!HypixelHelper.HypixelAPI.isValidKey(tempApiKey)
                        ) {
                            if (!ServerHelper.hypixel()) {
                                Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "You are not running this command on Hypixel, and thus the API key was not saved.")
                            } else {
                                Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "The API key was not valid! Please try again.")
                            }
                            shouldReturn.set(true)
                        }
                    }
                    if (shouldReturn.get()) return@subscribe
                    WyvtilsConfig.apiKey = unformattedText.substring("Your new API key is ".length)
                    WyvtilsConfig.markDirty()
                    WyvtilsConfig.writeData()
                    Wyvtilities.sendMessage(EnumChatFormatting.GREEN.toString() + "Your API Key has been automatically configured.")
                    return@subscribe
                }
            if (shouldReturn.get()) return
        }

        if (WyvtilsConfig.autoGetGEXP && WyvtilsConfig.isRegexLoaded) {
            if (!victoryDetected) {
                for (trigger in Wyvtilities.autoGGRegex) {
                    val triggerPattern = Pattern.compile(trigger.toString())
                    chatEvent.filter { triggerPattern.matcher(EnumChatFormatting.getTextWithoutFormattingCodes(it.message.unformattedText)).matches() }
                        .subscribe {
                            victoryDetected = true
                            Multithreading.runAsync {
                                if (HypixelUtils.getGEXP()) {
                                    EssentialAPI.getNotifications().push("Wyvtilities", "You currently have " + HypixelUtils.gexp + " guild EXP.")
                                } else {
                                    victoryDetected = false
                                    EssentialAPI.getNotifications().push("Wyvtilities", "There was a problem trying to get your GEXP.")
                                }
                            }}
                }
            }
        }
        worldLeaveEvent.filter { victoryDetected }
            .subscribe { victoryDetected = false }
        worldLeaveEvent.filter { worldLeft }
            .subscribe { victoryDetected = true }
        tickEvent.filter { changeTextColor }
            .subscribe {
                if (it.phase != TickEvent.Phase.START) return@subscribe
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
        if (WyvtilsConfig.soundBoost) {
            soundEvent.filter { it.result is PositionedSound && Wyvtilities.checkSound(it.name) }
                .subscribe {
                    (it.result as PositionedSound as AccessorPositionedSound).volume *= WyvtilsConfig.soundMultiplier
                }
        }
        if (WyvtilsConfig.highlightName) {
            stringRenderedEvent.filter { it.text != null && mc.theWorld != null && it.text.contains(mc.thePlayer.gameProfile.name) }
                .subscribe {
                    it.text = it.text.replace(mc.thePlayer.gameProfile.name, color + mc.thePlayer.gameProfile.name + EnumChatFormatting.RESET)
                }
        }
    }
}