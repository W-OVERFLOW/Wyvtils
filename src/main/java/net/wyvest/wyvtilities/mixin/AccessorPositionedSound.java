package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.audio.PositionedSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PositionedSound.class)
public interface AccessorPositionedSound {

    @Accessor
    float getVolume();

    @Accessor
    void setVolume(float newVolume);
}
