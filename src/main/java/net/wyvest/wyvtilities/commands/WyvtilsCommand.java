package net.wyvest.wyvtilities.commands;

import net.minecraft.util.EnumChatFormatting;
import net.wyvest.wyvtilities.Wyvtilities;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import net.wyvest.wyvtilities.utils.HypixelUtils;
import xyz.matthewtgm.json.util.JsonApiHelper;
import xyz.matthewtgm.requisite.commands.advanced.Command;
import xyz.matthewtgm.requisite.util.GuiHelper;
import xyz.matthewtgm.requisite.util.Multithreading;
import xyz.matthewtgm.requisite.util.Notifications;

@Command(name = "wyvtilities", aliases = {"wyvtils", "wytils", "wytil", "wyvtil"}, autoGenTabCompleteOptions = true)
public class WyvtilsCommand {
    private static WyvtilsCommand instance;

    public static WyvtilsCommand getInstance() {
        if (instance == null) {
            instance = new WyvtilsCommand();
        }
        return instance;
    }

    @Command.Process
    public void handle(){
        GuiHelper.open(WyvtilsConfig.INSTANCE.gui());
    }

    @Command.Argument(name = "config")
    public void config(){
        GuiHelper.open(WyvtilsConfig.INSTANCE.gui());
    }

    @Command.Argument(name = "setkey")
    public void setKey(String[] args) {
        Multithreading.runAsync(() -> {
            try {
                if (JsonApiHelper.getJsonObject("https://api.hypixel.net/key?key=" + args[1])
                        .getAsBoolean("success")
                ) {
                    WyvtilsConfig.INSTANCE.setApiKey(args[1]);
                    WyvtilsConfig.INSTANCE.markDirty();
                    WyvtilsConfig.INSTANCE.writeData();
                    Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.GREEN + "Saved API key successfully!");
                } else {
                    Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "Invalid API key! Please try again.");
                }
            } catch (Exception e) {
                Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "Invalid API key! Please try again.");
                e.printStackTrace();
            }
        });
    }

    @Command.Argument(name = "gexp")
    public void gexp(String[] args) {
        if (WyvtilsConfig.INSTANCE.getApiKey().isEmpty()) {
            Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "You need to provide a valid API key to run this command! Type /api new to autoset a key.");
        } else {
            try {
                Multithreading.runAsync(() -> {
                    {
                        if (args.length >= 2) {
                            if (HypixelUtils.INSTANCE.getGEXP(args[1])) {
                                Notifications
                                        .push("Wyvtilities", args[1] + " currently has " + HypixelUtils.INSTANCE.getGexpString() + " guild EXP.");
                            } else {
                                Notifications
                                        .push("Wyvtilities", "There was a problem trying to get " + args[1] + "'s GEXP.");
                            }
                        } else {
                            if (HypixelUtils.INSTANCE.getGEXP()) {
                                Notifications.push("Wyvtilities", "You currently have " + HypixelUtils.INSTANCE.getGexpString() + " guild EXP.");
                            } else {
                                Notifications.push("Wyvtilities", "There was a problem trying to get your GEXP.");
                            }
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    @Command.Argument(name = "winstreak")
    public void winstreak(String[] args) {
        if (WyvtilsConfig.INSTANCE.getApiKey().isEmpty()) {
            Wyvtilities.INSTANCE.sendMessage(EnumChatFormatting.RED + "You need to provide a valid API key to run this command! Type /api new to autoset a key.");
        } else {
            try {
                Multithreading.runAsync(() -> {
                    {
                        if (args.length >= 3) {
                            if (HypixelUtils.INSTANCE.getWinstreak(args[1], args[2])) {
                                Notifications
                                        .push("Wyvtilities", args[1] + " currently has a " + HypixelUtils.INSTANCE.getWinstreakString() + " winstreak in " + args[2] + ".");
                            } else {
                                Notifications
                                        .push("Wyvtilities", "There was a problem trying to get " + args[1] + "'s winstreak.");
                            }
                        }
                        else if (args.length >= 2) {
                            if (HypixelUtils.INSTANCE.getWinstreak(args[1])) {
                                Notifications
                                        .push("Wyvtilities", args[1] + " currently has a " + HypixelUtils.INSTANCE.getWinstreakString() + " winstreak.");
                            } else {
                                Notifications
                                        .push("Wyvtilities", "There was a problem trying to get " + args[1] + "'s winstreak.");
                            }
                        } else {
                            if (HypixelUtils.INSTANCE.getWinstreak()) {
                                Notifications.push("Wyvtilities", "You currently have a " + HypixelUtils.INSTANCE.getWinstreakString() + " winstreak.");
                            } else {
                                Notifications.push("Wyvtilities", "There was a problem trying to get your winstreak.");
                            }
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

}
