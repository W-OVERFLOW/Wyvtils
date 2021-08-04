package net.wyvest.wyvtilities.mixin;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

/**
 * A tweaker to load both Essential and Requisite's tweaker.
 */
public class RequisiteEssentialTweaker implements ITweaker {


    public RequisiteEssentialTweaker() {
        try {
            List<String> tweakClasses = (List) Launch.blackboard.get("TweakClasses");
            if (!tweakClasses.contains("xyz.matthewtgm.requisite.launchwrapper.RequisiteLaunchwrapper")) {
                if (Launch.blackboard.get("requisite") == null || !(Boolean) Launch.blackboard.get("requisite")) {
                    System.out.println("Injecting RequisiteLaunchwrapper from RequisiteEssentialTweaker");
                    Launch.classLoader.addClassLoaderExclusion("xyz.matthewtgm.requisite.launchwrapper.RequisiteLaunchwrapper".substring(0, "xyz.matthewtgm.requisite.launchwrapper.RequisiteLaunchwrapper".lastIndexOf(46)));
                    List<ITweaker> tweaks = (List)Launch.blackboard.get("Tweaks");
                    tweaks.add((ITweaker)Class.forName("xyz.matthewtgm.requisite.launchwrapper.RequisiteLaunchwrapper", true, Launch.classLoader).newInstance());
                }
            }
            if (!tweakClasses.contains("gg.essential.loader.stage0.EssentialSetupTweaker")) {
                System.out.println("Injecting EssentialSetupTweaker from RequisiteEssentialTweaker");
                Launch.classLoader.addClassLoaderExclusion("gg.essential.loader.stage0.EssentialSetupTweaker".substring(0, "gg.essential.loader.stage0.EssentialSetupTweaker".lastIndexOf(46)));
                List<ITweaker> tweaks = (List)Launch.blackboard.get("Tweaks");
                tweaks.add((ITweaker)Class.forName("gg.essential.loader.stage0.EssentialSetupTweaker", true, Launch.classLoader).newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {

    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {

    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
