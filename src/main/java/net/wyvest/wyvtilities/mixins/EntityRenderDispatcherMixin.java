package net.wyvest.wyvtilities.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Shadow
    private boolean renderHitboxes;

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void cancelHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity) && !WyvtilsConfig.INSTANCE.getNonplayerHitbox()) ci.cancel();
        if (!(entity instanceof ClientPlayerEntity)) ci.cancel();
    }

    @Shadow
    public abstract void setRenderHitboxes(boolean value);

    @Inject(method = "render", at = @At("HEAD"))
    private void forceHitboxes(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!renderHitboxes && WyvtilsConfig.INSTANCE.getForceHitbox()) {
            setRenderHitboxes(true);
        }
    }
}
