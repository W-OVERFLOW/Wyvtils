package net.wyvest.wyvtilities.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.DisplayName
import gg.essential.api.commands.SubCommand
import gg.essential.api.utils.Multithreading
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.HypixelUtils
import xyz.matthewtgm.json.util.JsonApiHelper

@Suppress("unused")
object WyvtilsCommands : Command("wyvtilities", true) {

    override val commandAliases = setOf(Alias("wyvtils"), Alias("wytils"), Alias("wyvtil"), Alias("wytil"))

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for Wyvtils")
    fun config() {
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

    @SubCommand("setkey", description = "Sets the API key for Wyvtils.")
    fun setKey(@DisplayName("api key") apiKey : String) {
        Multithreading.runAsync {
            try {
                if (JsonApiHelper.getJsonObject("https://api.hypixel.net/key?key=$apiKey")
                        .get("success").asBoolean
                ) {
                    WyvtilsConfig.apiKey = apiKey
                    WyvtilsConfig.markDirty()
                    WyvtilsConfig.writeData()
                    Wyvtilities.sendMessage(EnumChatFormatting.GREEN.toString() + "Saved API key successfully!")
                } else {
                    Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "Invalid API key! Please try again.")
                }
            } catch (ex: Throwable) {
                Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "Invalid API key! Please try again.")
                ex.printStackTrace()
            }
        }
    }

    @SubCommand("gexp", description = "Gets the GEXP of the player specified")
    fun gexp(@DisplayName("username") username : String?) {
        if (WyvtilsConfig.apiKey.isEmpty()) {
            Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
        } else {
            try {
                Multithreading.runAsync {
                    if (username != null) {
                        if (HypixelUtils.getGEXP(username)) {
                            EssentialAPI.getNotifications()
                                .push("Wyvtilities", "$username currently has " + HypixelUtils.gexp + " guild EXP.")
                        } else {
                            EssentialAPI.getNotifications()
                                .push("Wyvtilities", "There was a problem trying to get $username's GEXP.")
                        }
                    } else {
                        if (HypixelUtils.getGEXP()) {
                            EssentialAPI.getNotifications().push("Wyvtilities", "You currently have " + HypixelUtils.gexp + " guild EXP.")
                        } else {
                            EssentialAPI.getNotifications().push("Wyvtilities", "There was a problem trying to get your GEXP.")
                        }
                    }
                }
            } catch (e : NullPointerException) {
                e.printStackTrace()
            }

        }
    }
    /*/
    @SubCommand("winstreak", description = "Gets the winstreak of the player specified")
    fun winstreak(@DisplayName("username") username : String?) {
        if (WyvtilsConfig.apiKey.isEmpty()) {
            Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
        } else {
            try {
                Multithreading.runAsync {
                    if (username != null) {
                        if (HypixelUtils.getWinstreak(username)) {
                            EssentialAPI.getNotifications()
                                .push("Wyvtilities", "$username currently has " + HypixelUtils.gexp + " guild EXP.")
                        } else {
                            EssentialAPI.getNotifications()
                                .push("Wyvtilities", "There was a problem trying to get $username's GEXP.")
                        }
                    } else {
                        if (HypixelUtils.getWinstreak()) {
                            EssentialAPI.getNotifications().push("Wyvtilities", "You currently have " + HypixelUtils.gexp + " guild EXP.")
                        } else {
                            EssentialAPI.getNotifications().push("Wyvtilities", "There was a problem trying to get your GEXP.")
                        }
                    }
                }
            } catch (e : NullPointerException) {
                e.printStackTrace()
            }

        }
    }

     */

}