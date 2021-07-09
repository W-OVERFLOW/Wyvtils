package net.wyvest.wyvtilities.commands;

import net.wyvest.wyvtilities.config.WyvtilsConfig;
import xyz.matthewtgm.tgmlib.commands.advanced.Command;
import xyz.matthewtgm.tgmlib.util.GuiHelper;

@Command(name = "wyvtilities", aliases = {"wyvtils", "wytils"})
public class WyvtilsCommands {
    @Command.Process
    public void process() {
        GuiHelper.open(WyvtilsConfig.INSTANCE.gui());
    }
}