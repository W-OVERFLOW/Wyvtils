package net.wyvest.wyvtilities.listeners

import gg.essential.api.EssentialAPI
import gg.essential.universal.ChatColor
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.GexpUtils
import xyz.matthewtgm.json.util.JsonApiHelper
import xyz.matthewtgm.tgmlib.util.Multithreading
import xyz.matthewtgm.tgmlib.util.Notifications
import xyz.matthewtgm.tgmlib.util.ServerHelper
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern


object ChatListener {
    var worldLeft: Boolean = false
    var victoryDetected = false
    lateinit var color: String
    var changeTextColor = false

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onMessageReceived(event: ClientChatReceivedEvent) {
        val unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.unformattedText)
        val name: String = mc.thePlayer.name.lowercase(Locale.ENGLISH)
        val text = unformattedText.lowercase(Locale.ENGLISH)
        if (WyvtilsConfig.highlightName) {
            if (text.contains(name)) {
                event.isCanceled = true
                if (EssentialAPI.getMinecraftUtil().isHypixel() && false ) { //false is a temp value as i dont have hytils compatability working
                    val pattern = Pattern.compile("^(?:[\\w\\- ]+ )?(?:(?<chatTypePrefix>[A-Za-z]+) > |)(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>\\w{1,16})(?: [\\w\\- ]+)?: (?<message>.+)\$")
                        .matcher(unformattedText)
                    if (pattern.matches()) {
                        mc.ingameGUI.chatGUI.printChatMessage(
                            ChatComponentText(
                                event.message.formattedText.replace(
                                    mc.thePlayer.name,
                                    color + mc.thePlayer.name + EnumChatFormatting.RESET.toString(),
                                    true
                                ).replace(pattern.group("chatTypePrefix"), "N")
                            )
                        )
                        return
                    }
                }
                mc.ingameGUI.chatGUI.printChatMessage(
                    ChatComponentText(
                        event.message.formattedText.replace(
                            mc.thePlayer.name,
                            color + mc.thePlayer.name + EnumChatFormatting.RESET.toString(),
                            true
                        )
                    )
                )
            }
        }
        if (WyvtilsConfig.autoGetAPI) {
            /*/
            Adapted from Moulberry's NotEnoughUpdates, under the Attribution-NonCommercial 3.0 license.
            https://github.com/Moulberry/NotEnoughUpdates
         */
            //Stolen code starts here
            if (unformattedText.startsWith("Your new API key is ")) {
                val tempApiKey = unformattedText.substring("Your new API key is ".length)
                val shouldReturn = AtomicBoolean(false)
                Multithreading.runAsync {
                    if (!JsonApiHelper.getJsonObject("https://api.hypixel.net/key?key=$tempApiKey").get("success").asBoolean
                    ) {
                        if (!ServerHelper.hypixel()) {
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
                return
            }
            //Stolen code ends here
        }

        if (WyvtilsConfig.autoGetGEXP && WyvtilsConfig.isRegexLoaded) {
            if (!victoryDetected) {
                for (trigger in Wyvtilities.autoGGRegex) {
                    val triggerPattern = Pattern.compile(trigger.toString())
                    if (triggerPattern.matcher(unformattedText).matches()) {
                        victoryDetected = true
                        Multithreading.runAsync {
                            GexpUtils.getGEXP()
                            Notifications.push("Wyvtilities", "You currently have " + GexpUtils.gexp + " guild EXP.")
                        }
                        return
                    }
                }
            }
        }
        return
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun discordInviteCheck(event: ClientChatReceivedEvent) {
        val unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.unformattedText)
        if (WyvtilsConfig.removeDiscordInvites) {
            if (ServerHelper.hypixel()) {
                val pattern =
                    Pattern.compile("^(?:[\\w\\- ]+ )?(?:(?<chatTypePrefix>[A-Za-z]+) > |)(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>\\w{1,16})(?: [\\w\\- ]+)?: (?<message>.+)\$")
                        .matcher(unformattedText)
                if (pattern.matches()) {
                    if (pattern.group("senderUsername") == mc.session.username) return
                    if (pattern.group("senderUsername") == null) return
                    if (pattern.group("senderUsername") != null && !pattern.group("message").contains("discord.gg/")) return
                    if (WyvtilsConfig.showInPartyChat && pattern.group("chatTypePrefix") != null) {
                        if (pattern.group("chatTypePrefix").startsWith("P")) {
                            return
                        }
                    } else if (WyvtilsConfig.showInGuildChat) {
                        if (pattern.group("chatTypePrefix").startsWith("G")) {
                            return
                        }
                    } else if (WyvtilsConfig.showInOfficerChat) {
                        if (pattern.group("chatTypePrefix").startsWith("O")) {
                            return
                        }
                    }
                    event.isCanceled = true
                    Notifications.push(
                        "Wyvtilities",
                        "Wyvtilities just prevented a discord invite being shown on your screen!"
                    )
                }
            } else {
                if (unformattedText.contains("discord.gg/")) {
                    event.isCanceled = true
                    Notifications.push(
                        "Wyvtilities",
                        "Wyvtilities just prevented a discord invite being shown on your screen!"
                    )
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLeave(event: WorldEvent.Unload) {
        victoryDetected = false
        worldLeft = true
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
}