package net.wyvest.wyvtilities.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import net.wyvest.wyvtilities.gui.WyvtilsGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancel(MatrixStack matrices, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof WyvtilsGui) {
            ci.cancel();
        }
        if (!WyvtilsConfig.INSTANCE.getBossBar()) {
            ci.cancel();
        } else {
            if (WyvtilsConfig.INSTANCE.getFirstLaunchBossbar()) {
                WyvtilsConfig.INSTANCE.setFirstLaunchBossbar(false);
                WyvtilsConfig.INSTANCE.setBossBarX(MinecraftClient.getInstance().getWindow().getScaledWidth());
                WyvtilsConfig.INSTANCE.markDirty();
                WyvtilsConfig.INSTANCE.writeData();
            }
            matrices.push();
            double iHaveNoIdeaWhatToNameThisFloat = (double) WyvtilsConfig.INSTANCE.getBossbarScale() - 1.0f;
            matrices.translate(-WyvtilsConfig.INSTANCE.getBossBarX() * iHaveNoIdeaWhatToNameThisFloat / 2, -WyvtilsConfig.INSTANCE.getBossBarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
            matrices.scale(WyvtilsConfig.INSTANCE.getBossbarScale(), WyvtilsConfig.INSTANCE.getBossbarScale(), 1F);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void pop(MatrixStack matrices, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getBossBar()) matrices.pop();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int modifyText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (WyvtilsConfig.INSTANCE.getBossBarText()) {
            if (WyvtilsConfig.INSTANCE.getBossBarShadow()) {
                return textRenderer.drawWithShadow(matrices, text, x, y, color);
            } else {
                return textRenderer.draw(matrices, text, x, y, color);
            }
        } else {
            return 0;
        }
    }

    @Inject(method = "renderBossBar", at = @At("HEAD"), cancellable = true)
    private void cancelBar(MatrixStack matrices, int x, int y, BossBar bossBar, CallbackInfo ci) {
        if (!WyvtilsConfig.INSTANCE.getBossBarBar()) {
            ci.cancel();
        }
    }

}
