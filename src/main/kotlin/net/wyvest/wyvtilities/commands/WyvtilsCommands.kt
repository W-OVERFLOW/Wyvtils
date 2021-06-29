package net.wyvest.wyvtilities.commands

import gg.essential.universal.ChatColor
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.APIUtil
import net.wyvest.wyvtilities.utils.GexpUtils
import net.wyvest.wyvtilities.utils.Notifications
import java.util.*


class WyvtilsCommands : CommandBase() {
    override fun getCommandName(): String {
        return "wyvtilities"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/wyvtilities - opens Wyvtilities settings"
    }

    override fun getCommandAliases(): MutableList<String> {
        return mutableListOf("wyvtils")
    }

    override fun getRequiredPermissionLevel() = 0

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        if (args != null) {
            if (args.isEmpty()) {
                Wyvtilities.displayScreen = WyvtilsConfig.gui()
                return
            }
        }
        val mc = Minecraft.getMinecraft()
        when (args!![0].lowercase(Locale.getDefault())) {
            "help" -> Wyvtilities.sendMessage(
                """
            ${EnumChatFormatting.GREEN}Command Help
            /wyvtilities - Open Config Menu
            /wyvtilities help - Shows help for command usage
            /wyvtilities config - Open Config Menu
            /wyvtilities aliases - Shows aliases for this command.
            """.trimIndent()
            )
            "config" -> Wyvtilities.displayScreen = WyvtilsConfig.gui()
            "setkey" -> Wyvtilities.threadPool.submit {
                try {
                    if (args.size == 1 && APIUtil.getJSONResponse("https://api.hypixel.net/key?key=" + args[1])
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
            "gexp" -> {
                if (WyvtilsConfig.apiKey == "") {
                    Wyvtilities.sendMessage(ChatColor.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
                } else {
                    if (args.size <= 1) {
                        Wyvtilities.threadPool.submit{
                            GexpUtils.getGEXP()
                            Notifications.push("Wyvtilities", "You currently have " + GexpUtils.gexp + " guild EXP.")
                        }
                    } else {
                        Wyvtilities.threadPool.submit{
                            GexpUtils.getGEXP(args[1])
                            Notifications.push("Wyvtilities", args[1] + " currently have " + GexpUtils.gexp + " guild EXP.")
                        }
                    }
                }
            }
            "aliases" -> Wyvtilities.sendMessage(commandAliases.toString())
            else -> mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(EnumChatFormatting.DARK_PURPLE.toString() + "[Wyvtilities] " + EnumChatFormatting.LIGHT_PURPLE + "Unknown argument. Type /wyvtilities help for correct usage."))
        }
    }

}