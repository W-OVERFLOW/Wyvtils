package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.hooks.GuiIngameForgeHook")
public class MixinGuiIngameForgeHook {

    @SuppressWarnings({"UnresolvedMixinReference", "DefaultAnnotationParam"})
    @Inject(remap = false, method = "drawActionbarText", at = @At(remap = true, value = "HEAD"), cancellable = true)
    private static void onActionBarTextDrawn(String recordPlaying, int color, CallbackInfo ci) {
        int newX;
        int newY;
        if (WyvtilsConfig.INSTANCE.getActionBarPosition()) {
            newX = WyvtilsConfig.INSTANCE.getActionBarX();
            newY = WyvtilsConfig.INSTANCE.getActionBarY();
        } else {
            newX = -Minecraft.getMinecraft().fontRendererObj.getStringWidth(recordPlaying) >> 1;
            newY = -4;
        }
        Minecraft.getMinecraft().fontRendererObj.drawString(
                recordPlaying,
                newX, newY,
                color, WyvtilsConfig.INSTANCE.getActionBarShadow()
        );
        ci.cancel();
    }

}
