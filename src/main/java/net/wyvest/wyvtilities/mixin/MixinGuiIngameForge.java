package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import net.wyvest.wyvtilities.gui.ActionBarGui;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = GuiIngameForge.class, remap = false)
public class MixinGuiIngameForge {

    Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "renderRecordOverlay", at = @At(value = "HEAD"), cancellable = true)
    public void renderActionBar(int width, int height, float partialTicks, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getActionBarCustomization()) {
            ci.cancel();
            if (!WyvtilsConfig.INSTANCE.getActionBar() || Minecraft.getMinecraft().currentScreen instanceof ActionBarGui)
                return;
            AccessorGuiIngame guiIngame = (AccessorGuiIngame) mc.ingameGUI;
            if (guiIngame.getRecordPlayingUpFor() > 0) {
                mc.mcProfiler.startSection("overlayMessage");
                float hue = (float) guiIngame.getRecordPlayingUpFor() - partialTicks;
                int opacity = (int) (hue * 256.0F / 20.0F);
                if (opacity > 255) opacity = 255;

                if (opacity > 0) {
                    int newX;
                    int newY;
                    if (WyvtilsConfig.INSTANCE.getActionBarPosition()) {
                        newX = WyvtilsConfig.INSTANCE.getActionBarX();
                        newY = WyvtilsConfig.INSTANCE.getActionBarY();
                    } else {
                        newX = -mc.fontRendererObj.getStringWidth(guiIngame.getRecordPlaying()) / 2;
                        newY = -4;
                    }

                    GlStateManager.pushMatrix();
                    if (!WyvtilsConfig.INSTANCE.getActionBarPosition() && WyvtilsConfig.INSTANCE.getActionBarCustomization())
                        GlStateManager.translate((float) (width / 2), (float) (height - 68), 0.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                    int color = (guiIngame.getRecordIsPlaying() ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & Color.WHITE.getRGB() : Color.WHITE.getRGB());
                    mc.fontRendererObj.drawString(guiIngame.getRecordPlaying(), newX, newY, color | (opacity << 24), WyvtilsConfig.INSTANCE.getActionBarShadow());
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }

                mc.mcProfiler.endSection();
            }
        }
    }

    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void removeTitle(int width, int height, float partialTicks, CallbackInfo ci) {
        if (!WyvtilsConfig.INSTANCE.getTitle()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"))
    private void modifyTitleScale1(float x, float y, float z) {
        if (WyvtilsConfig.INSTANCE.getTitleScale() == 1.0F && WyvtilsConfig.INSTANCE.getSubtitleScale() == 1.0F) {
            GlStateManager.scale(x, y, z);
        } else {
            if (x == 4.0F) {
                //Title
                GlStateManager.scale(x * WyvtilsConfig.INSTANCE.getTitleScale(), y * WyvtilsConfig.INSTANCE.getTitleScale(), z * WyvtilsConfig.INSTANCE.getTitleScale());
                return;
            }
            if (x == 2.0F) {
                //Subtitle
                GlStateManager.scale(x * WyvtilsConfig.INSTANCE.getSubtitleScale(), y * WyvtilsConfig.INSTANCE.getSubtitleScale(), z * WyvtilsConfig.INSTANCE.getSubtitleScale());
            }
        }
    }

}
