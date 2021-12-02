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

package net.wyvest.wyvtils.eight.mixin.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.wyvest.wyvtils.core.config.WyvtilsConfig;
import net.wyvest.wyvtils.eight.hooks.RenderManagerHookKt;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin sends and handles the HitboxRenderEvent
 * which is used to handle hitbox related features in
 * Wyvtils. This also manually adds some Wyvtils features related
 * to the hitbox.
 */
@Mixin(RenderManager.class)
public class RenderManagerMixin {

    /**
     * Forces the hitbox to render.
     */
    @Inject(method = "doRenderEntity", at = @At(value = "HEAD"))
    private void forceHitbox(Entity p_doRenderEntity_1_, double p_doRenderEntity_2_, double p_doRenderEntity_3_, double p_doRenderEntity_4_, float p_doRenderEntity_5_, float p_doRenderEntity_6_, boolean p_doRenderEntity_8_, CallbackInfoReturnable<Boolean> cir) {
        if (WyvtilsConfig.INSTANCE.getForceHitbox()) {
            if (!Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox())
                Minecraft.getMinecraft().getRenderManager().setDebugBoundingBox(true);
        }
    }

    /**
     * Invokes a HitboxRenderEvent and cancels the rendering of the hitbox accordingly.
     */
    @Inject(method = "renderDebugBoundingBox", at = @At(value = "HEAD"), cancellable = true)
    private void invokeHitboxEvent(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        RenderManagerHookKt.setup(entityIn, ci);
    }

    /**
     * Cancels and changes the colour and size of the hitbox and hitbox line of sight accordingly.
     */
    @Redirect(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;IIII)V"))
    private void cancelLineOfSightAndBox(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha, Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
        RenderManagerHookKt.cancelLineOfSightAndBox(boundingBox, green, entityIn);
    }


    /**
     * Cancels and changes the colour and size of the hitbox eye line accordingly.
     */
    @Inject(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V"), cancellable = true)
    private void cancelEyeLine(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        RenderManagerHookKt.cancelEyeLine(entityIn, x, y, z, partialTicks, ci);
    }

    @Inject(method = "renderDebugBoundingBox", at = @At("RETURN"))
    private void reset(Entity p_renderDebugBoundingBox_1_, double p_renderDebugBoundingBox_2_, double p_renderDebugBoundingBox_445y_, double p_renderDebugBoundingBox_4_, float p_renderDebugBoundingBox_6_, float p_render63DebugBoundingBox_6_, CallbackInfo ci) {
        GL11.glLineWidth(1);
    }


}
