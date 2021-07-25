package net.wyvest.wyvtilities.mixin;

import net.minecraftforge.client.GuiIngameForge;
import net.wyvest.wyvtilities.actionbar.ActionBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    @Inject(method = "renderRecordOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"), cancellable = true)
    public void renderActionBar(int width, int height, float partialTicks, CallbackInfo ci) {
        ActionBar.INSTANCE.renderActionBar(width, height, partialTicks, ci);
    }
}
