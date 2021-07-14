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
        if (WyvtilsConfig.soundBoost) {
            if (sound instanceof PositionedSound) {
                if (Wyvtilities.INSTANCE.checkSound(sound.getSoundLocation().getResourcePath())) {
                    cir.setReturnValue(1.0F * WyvtilsConfig.soundMultiplier);
                }
            }
        }
    }
}
