package net.wyvest.wyvtilities.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    private final Minecraft mc = Minecraft.getMinecraft();
    @Inject(method = "renderRecordOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"), cancellable = true)
    public void renderActionBar(int width, int height, float partialTicks, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getActionBarCustomization()) {
            mc.mcProfiler.startSection("overlayMessage");
            AccessorGuiIngame guiIngame = (AccessorGuiIngame) mc.ingameGUI;
            float hue = (float)guiIngame.getRecordPlayingUpFor() - partialTicks;
            int opacity = (int)(hue * 256.0F / 20.0F);
            if (opacity > 255) opacity = 255;
            if (opacity > 0)
            {
                GlStateManager.pushMatrix();
                if (!WyvtilsConfig.INSTANCE.getActionBarPosition()) {
                    GlStateManager.translate((float)(width / 2), (float)(height - 68), 0.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                }
                int color = (guiIngame.getRecordIsPlaying() ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & Color.WHITE.getRGB() : Color.WHITE.getRGB());
                if (WyvtilsConfig.INSTANCE.getActionBar()) {
                    int x;
                    int y;
                    if (WyvtilsConfig.INSTANCE.getActionBarPosition()) {
                        x = WyvtilsConfig.INSTANCE.getActionBarX() - (mc.fontRendererObj.getStringWidth(guiIngame.getRecordPlaying()) / 2);
                        y = WyvtilsConfig.INSTANCE.getActionBarY();
                    } else {
                        x = -mc.fontRendererObj.getStringWidth(guiIngame.getRecordPlaying()) / 2;
                        y = -4;
                    }
                    mc.fontRendererObj.drawString(guiIngame.getRecordPlaying(), x, y, color | (opacity << 24));
                }
                if (!WyvtilsConfig.INSTANCE.getActionBarPosition()) GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            mc.mcProfiler.endSection();
            ci.cancel();
        }
    }
}
