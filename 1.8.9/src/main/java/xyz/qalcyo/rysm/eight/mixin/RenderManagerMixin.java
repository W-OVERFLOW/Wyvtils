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

package xyz.qalcyo.rysm.eight.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.core.RysmCore;
import xyz.qalcyo.rysm.core.config.RysmConfig;
import xyz.qalcyo.rysm.core.listener.events.HitboxRenderEvent;
import xyz.qalcyo.rysm.core.listener.events.entity.*;

import java.awt.*;

@Mixin(RenderManager.class)
public class RenderManagerMixin {
    private HitboxRenderEvent hitboxRenderEvent;

    @Inject(method = "doRenderEntity", at = @At(value = "HEAD"))
    private void forceHitbox(Entity p_doRenderEntity_1_, double p_doRenderEntity_2_, double p_doRenderEntity_3_, double p_doRenderEntity_4_, float p_doRenderEntity_5_, float p_doRenderEntity_6_, boolean p_doRenderEntity_8_, CallbackInfoReturnable<Boolean> cir) {
        if (RysmConfig.INSTANCE.getForceHitbox()) {
            if (!Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox())
                Minecraft.getMinecraft().getRenderManager().setDebugBoundingBox(true);
        }
    }


    @Inject(method = "renderDebugBoundingBox", at = @At(value = "HEAD"), cancellable = true)
    private void invokeHitboxEvent(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (entityIn instanceof EntityPlayer) {
            hitboxRenderEvent = new HitboxRenderEvent(new PlayerEntity(entityIn instanceof EntityPlayerSP && ((EntityPlayer) entityIn).getGameProfile().getId() == Minecraft.getMinecraft().thePlayer.getGameProfile().getId()), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof EntityLiving) {
            hitboxRenderEvent = new HitboxRenderEvent(new LivingEntity(entityIn instanceof IMob), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof EntityArmorStand) {
            hitboxRenderEvent = new HitboxRenderEvent(new ArmorstandEntity(), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof EntityFireball) {
            hitboxRenderEvent = new HitboxRenderEvent((entityIn instanceof EntityWitherSkull ? new WitherSkullEntity() : new FireballEntity()), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof EntityMinecart) {
            hitboxRenderEvent = new HitboxRenderEvent(new MinecartEntity(), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof EntityItem) {
            hitboxRenderEvent = new HitboxRenderEvent(new ItemEntity(), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof EntityFireworkRocket) {
            hitboxRenderEvent = new HitboxRenderEvent(new FireworkEntity(), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof EntityXPOrb) {
            hitboxRenderEvent = new HitboxRenderEvent(new XPEntity(), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else if (entityIn instanceof IProjectile) {
            hitboxRenderEvent = new HitboxRenderEvent(new ProjectileEntity(), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        } else {
            hitboxRenderEvent = new HitboxRenderEvent(new xyz.qalcyo.rysm.core.listener.events.entity.Entity(), getReachDistanceFromEntity(entityIn), Color.WHITE, Color.WHITE, Color.WHITE, false, false, false, false);
        }
        RysmCore.INSTANCE.getEventBus().post(hitboxRenderEvent);
        if (hitboxRenderEvent.getCancelled()) ci.cancel();
    }

    @Redirect(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;IIII)V"))
    private void cancelLineOfSightAndBox(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha, Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
        if (green == 255) {
            if (!hitboxRenderEvent.getCancelBox()) {
                RenderGlobal.drawOutlinedBoundingBox((RysmConfig.INSTANCE.getAccurateHitbox() ? boundingBox.expand(entityIn.getCollisionBorderSize(), entityIn.getCollisionBorderSize(), entityIn.getCollisionBorderSize()) : boundingBox), hitboxRenderEvent.getBoxColor().getRed(), hitboxRenderEvent.getBoxColor().getGreen(), hitboxRenderEvent.getBoxColor().getBlue(), hitboxRenderEvent.getBoxColor().getAlpha());
            }
        } else {
            if (!hitboxRenderEvent.getCancelLineOfSight())
                RenderGlobal.drawOutlinedBoundingBox(boundingBox, hitboxRenderEvent.getLineOfSightColor().getRed(), hitboxRenderEvent.getLineOfSightColor().getGreen(), hitboxRenderEvent.getLineOfSightColor().getBlue(), hitboxRenderEvent.getLineOfSightColor().getAlpha());
        }
    }

    @Inject(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V"), cancellable = true)
    private void cancelEyeLine(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!hitboxRenderEvent.getCancelEyeLine()) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            Vec3 vec3 = entityIn.getLook(partialTicks);

            worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(x, y + (double) entityIn.getEyeHeight(), z).color(hitboxRenderEvent.getEyeLineColor().getRed(), hitboxRenderEvent.getEyeLineColor().getGreen(), hitboxRenderEvent.getEyeLineColor().getBlue(), hitboxRenderEvent.getEyeLineColor().getAlpha()).endVertex();
            worldrenderer.pos(x + vec3.xCoord * 2.0D, y + (double) entityIn.getEyeHeight() + vec3.yCoord * 2.0D, z + vec3.zCoord * 2.0D).color(hitboxRenderEvent.getEyeLineColor().getRed(), hitboxRenderEvent.getEyeLineColor().getGreen(), hitboxRenderEvent.getEyeLineColor().getBlue(), hitboxRenderEvent.getEyeLineColor().getAlpha()).endVertex();
            tessellator.draw();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        ci.cancel();
    }


    /**
     * Adapted from EvergreenHUD under GPLv3
     * https://github.com/isXander/EvergreenHUD/blob/main/LICENSE
     * <p>
     * Modified to be more compact.
     */
    private double getReachDistanceFromEntity(Entity entity) {
        if (entity == null) return -1.0;
        Minecraft.getMinecraft().mcProfiler.startSection("Calculating Reach Dist");

        double maxSize = 6D;
        AxisAlignedBB otherBB = entity.getEntityBoundingBox();
        float collisionBorderSize = entity.getCollisionBorderSize();
        AxisAlignedBB otherHitbox = otherBB.expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        Vec3 eyePos = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0F);
        Vec3 lookPos = Minecraft.getMinecraft().thePlayer.getLook(1.0F);
        Vec3 adjustedPos = eyePos.addVector(lookPos.xCoord * maxSize, lookPos.yCoord * maxSize, lookPos.zCoord * maxSize);
        MovingObjectPosition movingObjectPosition = otherHitbox.calculateIntercept(eyePos, adjustedPos);
        if (movingObjectPosition == null)
            return -1.0;
        // finally calculate distance between both vectors
        double dist = eyePos.distanceTo(movingObjectPosition.hitVec);
        Minecraft.getMinecraft().mcProfiler.endSection();
        return dist;
    }
}
