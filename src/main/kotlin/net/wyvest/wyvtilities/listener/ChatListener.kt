package net.wyvest.wyvtilities.listener

import gg.essential.universal.ChatColor
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.Wyvtilities.Companion.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern


object ChatListener {
    private var victoryDetected = false
    lateinit var color : String
    var changeTextColor = false
    //stolen regexes from Hychat (ty moulberry)
    const val PARTY_TALK = "Party > (.*)"
    const val PARTY_TALK_HYTILS = "P > (.*)"

    //ok now these are mine although very based off of hychat regex
    const val GUILD_TALK = "Guild > (.*)"
    const val GUILD_TALK_HYTILS = "G > (.*)"
    const val OFFICER_TALK = "Officer > (.*)"
    const val OFFICER_TALK_HYTILS = "O > (.*)"

    @SubscribeEvent
    fun onMessageReceived(event: ClientChatReceivedEvent) {
        val unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.unformattedText)
        val name: String = mc.thePlayer.name.lowercase(Locale.ENGLISH)
        val text = unformattedText.lowercase(Locale.ENGLISH)
        if (WyvtilsConfig.highlightName) {
            if (text.contains(name)) {
                event.isCanceled = true
                mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(event.message.formattedText.replace(mc.thePlayer.name, color + mc.thePlayer.name + EnumChatFormatting.RESET.toString(), true)))
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
                Wyvtilities.threadPool.submit {
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
                return
            }
            //Stolen code ends here
        }

        if (WyvtilsConfig.removeDiscordInvites) {
            if (Pattern.compile("(.*)(?i)discord.gg(?-i)/(.*)").matcher(unformattedText).matches()) {
                if (WyvtilsConfig.showInPartyChat) {
                    val partyPattern = Pattern.compile(PARTY_TALK)
                    val partyHytilsPattern = Pattern.compile(PARTY_TALK_HYTILS)
                    if (partyHytilsPattern.matcher(unformattedText).matches() || partyPattern.matcher(unformattedText).matches()) {
                        return
                    }
                } else if (WyvtilsConfig.showInGuildChat) {
                    val guildPattern = Pattern.compile(GUILD_TALK)
                    val guildHytilsPattern = Pattern.compile(GUILD_TALK_HYTILS)
                    if (guildPattern.matcher(unformattedText).matches() || guildHytilsPattern.matcher(unformattedText).matches()) {
                        return
                    }
                } else if (WyvtilsConfig.showInOfficerChat) {
                    val officerPattern = Pattern.compile(OFFICER_TALK)
                    val officerHytilsPattern = Pattern.compile(OFFICER_TALK_HYTILS)
                    if (officerPattern.matcher(unformattedText).matches() || officerHytilsPattern.matcher(unformattedText).matches()) {
                        return
                    }
                }
                event.isCanceled = true
                Notifications.push("Wyvtilities", "Wyvtilities just prevented a discord invite being shown on your screen!")
            }
        }

        if (WyvtilsConfig.autoGetGEXP && WyvtilsConfig.isRegexLoaded) {
            if (!victoryDetected) {
                for (trigger in Wyvtilities.autoGGRegex) {
                    val triggerPattern = Pattern.compile(trigger.toString())
                    if (triggerPattern.matcher(unformattedText).matches()) {
                        victoryDetected = true
                        Wyvtilities.threadPool.submit {
                            GexpUtils.getGEXP()
                            Notifications.push("Wyvtilities", "You currently have " + GexpUtils.gexp + " guild EXP.")
                        }
                        return
                    }
                }
            }
            if (mc.ingameGUI.displayedTitle.containsAny("win", "won", "over", "end", "victory") && !victoryDetected) {
                victoryDetected = true
                Wyvtilities.threadPool.submit {
                    GexpUtils.getGEXP()
                    Notifications.push("Wyvtilities", "You currently have " + GexpUtils.gexp + " guild EXP.")
                }
                return
            }
        }
        return
    }

    @SubscribeEvent
    fun onWorldLeave(event: WorldEvent.Unload) {
        mc.ingameGUI.displayedTitle = ""
        mc.ingameGUI.displayedSubTitle = ""
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
}