package net.wyvest.wyvtilities.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "club.sk1er.hytilities.config.HytilitiesConfig")
public interface AccessorHytilitiesConfig {
    @Accessor
    boolean getShortChannelNames();
}
