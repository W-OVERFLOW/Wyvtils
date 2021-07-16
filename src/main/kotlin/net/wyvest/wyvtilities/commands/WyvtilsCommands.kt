package net.wyvest.wyvtilities.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import gg.essential.api.utils.Multithreading
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.GexpUtils
import xyz.matthewtgm.json.util.JsonApiHelper


object WyvtilsCommands : Command("wyvtilities", true) {

    override val commandAliases: Set<Alias>
        get() = setOf(Alias("wyvtils"), Alias("wytils"))

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for Wyvtils")
    fun config() {
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

    @SubCommand("setkey", description = "Sets the API key for Wyvtils.")
    fun setKey(args: Array<String>) {
        Multithreading.runAsync {
            try {
                if (args.size == 1 && JsonApiHelper.getJsonObject("https://api.hypixel.net/key?key=" + args[1])
                        .get("success").asBoolean
                ) {
                    WyvtilsConfig.apiKey = args[1]
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
    fun gexp(args: Array<String>) {
        if (WyvtilsConfig.apiKey == "") {
            Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
        } else {
            if (args.size <= 1) {
                Multithreading.runAsync {
                    GexpUtils.getGEXP()
                    EssentialAPI.getNotifications()
                        .push("Wyvtilities", "You currently have " + GexpUtils.gexp + " guild EXP.")
                }
            } else {
                Multithreading.runAsync {
                    GexpUtils.getGEXP(args[1])
                    EssentialAPI.getNotifications()
                        .push("Wyvtilities", args[1] + " currently have " + GexpUtils.gexp + " guild EXP.")
                }
            }
        }
    }

}