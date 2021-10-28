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

package xyz.qalcyo.rysm.seventeen.mixin.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.core.RysmCore;
import xyz.qalcyo.rysm.core.config.RysmConfig;
import xyz.qalcyo.rysm.core.listener.events.StringRenderEvent;

/**
 * This mixin sends and handles the StringRenderEvent which is used
 * in the core submodule to modify rendered text ingame.
 */
@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @ModifyVariable(method = "drawInternal(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", at = @At("HEAD"), index = 5)
    private boolean disableShadow(boolean textShadow) {
        return (!RysmConfig.INSTANCE.getDisableTextShadow() && textShadow);
    }

    @ModifyVariable(method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", at = @At("HEAD"), index = 5)
    private boolean disableShadow2(boolean textShadow) {
        return (!RysmConfig.INSTANCE.getDisableTextShadow() && textShadow);
    }

    private StringRenderEvent drawStringEvent;
    @Inject(method = "drawInternal(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", at = @At("HEAD"))
    private void onStringRendered(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, boolean mirror, CallbackInfoReturnable<Integer> cir) {
        drawStringEvent = new StringRenderEvent(text == null ? "" : text, MinecraftClient.getInstance().player == null ? null : MinecraftClient.getInstance().player.getName().asString());
        RysmCore.INSTANCE.getEventBus().post(drawStringEvent);
    }

    @Inject(method = "draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", at = @At("HEAD"))
    private void onString(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, CallbackInfoReturnable<Integer> cir) {
        drawStringEvent = new StringRenderEvent(text == null || text.asString() == null ? "" : text.asString(), MinecraftClient.getInstance().player == null ? null : MinecraftClient.getInstance().player.getName().asString());
        RysmCore.INSTANCE.getEventBus().post(drawStringEvent);
    }

    @ModifyVariable(method = "draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Text onStringRendered_modifyText(Text original) {
        return Text.of(drawStringEvent.getString());
    }

    @ModifyVariable(method = "draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Text thefunny(Text original) {
        return Text.of(drawStringEvent.getString());
    }

    @ModifyVariable(method = "drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Text thefunny2(Text original) {
        return Text.of(drawStringEvent.getString());
    }

    @Inject(method = "drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", at = @At("HEAD"))
    private void on(MatrixStack matrices, Text text, float x, float y, int color, CallbackInfoReturnable<Integer> cir) {
        drawStringEvent = new StringRenderEvent(text == null || text.asString() == null ? "" : text.asString(), MinecraftClient.getInstance().player == null ? null : MinecraftClient.getInstance().player.getName().asString());
        RysmCore.INSTANCE.getEventBus().post(drawStringEvent);
    }

    @Inject(method = "draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", at = @At("HEAD"))
    private void ona(MatrixStack matrices, Text text, float x, float y, int color, CallbackInfoReturnable<Integer> cir) {
        drawStringEvent = new StringRenderEvent(text == null || text.asString() == null ? "" : text.asString(), MinecraftClient.getInstance().player == null ? null : MinecraftClient.getInstance().player.getName().asString());
        RysmCore.INSTANCE.getEventBus().post(drawStringEvent);
    }

    @ModifyVariable(method = "drawInternal(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String onStringRendered_modifyText(String original) {
        return drawStringEvent.getString();
    }

}
