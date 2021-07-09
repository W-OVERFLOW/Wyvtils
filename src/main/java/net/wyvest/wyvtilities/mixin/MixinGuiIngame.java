package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.wyvest.wyvtilities.bossbar.BossHealth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    //if making a fork, do not touch this right now as i am currently working on this feature!
    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
        protected void renderBossHealth(CallbackInfo ci){
        BossHealth.INSTANCE.renderBossHealth();
        ci.cancel();
    }
}
