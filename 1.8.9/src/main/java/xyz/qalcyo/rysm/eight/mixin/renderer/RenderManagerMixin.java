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

package xyz.qalcyo.rysm.eight.mixin.renderer;

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
import xyz.qalcyo.rysm.core.utils.ColorUtils;

import java.awt.*;

/**
 * This mixin sends and handles the HitboxRenderEvent
 * which is used to handle hitbox related features in
 * Rysm. This also manually adds some Rysm features related
 * to the hitbox.
 */
@Mixin(RenderManager.class)
public class RenderManagerMixin {
    private HitboxRenderEvent hitboxRenderEvent;

    /**
     * Forces the hitbox to render.
     */
    @Inject(method = "doRenderEntity", at = @At(value = "HEAD"))
    private void forceHitbox(Entity p_doRenderEntity_1_, double p_doRenderEntity_2_, double p_doRenderEntity_3_, double p_doRenderEntity_4_, float p_doRenderEntity_5_, float p_doRenderEntity_6_, boolean p_doRenderEntity_8_, CallbackInfoReturnable<Boolean> cir) {
        if (RysmConfig.INSTANCE.getForceHitbox()) {
            if (!Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox())
                Minecraft.getMinecraft().getRenderManager().setDebugBoundingBox(true);
        }
    }

    /**
     * Invokes a HitboxRenderEvent and cancels the rendering of the hitbox accordingly.
     */
    @Inject(method = "renderDebugBoundingBox", at = @At(value = "HEAD"), cancellable = true)
    private void invokeHitboxEvent(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (entityIn instanceof EntityPlayer) {
            hitboxRenderEvent = new HitboxRenderEvent((entityIn instanceof EntityPlayerSP && ((EntityPlayer) entityIn).getGameProfile().getId() == Minecraft.getMinecraft().thePlayer.getGameProfile().getId()) ? xyz.qalcyo.rysm.core.listener.events.Entity.SELF : xyz.qalcyo.rysm.core.listener.events.Entity.PLAYER, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else if (entityIn instanceof EntityLiving) {
            hitboxRenderEvent = new HitboxRenderEvent(entityIn instanceof IMob ? xyz.qalcyo.rysm.core.listener.events.Entity.MONSTER : xyz.qalcyo.rysm.core.listener.events.Entity.LIVING, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else if (entityIn instanceof EntityArmorStand) {
            hitboxRenderEvent = new HitboxRenderEvent(xyz.qalcyo.rysm.core.listener.events.Entity.ARMORSTAND, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else if (entityIn instanceof EntityFireball) {
            hitboxRenderEvent = new HitboxRenderEvent((entityIn instanceof EntityWitherSkull ? xyz.qalcyo.rysm.core.listener.events.Entity.WITHERSKULL : xyz.qalcyo.rysm.core.listener.events.Entity.FIREBALL), getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else if (entityIn instanceof EntityMinecart) {
            hitboxRenderEvent = new HitboxRenderEvent(xyz.qalcyo.rysm.core.listener.events.Entity.MINECART, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else if (entityIn instanceof EntityItem) {
            hitboxRenderEvent = new HitboxRenderEvent(xyz.qalcyo.rysm.core.listener.events.Entity.ITEM, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else if (entityIn instanceof EntityFireworkRocket) {
            hitboxRenderEvent = new HitboxRenderEvent(xyz.qalcyo.rysm.core.listener.events.Entity.FIREWORK, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else if (entityIn instanceof EntityXPOrb) {
            hitboxRenderEvent = new HitboxRenderEvent(xyz.qalcyo.rysm.core.listener.events.Entity.XP, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else if (entityIn instanceof IProjectile) {
            hitboxRenderEvent = new HitboxRenderEvent(xyz.qalcyo.rysm.core.listener.events.Entity.PROJECTILE, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        } else {
            hitboxRenderEvent = new HitboxRenderEvent(xyz.qalcyo.rysm.core.listener.events.Entity.UNDEFINED, getReachDistanceFromEntity(entityIn), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), false, false, false, false);
        }
        RysmCore.INSTANCE.getEventBus().post(hitboxRenderEvent);
        if (hitboxRenderEvent.getCancelled()) ci.cancel();
    }

    /**
     * Cancels and changes the colour and size of the hitbox and hitbox line of sight accordingly.
     */
    @Redirect(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;IIII)V"))
    private void cancelLineOfSightAndBox(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha, Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
        if (green == 255) {
            if (!hitboxRenderEvent.getCancelBox()) {
                RenderGlobal.drawOutlinedBoundingBox((RysmConfig.INSTANCE.getAccurateHitbox() ? boundingBox.expand(entityIn.getCollisionBorderSize(), entityIn.getCollisionBorderSize(), entityIn.getCollisionBorderSize()) : boundingBox), ColorUtils.INSTANCE.getRed(hitboxRenderEvent.getBoxColor()), ColorUtils.INSTANCE.getGreen(hitboxRenderEvent.getBoxColor()), ColorUtils.INSTANCE.getBlue(hitboxRenderEvent.getBoxColor()), ColorUtils.INSTANCE.getAlpha(hitboxRenderEvent.getBoxColor()));
            }
        } else {
            if (!hitboxRenderEvent.getCancelLineOfSight())
                RenderGlobal.drawOutlinedBoundingBox(boundingBox, ColorUtils.INSTANCE.getRed(hitboxRenderEvent.getLineOfSightColor()), ColorUtils.INSTANCE.getGreen(hitboxRenderEvent.getLineOfSightColor()), ColorUtils.INSTANCE.getBlue(hitboxRenderEvent.getLineOfSightColor()), ColorUtils.INSTANCE.getAlpha(hitboxRenderEvent.getLineOfSightColor()));
        }
    }


    /**
     * Cancels and changes the colour and size of the hitbox eye line accordingly.
     */
    @Inject(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V"), cancellable = true)
    private void cancelEyeLine(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!hitboxRenderEvent.getCancelEyeLine()) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            Vec3 vec3 = entityIn.getLook(partialTicks);

            worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(x, y + (double) entityIn.getEyeHeight(), z).color(ColorUtils.INSTANCE.getRed(hitboxRenderEvent.getEyeLineColor()), ColorUtils.INSTANCE.getGreen(hitboxRenderEvent.getEyeLineColor()), ColorUtils.INSTANCE.getBlue(hitboxRenderEvent.getEyeLineColor()), ColorUtils.INSTANCE.getAlpha(hitboxRenderEvent.getEyeLineColor())).endVertex();
            worldrenderer.pos(x + vec3.xCoord * 2.0D, y + (double) entityIn.getEyeHeight() + vec3.yCoord * 2.0D, z + vec3.zCoord * 2.0D).color(ColorUtils.INSTANCE.getRed(hitboxRenderEvent.getEyeLineColor()), ColorUtils.INSTANCE.getGreen(hitboxRenderEvent.getEyeLineColor()), ColorUtils.INSTANCE.getBlue(hitboxRenderEvent.getEyeLineColor()), ColorUtils.INSTANCE.getAlpha(hitboxRenderEvent.getEyeLineColor())).endVertex();
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
