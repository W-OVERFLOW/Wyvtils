/*
 * Wyvtilities - Utilities for Hypixel 1.8.9.
 * Copyright (C) 2021 Wyvtilities
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

package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    boolean withinRange = false;

    @Inject(method = "renderDebugBoundingBox", at = @At(value = "HEAD"), cancellable = true)
    private void cancelForSelf(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getDisableForSelf()) {
            if (entityIn instanceof EntityPlayerSP) {
                if (entityIn == Minecraft.getMinecraft().thePlayer) {
                    ci.cancel();
                }
            }
        }
        if (!WyvtilsConfig.INSTANCE.getItemHitBox()) {
            if (entityIn instanceof EntityItem) {
                ci.cancel();
            }
        }
        if (!WyvtilsConfig.INSTANCE.getItemFrameHitBox()) {
            if (entityIn instanceof EntityItemFrame) {
                ci.cancel();
            }
        }
        if (!WyvtilsConfig.INSTANCE.getNonplayerHitbox()) {
            if (!(entityIn instanceof AbstractClientPlayer)) {
                ci.cancel();
            }
        }
        if (WyvtilsConfig.INSTANCE.getHitboxColor() != WyvtilsConfig.INSTANCE.getHitboxRangeColor()) {
            if (getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer, entityIn) <= 9 && Minecraft.getMinecraft().thePlayer != entityIn) {
                withinRange = true;
            }
        }
    }

    @Inject(method = "doRenderEntity", at = @At(value = "HEAD"))
    private void forceHitbox(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean hideDebugBox, CallbackInfoReturnable<Boolean> cir) {
        if (WyvtilsConfig.INSTANCE.getForceHitbox()) {
            if (!Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox())
                Minecraft.getMinecraft().getRenderManager().setDebugBoundingBox(true);
        }
    }

    @Redirect(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;IIII)V"))
    private void addHitBoxAndSight(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha) {
        if (green == 255) {
            if (WyvtilsConfig.INSTANCE.getHitbox()) {
                if (withinRange) {
                    withinRange = false;
                    RenderGlobal.drawOutlinedBoundingBox(boundingBox, WyvtilsConfig.INSTANCE.getHitboxRangeColor().getRed(), WyvtilsConfig.INSTANCE.getHitboxRangeColor().getGreen(), WyvtilsConfig.INSTANCE.getHitboxRangeColor().getBlue(), WyvtilsConfig.INSTANCE.getHitboxRangeColor().getAlpha());
                } else {
                    RenderGlobal.drawOutlinedBoundingBox(boundingBox, WyvtilsConfig.INSTANCE.getHitboxColor().getRed(), WyvtilsConfig.INSTANCE.getHitboxColor().getGreen(), WyvtilsConfig.INSTANCE.getHitboxColor().getBlue(), WyvtilsConfig.INSTANCE.getHitboxColor().getAlpha());
                }
            }
        } else {
            if (WyvtilsConfig.INSTANCE.getHitboxLineOfSight())
                RenderGlobal.drawOutlinedBoundingBox(boundingBox, WyvtilsConfig.INSTANCE.getHitboxLineOfSightColor().getRed(), WyvtilsConfig.INSTANCE.getHitboxLineOfSightColor().getGreen(), WyvtilsConfig.INSTANCE.getHitboxLineOfSightColor().getBlue(), WyvtilsConfig.INSTANCE.getHitboxLineOfSightColor().getAlpha());
        }
    }

    @Inject(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V"), cancellable = true)
    private void addEyeLine(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getHitboxEyeLine()) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            Vec3 vec3 = entityIn.getLook(partialTicks);

            worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(x, y + (double) entityIn.getEyeHeight(), z).color(WyvtilsConfig.INSTANCE.getHitboxEyelineColor().getRed(), WyvtilsConfig.INSTANCE.getHitboxEyelineColor().getGreen(), WyvtilsConfig.INSTANCE.getHitboxEyelineColor().getBlue(), WyvtilsConfig.INSTANCE.getHitboxEyelineColor().getAlpha()).endVertex();
            worldrenderer.pos(x + vec3.xCoord * 2.0D, y + (double) entityIn.getEyeHeight() + vec3.yCoord * 2.0D, z + vec3.zCoord * 2.0D).color(WyvtilsConfig.INSTANCE.getHitboxEyelineColor().getRed(), WyvtilsConfig.INSTANCE.getHitboxEyelineColor().getGreen(), WyvtilsConfig.INSTANCE.getHitboxEyelineColor().getBlue(), WyvtilsConfig.INSTANCE.getHitboxEyelineColor().getAlpha()).endVertex();
            tessellator.draw();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        ci.cancel();
    }

    private float getDistanceSqToEntity(Entity entityIn, Entity entityIn2) {
        float f = (float) (entityIn2.posX - entityIn.posX);
        float f1 = (float) (entityIn2.posY - entityIn.posY);
        float f2 = (float) (entityIn2.posZ - entityIn.posZ);
        return f * f + f1 * f1 + f2 * f2;
    }

}
