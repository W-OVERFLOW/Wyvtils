package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.wyvest.wyvtilities.bossbar.BossHealth;
import net.wyvest.wyvtilities.bossbar.BossHealthGui;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    @Inject(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableBlend()V"), cancellable = true)
    protected void renderBossHealth(CallbackInfo ci) {
        if (Minecraft.getMinecraft().currentScreen instanceof BossHealthGui) {
            return;
        }
        if (!WyvtilsConfig.bossBarCustomization) {
            return;
        }
        if (WyvtilsConfig.bossBar == true) BossHealth.INSTANCE.renderBossHealth();
    }

}
