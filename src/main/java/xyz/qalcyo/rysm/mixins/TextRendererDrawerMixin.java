package xyz.qalcyo.rysm.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.Rysm;

@Mixin(targets = "net/minecraft/client/font/TextRenderer$Drawer")
public class TextRendererDrawerMixin {
    @Inject(method = "drawLayer(IF)F", at = @At("HEAD"), cancellable = true)
    private void cancel(int underlineColor, float x, CallbackInfoReturnable<Float> cir) {
        if (Rysm.INSTANCE.getNeedsToCancel()) {
            cir.cancel();
        }
    }

}
