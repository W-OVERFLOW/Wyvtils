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

package xyz.qalcyo.wyvtils.seventeen.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qalcyo.wyvtils.core.WyvtilsCore;
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig;
import xyz.qalcyo.wyvtils.core.listener.events.HitboxRenderEvent;
import xyz.qalcyo.wyvtils.core.listener.events.entity.FireballEntity;
import xyz.qalcyo.wyvtils.core.listener.events.entity.ProjectileEntity;
import xyz.qalcyo.wyvtils.core.listener.events.entity.*;

import java.awt.*;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    private static HitboxRenderEvent hitboxRenderEvent;
    @Shadow
    private boolean renderHitboxes;
    @Shadow
    public abstract void setRenderHitboxes(boolean value);

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void invokeHitboxEvent(MatrixStack matrices, VertexConsumer vertices, Entity entityIn, float tickDelta, CallbackInfo ci) {
        if (entityIn instanceof net.minecraft.entity.player.PlayerEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new PlayerEntity(entityIn instanceof ClientPlayerEntity && ((net.minecraft.entity.player.PlayerEntity) entityIn).getGameProfile().getId() == (MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getGameProfile().getId() : "null")), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof MobEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new LivingEntity(entityIn instanceof Monster), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof ArmorStandEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new ArmorstandEntity(), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof net.minecraft.entity.projectile.FireballEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new FireballEntity(), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof net.minecraft.entity.vehicle.MinecartEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new MinecartEntity(), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof net.minecraft.entity.ItemEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new ItemEntity(), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof FireworkRocketEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new FireworkEntity(), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof ExperienceOrbEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new XPEntity(), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof TridentEntity || entityIn instanceof ShulkerBulletEntity || entityIn instanceof FishingBobberEntity || entityIn instanceof LlamaSpitEntity || entityIn instanceof ArrowEntity || entityIn instanceof ThrownEntity || entityIn instanceof SpectralArrowEntity) {
            hitboxRenderEvent = new HitboxRenderEvent(new ProjectileEntity(), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else {
            hitboxRenderEvent = new HitboxRenderEvent(new xyz.qalcyo.wyvtils.core.listener.events.entity.Entity(), (!(MinecraftClient.getInstance().targetedEntity == null) && MinecraftClient.getInstance().targetedEntity == entityIn ? 2 : Integer.MAX_VALUE), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        }
        WyvtilsCore.INSTANCE.getEventBus().post(hitboxRenderEvent);
        if (hitboxRenderEvent.getCancelled()) ci.cancel();
    }

    @Redirect(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/math/Box;FFFF)V"))
    private static void modifyBox(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, float red, float green, float blue, float alpha) {
        if (!hitboxRenderEvent.getCancelBox()) {
            WorldRenderer.drawBox(matrices, vertexConsumer, box, (float) hitboxRenderEvent.getBoxColor().getRed() / 255, (float) hitboxRenderEvent.getBoxColor().getGreen() / 255, (float) hitboxRenderEvent.getBoxColor().getBlue() / 255, (float) hitboxRenderEvent.getBoxColor().getAlpha() / 255);
        }
    }

    @Redirect(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDDDDFFFF)V"))
    private static void modifyLineOfSight(MatrixStack matrices, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        if (!hitboxRenderEvent.getCancelLineOfSight()) {
            WorldRenderer.drawBox(matrices, vertexConsumer, x1, y1, z1, x2, y2, z2, (float) hitboxRenderEvent.getLineOfSightColor().getRed() / 255, (float) hitboxRenderEvent.getLineOfSightColor().getGreen() / 255, (float) hitboxRenderEvent.getLineOfSightColor().getBlue() / 255, (float) hitboxRenderEvent.getLineOfSightColor().getAlpha() / 255);
        }
    }

    @Inject(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;vertex(Lnet/minecraft/util/math/Matrix4f;FFF)Lnet/minecraft/client/render/VertexConsumer;"), cancellable = true)
    private static void cancelEyeline(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        if (hitboxRenderEvent.getCancelEyeLine()) ci.cancel();
    }

    @ModifyArgs(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(IIII)Lnet/minecraft/client/render/VertexConsumer;"))
    private static void modifyEyeLine(Args args) {
        args.set(0, hitboxRenderEvent.getEyeLineColor().getRed());
        args.set(1, hitboxRenderEvent.getEyeLineColor().getGreen());
        args.set(2, hitboxRenderEvent.getEyeLineColor().getBlue());
        args.set(3, hitboxRenderEvent.getEyeLineColor().getAlpha());
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void forceHitboxes(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!renderHitboxes && WyvtilsConfig.INSTANCE.getForceHitbox()) {
            setRenderHitboxes(true);
        }
    }
}
