package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.audio.*;
import net.wyvest.wyvtilities.Wyvtilities;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class MixinSoundManager {
    @Inject(method = "getNormalizedVolume", at = @At("HEAD"), cancellable = true)
    public void getVolume(ISound sound, SoundPoolEntry entry, SoundCategory category, CallbackInfoReturnable<Float> cir) {
        if (WyvtilsConfig.INSTANCE.getSoundBoost()) {
            if (sound instanceof PositionedSound) {
                PositionedSound positionedSound = (PositionedSound) sound;
                if (Wyvtilities.INSTANCE.checkSound(sound.getSoundLocation().getResourcePath())) {
                    if (positionedSound.getVolume() * WyvtilsConfig.INSTANCE.getSoundMultiplier() <= 1) {
                        cir.setReturnValue(positionedSound.getVolume() * WyvtilsConfig.INSTANCE.getSoundMultiplier());
                    } else {
                        cir.setReturnValue(Float.parseFloat("1"));
                    }
                }
            }
        }
    }
}
