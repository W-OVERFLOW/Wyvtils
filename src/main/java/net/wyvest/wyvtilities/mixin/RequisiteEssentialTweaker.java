package net.wyvest.wyvtilities.mixin;

import gg.essential.loader.stage0.EssentialSetupTweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import xyz.matthewtgm.requisite.launchwrapper.RequisiteLaunchwrapper;

public class RequisiteEssentialTweaker extends EssentialSetupTweaker {

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        RequisiteLaunchwrapper.inject(classLoader);
        super.injectIntoClassLoader(classLoader);
    }

}
