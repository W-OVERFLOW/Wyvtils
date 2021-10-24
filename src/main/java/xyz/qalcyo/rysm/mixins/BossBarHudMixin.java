package xyz.qalcyo.rysm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import xyz.qalcyo.rysm.config.RysmConfig;
import xyz.qalcyo.rysm.gui.BossBarGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    @Shadow @Final
    Map<UUID, ClientBossBar> bossBars;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancel(MatrixStack matrices, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof BossBarGui) {
            ci.cancel();
        } else if (!RysmConfig.INSTANCE.getBossBar()) {
            ci.cancel();
        } else {
            if (RysmConfig.INSTANCE.getFirstLaunchBossbar()) {
                RysmConfig.INSTANCE.setFirstLaunchBossbar(false);
                RysmConfig.INSTANCE.setBossBarX(MinecraftClient.getInstance().getWindow().getScaledWidth());
                RysmConfig.INSTANCE.markDirty();
                RysmConfig.INSTANCE.writeData();
            }
            if (!bossBars.isEmpty()) {
                matrices.push();
                double iHaveNoIdeaWhatToNameThisFloat = (double) RysmConfig.INSTANCE.getBossbarScale() - 1.0f;
                matrices.translate(-RysmConfig.INSTANCE.getBossBarX() * iHaveNoIdeaWhatToNameThisFloat / 2, -RysmConfig.INSTANCE.getBossBarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
                matrices.scale(RysmConfig.INSTANCE.getBossbarScale(), RysmConfig.INSTANCE.getBossbarScale(), 1F);
            }
        }
    }

    @ModifyVariable(method = "render", at = @At("STORE"), index = 2)
    private int getX(int x) {
        return RysmConfig.INSTANCE.getBossBarX();
    }

    @ModifyVariable(method = "render", at = @At("STORE"), index = 3)
    private int getY(int y) {
        return RysmConfig.INSTANCE.getBossBarY();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int modifyText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (RysmConfig.INSTANCE.getBossBarText()) {
            if (RysmConfig.INSTANCE.getBossBarShadow()) {
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
        if (!RysmConfig.INSTANCE.getBossBarBar()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void pop(MatrixStack matrices, CallbackInfo ci) {
        if (!bossBars.isEmpty()) {
            matrices.pop();
        }
    }

}
