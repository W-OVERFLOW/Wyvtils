package net.wyvest.wyvtilities.mixin;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinTweaker;
import xyz.matthewtgm.requisite.launchwrapper.RequisiteLaunchwrapper;

import java.io.File;
import java.util.List;

public class RequisiteEssentialTweaker extends RequisiteLaunchwrapper {
    private final MixinTweaker mixinTweaker;
    public RequisiteEssentialTweaker() {
        mixinTweaker = new MixinTweaker();
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        super.acceptOptions(args, gameDir, assetsDir, profile);
        mixinTweaker.acceptOptions(args, gameDir, assetsDir, profile);
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        super.injectIntoClassLoader(classLoader);
        mixinTweaker.injectIntoClassLoader(classLoader);
    }

    @Override
    public String getLaunchTarget() {
        return mixinTweaker.getLaunchTarget();
    }
}
