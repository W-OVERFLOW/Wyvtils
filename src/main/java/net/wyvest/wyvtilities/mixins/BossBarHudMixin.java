package net.wyvest.wyvtilities.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import net.wyvest.wyvtilities.gui.BossBarGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    private int yAdd = 0;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancel(MatrixStack matrices, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof BossBarGui) {
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
        yAdd = 0;
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;renderBossBar(Lnet/minecraft/client/util/math/MatrixStack;IILnet/minecraft/entity/boss/BossBar;)V"))
    private void modifyBar(Args args) {
        args.set(1, WyvtilsConfig.INSTANCE.getBossBarX() - 91);
        args.set(2, WyvtilsConfig.INSTANCE.getBossBarY() + yAdd);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int modifyText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (WyvtilsConfig.INSTANCE.getBossBarText()) {
            if (WyvtilsConfig.INSTANCE.getBossBarShadow()) {
                return textRenderer.drawWithShadow(matrices, text, WyvtilsConfig.INSTANCE.getBossBarX() - ((float) textRenderer.getWidth(text) / 2), ((float) WyvtilsConfig.INSTANCE.getBossBarY()) - 10 + yAdd, color);
            } else {
                return textRenderer.draw(matrices, text, x, y, color);
            }
        } else {
            return 0;
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/Objects;requireNonNull(Ljava/lang/Object;)Ljava/lang/Object;"))
    private void addY(MatrixStack matrices, CallbackInfo ci) {
        yAdd += 19;
    }

    @Inject(method = "renderBossBar", at = @At("HEAD"), cancellable = true)
    private void cancelBar(MatrixStack matrices, int x, int y, BossBar bossBar, CallbackInfo ci) {
        if (!WyvtilsConfig.INSTANCE.getBossBarBar()) {
            ci.cancel();
        }
    }

}
