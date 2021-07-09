package net.wyvest.wyvtilities.mixin;

import net.minecraftforge.client.GuiIngameForge;
import net.wyvest.wyvtilities.bossbar.BossHealth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    //if making a fork, do not touch this right now as i am currently working on this feature!
    @Inject(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableBlend()V"))
    protected void renderBossHealth(CallbackInfo ci) {
        BossHealth.INSTANCE.renderBossHealth();
    }

}
