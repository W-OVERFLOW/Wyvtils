package net.wyvest.wyvtilities.commands;

import net.minecraft.util.EnumChatFormatting;
import net.wyvest.wyvtilities.Wyvtilities;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import net.wyvest.wyvtilities.utils.GexpUtils;
import xyz.matthewtgm.json.util.JsonApiHelper;
import xyz.matthewtgm.tgmlib.commands.advanced.Command;
import xyz.matthewtgm.tgmlib.util.GuiHelper;
import xyz.matthewtgm.tgmlib.util.Multithreading;
import xyz.matthewtgm.tgmlib.util.Notifications;

@SuppressWarnings("unused")
@Command(name = "wyvtilities", aliases = {"wyvtils", "wytils"}, tabCompleteOptions = {"help", "config", "setkey", "gexp"})
public class WyvtilsCommands {
    @Command.Process
    public void process(String[] args) {
        if (args.length <= 0) {
            GuiHelper.open(WyvtilsConfig.INSTANCE.gui());
        } else {
            Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "Unknown argument. Type /wyvtilities help for correct usage.");
        }
    }

    @Command.Argument(name = "help")
    public void help() {
        Wyvtilities.INSTANCE.sendHelpMessage();
    }

    @Command.Argument(name = "config")
    public void config() {
        GuiHelper.open(WyvtilsConfig.INSTANCE.gui());
    }

    @Command.Argument(name = "setkey")
    public void setKey(String[] args) {
        try {
            if (args.length == 1 && JsonApiHelper.getJsonObject("https://api.hypixel.net/key?key=" + args[1])
                    .get("success").getAsBoolean()
            ) {
                WyvtilsConfig.apiKey = args[1];
                WyvtilsConfig.INSTANCE.markDirty();
                WyvtilsConfig.INSTANCE.writeData();
                Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.GREEN.toString() + "Saved API key successfully!");
            } else {
                Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "Invalid API key! Please try again.");
            }
        } catch (Exception ex) {
            Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "Invalid API key! Please try again.");
            ex.printStackTrace();
        }
    }

    @Command.Argument(name = "gexp")
    public void getGexp(String[] args) {
        if (WyvtilsConfig.apiKey.isEmpty()) {
            Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "You need to provide a valid API key to run this command! Type /api new to autoset a key.");
        } else {
            if (args.length <= 1) {
                Multithreading.runAsync(() -> {
                    GexpUtils.INSTANCE.getGEXP();
                    Notifications.push("Wyvtilities", "You currently have " + GexpUtils.gexp + " guild EXP.");
                });
            } else {
                Multithreading.runAsync(() -> {
                    GexpUtils.INSTANCE.getGEXP(args[1]);
                    Notifications.push("Wyvtilities", args[1] + " currently have " + GexpUtils.gexp + " guild EXP.");
                });
            }
        }
    }

}