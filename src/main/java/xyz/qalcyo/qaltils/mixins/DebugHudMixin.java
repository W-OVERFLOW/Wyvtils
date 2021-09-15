package xyz.qalcyo.qaltils.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.util.math.MatrixStack;
import xyz.qalcyo.qaltils.config.QaltilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @ModifyArg(method = "renderLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int color1(int color) {
        return QaltilsConfig.INSTANCE.getDebugColor().getRGB();
    }

    @ModifyArg(method = "renderRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int color2(int color) {
        return QaltilsConfig.INSTANCE.getDebugColor().getRGB();
    }

    @ModifyArg(method = "drawMetricsData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int color3(int color) {
        return QaltilsConfig.INSTANCE.getDebugColor().getRGB();
    }

    @Redirect(method = "renderLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int overwriteShadow1(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return (QaltilsConfig.INSTANCE.getDebugShadow() ? textRenderer.drawWithShadow(matrices, text, x, y, color) : textRenderer.draw(matrices, text, x, y, color));
    }

    @Redirect(method = "renderRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int overwriteShadow2(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return (QaltilsConfig.INSTANCE.getDebugShadow() ? textRenderer.drawWithShadow(matrices, text, x, y, color) : textRenderer.draw(matrices, text, x, y, color));
    }

    @Redirect(method = "drawMetricsData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int overwriteShadow3(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return (QaltilsConfig.INSTANCE.getDebugShadow() ? textRenderer.drawWithShadow(matrices, text, x, y, color) : textRenderer.draw(matrices, text, x, y, color));
    }

    @Redirect(method = "drawMetricsData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int overwriteShadow4(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return (QaltilsConfig.INSTANCE.getDebugShadow() ? textRenderer.drawWithShadow(matrices, text, x, y, color) : textRenderer.draw(matrices, text, x, y, color));
    }

}
