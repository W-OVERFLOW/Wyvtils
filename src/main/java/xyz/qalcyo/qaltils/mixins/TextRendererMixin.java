package xyz.qalcyo.qaltils.mixins;

import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.qalcyo.qaltils.config.QaltilsConfig;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @ModifyVariable(method = "drawInternal(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", at = @At("HEAD"), index = 5)
    private boolean disableShadow(boolean textShadow) {
        return (!QaltilsConfig.INSTANCE.getDisableTextShadow() && textShadow);
    }

    @ModifyVariable(method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", at = @At("HEAD"), index = 5)
    private boolean disableShadow2(boolean textShadow) {
        return (!QaltilsConfig.INSTANCE.getDisableTextShadow() && textShadow);
    }

}

