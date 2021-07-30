package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.wyvest.wyvtilities.actionbar.ActionBar;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.matthewtgm.tgmlib.tweaker.hooks.TGMLibGuiIngameAccessor;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    @Inject(method = "renderRecordOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"), cancellable = true)
    public void renderActionBar(int width, int height, float partialTicks, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getActionBarCustomization()) {
            ActionBar.INSTANCE.renderActionBar(width, height, partialTicks, ci);
        }
    }
}
