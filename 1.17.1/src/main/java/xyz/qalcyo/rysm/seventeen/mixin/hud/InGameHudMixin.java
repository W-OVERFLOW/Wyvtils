/*
 * Rysm, a utility mod for 1.8.9.
 * Copyright (C) 2021 Rysm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.qalcyo.rysm.seventeen.mixin.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qalcyo.rysm.core.RysmCore;
import xyz.qalcyo.rysm.core.config.RysmConfig;
import xyz.qalcyo.rysm.core.listener.events.TitleEvent;
import xyz.qalcyo.rysm.seventeen.gui.SidebarGui;

/**
 * This mixin sends and handles the TitleEvent which is used
 * in the core submodule to modify rendered titles and
 * subtitles ingame.
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    private TitleEvent titleEvent;
    @Shadow
    private int scaledHeight;

    private int sidebarWidth;
    private int bottom;
    private int right;

    @Shadow
    protected abstract void drawTextBackground(MatrixStack matrices, TextRenderer textRenderer, int yOffset, int width, int color);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"))
    private void removeTranslation(MatrixStack matrixStack, double x, double y, double z) {
        if (y == (double) (scaledHeight - 68)) {
            if ((!RysmConfig.INSTANCE.getActionBarPosition() && RysmConfig.INSTANCE.getActionBarCustomization()) || !RysmConfig.INSTANCE.getActionBarCustomization()) {
                matrixStack.translate(x, y, z);
            }
        } else {
            matrixStack.translate(x, y, z);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTextBackground(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;III)V"))
    private void modifyActionBarBackground(InGameHud inGameHud, MatrixStack matrices, TextRenderer textRenderer, int yOffset, int width, int color) {
        if (yOffset == -4) {
            if (RysmConfig.INSTANCE.getActionBarCustomization() && RysmConfig.INSTANCE.getActionBar()) {
                drawTextBackground(matrices, textRenderer, yOffset, width, color);
            }
        } else {
            drawTextBackground(matrices, textRenderer, yOffset, width, color);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int modifyActionBar(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (RysmConfig.INSTANCE.getActionBarCustomization()) {
            if (RysmConfig.INSTANCE.getActionBar()) {
                if (RysmConfig.INSTANCE.getActionBarShadow()) {
                    return textRenderer.drawWithShadow(matrices, text, (RysmConfig.INSTANCE.getActionBarPosition() ? RysmConfig.INSTANCE.getActionBarX() : x), (RysmConfig.INSTANCE.getActionBarPosition() ? RysmConfig.INSTANCE.getActionBarY() : y), color);
                } else {
                    return textRenderer.draw(matrices, text, (RysmConfig.INSTANCE.getActionBarPosition() ? RysmConfig.INSTANCE.getActionBarX() : x), (RysmConfig.INSTANCE.getActionBarPosition() ? RysmConfig.INSTANCE.getActionBarY() : y), color);
                }
            }
        } else {
            return textRenderer.draw(matrices, text, x, y, color);
        }
        return 0;
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void cancelScoreboard(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        if (!RysmConfig.INSTANCE.getSidebar()) {
            ci.cancel();
        } else if (MinecraftClient.getInstance().currentScreen instanceof SidebarGui) {
            ci.cancel();
        } else {
            matrices.push();
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
    private int getMax(int a, int b) {
        sidebarWidth = Math.max(a, b);
        return sidebarWidth;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At("STORE"), index = 12)
    private int modifyY(int m) {
        bottom = (RysmConfig.INSTANCE.getSidebarPosition() ? RysmConfig.INSTANCE.getSidebarY() : m);
        return bottom;
    }


    @ModifyVariable(method = "renderScoreboardSidebar", at = @At("STORE"), index = 14)
    private int modifyX(int m, MatrixStack matrices, ScoreboardObjective objective) {
        if (RysmConfig.INSTANCE.getSidebarPosition()) {
            return RysmConfig.INSTANCE.getSidebarX() - sidebarWidth - 3;
        } else {
            return m;
        }

    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At("STORE"), index = 25)
    private int modifyX2(int m) {
        right = (RysmConfig.INSTANCE.getSidebarPosition() ? RysmConfig.INSTANCE.getSidebarX() - 1 : m);
        return right;
    }

    @Inject(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;getSecond()Ljava/lang/Object;"))
    private void scale(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        float iHaveNoIdeaWhatToNameThisFloat = RysmConfig.INSTANCE.getSidebarScale() - 1.0f;
        matrices.translate(-right * iHaveNoIdeaWhatToNameThisFloat, -bottom * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
        matrices.scale(RysmConfig.INSTANCE.getSidebarScale(), RysmConfig.INSTANCE.getSidebarScale(), 1.0F);
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int addTextShadow(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (RysmConfig.INSTANCE.getSidebarTextShadow()) {
            return textRenderer.drawWithShadow(matrices, text, x, y, color);
        } else {
            return textRenderer.draw(matrices, text, x, y, color);
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"))
    private void removeBackground(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        if (RysmConfig.INSTANCE.getSidebarBackground()) {
            DrawableHelper.fill(matrices, x1, y1, right, y2, RysmConfig.INSTANCE.getSidebarBackgroundColor().getRGB());
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int modifyRedText(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        if (RysmConfig.INSTANCE.getSidebarScorePoints()) {
            if (RysmConfig.INSTANCE.getSidebarTextShadow()) {
                return textRenderer.drawWithShadow(matrices, text, x, y, color);
            } else {
                return textRenderer.draw(matrices, text, x, y, color);
            }
        }
        return 0;
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("TAIL"))
    private void pop(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        matrices.pop();
    }

    /**
     * Invokes the TitleEvent and modifies the title scale accordingly.
     */
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
    private void modifyTitleTranslate(Args args) {
        titleEvent = new TitleEvent(false, 1.0F, 1.0F, true);
        RysmCore.INSTANCE.getEventBus().post(titleEvent);
        if (((float) args.get(0)) == 4.0F) {
            args.set(0, 4.0F * titleEvent.getTitleScale());
            args.set(1, 4.0F * titleEvent.getTitleScale());
        } else if (((float) args.get(0)) == 2.0F) {
            args.set(0, 2.0F * titleEvent.getSubtitleScale());
            args.set(1, 2.0F * titleEvent.getSubtitleScale());
        }
    }

    /**
     * Cancels and sets the shadow of the title and subtitle based on the invoked event.
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int redirect(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color) {
        if (!titleEvent.getCancelled()) {
            if (titleEvent.getShadow()) {
                return instance.drawWithShadow(matrices, text, x, y, color);
            } else {
                return instance.draw(matrices, text, x, y, color);
            }
        } else {
            return -1;
        }
    }
}
