package net.wyvest.wyvtilities.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    private int scaledHeight;

    @Shadow
    protected abstract void drawTextBackground(MatrixStack matrices, TextRenderer textRenderer, int yOffset, int width, int color);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"))
    private void removeTranslation(MatrixStack matrixStack, double x, double y, double z) {
        if (y == (double) (scaledHeight - 68)) {
            if ((!WyvtilsConfig.INSTANCE.getActionBarPosition() && WyvtilsConfig.INSTANCE.getActionBarCustomization()) || !WyvtilsConfig.INSTANCE.getActionBarCustomization()) {
                matrixStack.translate(x, y, z);
            }
        } else {
            matrixStack.translate(x, y, z);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTextBackground(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;III)V"))
    private void modifyActionBarBackground(InGameHud inGameHud, MatrixStack matrices, TextRenderer textRenderer, int yOffset, int width, int color) {
        if (yOffset == -4) {
            if (WyvtilsConfig.INSTANCE.getActionBarCustomization() && WyvtilsConfig.INSTANCE.getActionBar()) {
                drawTextBackground(matrices, textRenderer, yOffset, width, color);
            }
        } else {
            drawTextBackground(matrices, textRenderer, yOffset, width, color);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int modifyActionBar(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (WyvtilsConfig.INSTANCE.getActionBarCustomization()) {
            if (WyvtilsConfig.INSTANCE.getActionBar()) {
                if (WyvtilsConfig.INSTANCE.getActionBarShadow()) {
                    return textRenderer.drawWithShadow(matrices, text, (WyvtilsConfig.INSTANCE.getActionBarPosition() ? WyvtilsConfig.INSTANCE.getActionBarX() : x), (WyvtilsConfig.INSTANCE.getActionBarPosition() ? WyvtilsConfig.INSTANCE.getActionBarY() : y), color);
                } else {
                    return textRenderer.draw(matrices, text, (WyvtilsConfig.INSTANCE.getActionBarPosition() ? WyvtilsConfig.INSTANCE.getActionBarX() : x), (WyvtilsConfig.INSTANCE.getActionBarPosition() ? WyvtilsConfig.INSTANCE.getActionBarY() : y), color);
                }
            }
        } else {
            return textRenderer.draw(matrices, text, x, y, color);
        }
        return 0;
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
    private void modifyTitleTranslate(Args args) {
        if (((float) args.get(0)) == 4.0F) {
            args.set(0, 4.0F * WyvtilsConfig.INSTANCE.getTitleScale());
            args.set(1, 4.0F * WyvtilsConfig.INSTANCE.getTitleScale());
        } else if (((float) args.get(0)) == 2.0F) {
            args.set(0, 2.0F * WyvtilsConfig.INSTANCE.getSubtitleScale());
            args.set(1, 2.0F * WyvtilsConfig.INSTANCE.getSubtitleScale());
        }
    }

}
