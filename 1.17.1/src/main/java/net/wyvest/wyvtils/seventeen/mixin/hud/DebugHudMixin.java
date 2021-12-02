/*
 * Wyvtils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Wyvtils
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

package net.wyvest.wyvtils.seventeen.mixin.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.util.math.MatrixStack;
import net.wyvest.wyvtils.core.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @ModifyArg(method = "renderLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int color1(int color) {
        return WyvtilsConfig.INSTANCE.getDebugColor().getRGB();
    }

    @ModifyArg(method = "renderRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int color2(int color) {
        return WyvtilsConfig.INSTANCE.getDebugColor().getRGB();
    }

    @ModifyArg(method = "drawMetricsData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int color3(int color) {
        return WyvtilsConfig.INSTANCE.getDebugColor().getRGB();
    }

    @Redirect(method = "renderLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int overwriteShadow1(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return (WyvtilsConfig.INSTANCE.getDebugShadow() ? textRenderer.drawWithShadow(matrices, text, x, y, color) : textRenderer.draw(matrices, text, x, y, color));
    }

    @Redirect(method = "renderRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int overwriteShadow2(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return (WyvtilsConfig.INSTANCE.getDebugShadow() ? textRenderer.drawWithShadow(matrices, text, x, y, color) : textRenderer.draw(matrices, text, x, y, color));
    }

    @Redirect(method = "drawMetricsData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int overwriteShadow3(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return (WyvtilsConfig.INSTANCE.getDebugShadow() ? textRenderer.drawWithShadow(matrices, text, x, y, color) : textRenderer.draw(matrices, text, x, y, color));
    }

    @Redirect(method = "drawMetricsData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int overwriteShadow4(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return (WyvtilsConfig.INSTANCE.getDebugShadow() ? textRenderer.drawWithShadow(matrices, text, x, y, color) : textRenderer.draw(matrices, text, x, y, color));
    }

}