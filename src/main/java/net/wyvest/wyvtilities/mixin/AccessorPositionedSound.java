package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.audio.PositionedSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PositionedSound.class)
public interface AccessorPositionedSound {
    @Accessor
    void setVolume(float yeah);
}
