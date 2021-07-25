package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.wyvest.wyvtilities.bossbar.BossHealth;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import net.wyvest.wyvtilities.bossbar.BossHealthGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    protected void renderBossHealth(CallbackInfo ci) {
        if (Minecraft.getMinecraft().currentScreen instanceof BossHealthGui) {
            ci.cancel();
            return;
        }
        if (!WyvtilsConfig.INSTANCE.getBossBarCustomization()) {
            return;
        }
        if (WyvtilsConfig.INSTANCE.getBossBar()) BossHealth.INSTANCE.renderBossHealth();
        ci.cancel();
    }

}
