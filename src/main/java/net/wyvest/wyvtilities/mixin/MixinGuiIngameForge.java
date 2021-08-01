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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = GuiIngameForge.class, remap = false)
public class MixinGuiIngameForge {

    Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "renderRecordOverlay", at = @At(value = "HEAD"), cancellable = true)
    public void renderActionBar(int width, int height, float partialTicks, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getActionBarCustomization()) {
            ci.cancel();
            if (!WyvtilsConfig.INSTANCE.getActionBar() || Minecraft.getMinecraft().currentScreen instanceof ActionBarGui) return;
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
                    mc.fontRendererObj.drawString(guiIngame.getRecordPlaying(), newX, newY, color | (opacity << 24));
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }

                mc.mcProfiler.endSection();
            }
        }
    }

    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void removeTitle(int width, int height, float partialTicks, CallbackInfo ci) {
        if ((WyvtilsConfig.INSTANCE.getActionBarCustomization() && !WyvtilsConfig.INSTANCE.getActionBar()) || Minecraft.getMinecraft().currentScreen instanceof ActionBarGui) {
            ci.cancel();
        }
    }

    @ModifyArg(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"), index = 0)
    private float modifyTitleScale1(float x) {
        if (x == 4) {
            //Title
            return x * WyvtilsConfig.INSTANCE.getTitleScale();
        }
        if (x == 2) {
            //Subtitle
            return x * WyvtilsConfig.INSTANCE.getSubtitleScale();
        }
        return x;
    }

    @ModifyArg(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"), index = 1)
    private float modifyTitleScale2(float x) {
        if (x == 4) {
            //Title
            return x * WyvtilsConfig.INSTANCE.getTitleScale();
        }
        if (x == 2) {
            //Subtitle
            return x * WyvtilsConfig.INSTANCE.getSubtitleScale();
        }
        return x;
    }

    @ModifyArg(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"), index = 2)
    private float modifyTitleScale3(float x) {
        if (x == 4) {
            //Title
            return x * WyvtilsConfig.INSTANCE.getTitleScale();
        }
        if (x == 2) {
            //Subtitle
            return x * WyvtilsConfig.INSTANCE.getSubtitleScale();
        }
        return x;
    }

}
