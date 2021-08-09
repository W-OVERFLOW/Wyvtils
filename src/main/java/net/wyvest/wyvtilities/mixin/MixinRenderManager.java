package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Inject(method = "renderDebugBoundingBox", at = @At(value = "HEAD"), cancellable = true)
    private void cancelForSelf(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getDisableForSelf()) {
            if (entityIn instanceof EntityPlayerSP) {
                if (entityIn == Minecraft.getMinecraft().thePlayer) {
                    ci.cancel();
                }
            }
        }
    }

    @Redirect(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;IIII)V"))
    private void addHitBoxAndSight(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha) {
        if (green == 255) {
            if (WyvtilsConfig.INSTANCE.getHitbox())
                RenderGlobal.drawOutlinedBoundingBox(boundingBox, WyvtilsConfig.INSTANCE.getHitboxColor().getRed(), WyvtilsConfig.INSTANCE.getHitboxColor().getGreen(), WyvtilsConfig.INSTANCE.getHitboxColor().getBlue(), WyvtilsConfig.INSTANCE.getHitboxColor().getAlpha());
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

}
