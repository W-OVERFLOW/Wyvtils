package xyz.qalcyo.wyvtils.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.wyvtils.config.WyvtilsConfig;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "hasLabel", at = @At(value = "TAIL", shift = At.Shift.BEFORE), cancellable = true)
    private void renderPlayerNametag(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(
                MinecraftClient.isHudEnabled() && (WyvtilsConfig.INSTANCE.getRenderOwnNametag() || livingEntity != MinecraftClient.getInstance().getCameraEntity()) && !livingEntity.isInvisibleTo(MinecraftClient.getInstance().player) && !livingEntity.hasPassengers()
        );
    }

}
