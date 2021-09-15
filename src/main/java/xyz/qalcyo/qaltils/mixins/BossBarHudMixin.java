package xyz.qalcyo.qaltils.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import xyz.qalcyo.qaltils.config.QaltilsConfig;
import xyz.qalcyo.qaltils.gui.BossBarGui;
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
        } else if (!QaltilsConfig.INSTANCE.getBossBar()) {
            ci.cancel();
        } else {
            if (QaltilsConfig.INSTANCE.getFirstLaunchBossbar()) {
                QaltilsConfig.INSTANCE.setFirstLaunchBossbar(false);
                QaltilsConfig.INSTANCE.setBossBarX(MinecraftClient.getInstance().getWindow().getScaledWidth());
                QaltilsConfig.INSTANCE.markDirty();
                QaltilsConfig.INSTANCE.writeData();
            }
            if (!bossBars.isEmpty()) {
                matrices.push();
                double iHaveNoIdeaWhatToNameThisFloat = (double) QaltilsConfig.INSTANCE.getBossbarScale() - 1.0f;
                matrices.translate(-QaltilsConfig.INSTANCE.getBossBarX() * iHaveNoIdeaWhatToNameThisFloat / 2, -QaltilsConfig.INSTANCE.getBossBarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
                matrices.scale(QaltilsConfig.INSTANCE.getBossbarScale(), QaltilsConfig.INSTANCE.getBossbarScale(), 1F);
            }
        }
    }

    @ModifyVariable(method = "render", at = @At("STORE"), index = 2)
    private int getX(int x) {
        return QaltilsConfig.INSTANCE.getBossBarX();
    }

    @ModifyVariable(method = "render", at = @At("STORE"), index = 3)
    private int getY(int y) {
        return QaltilsConfig.INSTANCE.getBossBarY();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int modifyText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (QaltilsConfig.INSTANCE.getBossBarText()) {
            if (QaltilsConfig.INSTANCE.getBossBarShadow()) {
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
        if (!QaltilsConfig.INSTANCE.getBossBarBar()) {
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
