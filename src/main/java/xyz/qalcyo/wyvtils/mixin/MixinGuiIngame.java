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

package xyz.qalcyo.wyvtils.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.wyvtils.config.WyvtilsConfig;
import xyz.qalcyo.wyvtils.gui.BossHealthGui;
import xyz.qalcyo.wyvtils.gui.SidebarGui;
import xyz.qalcyo.wyvtils.utils.GlUtil;

import java.awt.Color;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {

    @Shadow
    @Final
    protected Minecraft mc;

    @Shadow public abstract FontRenderer getFontRenderer();

    private int i;
    private int x;
    private int bottom;
    private int lines = 0;
    private int count = 0;

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    protected void renderBossHealth(CallbackInfo ci) {
        if (mc.currentScreen instanceof BossHealthGui) {
            ci.cancel();
            return;
        }
        if (WyvtilsConfig.INSTANCE.getBossBarCustomization() && !WyvtilsConfig.INSTANCE.getBossBar()) {
            ci.cancel();
            return;
        }
        GlStateManager.pushMatrix();
        float iHaveNoIdeaWhatToNameThisFloat = WyvtilsConfig.INSTANCE.getBossbarScale() - 1.0f;
        GlStateManager.translate(-WyvtilsConfig.INSTANCE.getBossBarX() * iHaveNoIdeaWhatToNameThisFloat, -WyvtilsConfig.INSTANCE.getBossBarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
        GlStateManager.scale(WyvtilsConfig.INSTANCE.getBossbarScale(), WyvtilsConfig.INSTANCE.getBossbarScale(), 1.0F);
    }

    @Inject(method = "renderBossHealth", at = @At("TAIL"))
    private void pop(CallbackInfo ci) {
        GlStateManager.popMatrix();
    }

    @Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int injected(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (WyvtilsConfig.INSTANCE.getBossBarCustomization()) {
            if (WyvtilsConfig.INSTANCE.getBossBarText()) {
                checkFirstTime();
                return fontRenderer.drawString(
                        BossStatus.bossName,
                        Float.parseFloat(String.valueOf(WyvtilsConfig.INSTANCE.getBossBarX() - (fontRenderer.getStringWidth(text) / 2))), WyvtilsConfig.INSTANCE.getBossBarY() - 10,
                        Color.WHITE.getRGB(), WyvtilsConfig.INSTANCE.getBossBarShadow()
                );
            } else {
                return 1;
            }
        } else {
            return fontRenderer.drawStringWithShadow(text, x, y, color);
        }
    }

    @Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawTexturedModalRect(IIIIII)V"))
    private void removeBar(GuiIngame guiIngame, int x, int y, int textureX, int textureY, int width, int height) {
        if (WyvtilsConfig.INSTANCE.getBossBarCustomization()) {
            if (WyvtilsConfig.INSTANCE.getBossBarBar()) {
                mc.ingameGUI.drawTexturedModalRect(WyvtilsConfig.INSTANCE.getBossBarX() - 91, WyvtilsConfig.INSTANCE.getBossBarY(), textureX, textureY, width, height);
            }
        } else {
            mc.ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
        }
    }


    @Inject(method = "renderScoreboard", at = @At(value = "HEAD"), cancellable = true)
    private void removeScoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (!WyvtilsConfig.INSTANCE.getSidebar() || mc.currentScreen instanceof SidebarGui) {
            ci.cancel();
        } else {
            GlStateManager.pushMatrix();
            lines = 0;
            count = 0;
        }
    }

    @ModifyArg(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"), index = 0)
    private int max(int i, int width) {
        ++lines;
        this.i = Math.max(i, width);
        return i;
    }

    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 2)
    private int modifyHeight(int x) {
        bottom = x;
        return (WyvtilsConfig.INSTANCE.getSidebarPosition() ? WyvtilsConfig.INSTANCE.getSidebarY() : x);
    }

    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 4)
    private int modifyWidth(int x) {
        this.x = x;
        return (WyvtilsConfig.INSTANCE.getSidebarPosition() ? WyvtilsConfig.INSTANCE.getSidebarX() - i : x);
    }

    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 7)
    private int modifyWidth2(int x) {
        count++;
        if (count == 1) {
            float iHaveNoIdeaWhatToNameThisFloat = WyvtilsConfig.INSTANCE.getSidebarScale() - 1.0f;
            if (WyvtilsConfig.INSTANCE.getSidebarPosition()) {
                GlStateManager.translate(-WyvtilsConfig.INSTANCE.getSidebarX() * iHaveNoIdeaWhatToNameThisFloat, -WyvtilsConfig.INSTANCE.getSidebarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
            } else {
                GlStateManager.translate(-(this.x + i) * iHaveNoIdeaWhatToNameThisFloat, -bottom * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
            }
            GlStateManager.scale(WyvtilsConfig.INSTANCE.getSidebarScale(), WyvtilsConfig.INSTANCE.getSidebarScale(), 1.0F);
        }
        return (WyvtilsConfig.INSTANCE.getSidebarPosition() ? WyvtilsConfig.INSTANCE.getSidebarX() : x);
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    private int handleDrawString(FontRenderer fontRenderer, String text, int x, int y, int color, ScoreObjective objective, ScaledResolution scaledRes) {
        if (WyvtilsConfig.INSTANCE.getSidebarScorePoints() || (WyvtilsConfig.INSTANCE.getSidebarPosition() ? (x != WyvtilsConfig.INSTANCE.getSidebarX() - fontRenderer.getStringWidth(text)) : (x != scaledRes.getScaledWidth() - 1 - fontRenderer.getStringWidth(text)))) {
            return fontRenderer.drawString(text, x, y, color, WyvtilsConfig.INSTANCE.getSidebarTextShadow());
        } else {
            return 0;
        }
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawRect(IIIII)V"))
    private void handleBackground(int left, int top, int right, int bottom, int color) {
        if (WyvtilsConfig.INSTANCE.getSidebarBackground()) {
            Gui.drawRect(left, top, right, bottom, WyvtilsConfig.INSTANCE.getSidebarBackgroundColor().getRGB());
        }
    }

    @Inject(method = "renderScoreboard", at = @At("TAIL"))
    private void popMatrix(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getSidebar()) {
            if (WyvtilsConfig.INSTANCE.getBackgroundBorder()) {
                if (WyvtilsConfig.INSTANCE.getSidebarPosition()) {
                    GlUtil.INSTANCE.drawHollowRectangle(WyvtilsConfig.INSTANCE.getSidebarX() - i - 2 - WyvtilsConfig.INSTANCE.getBorderNumber(), WyvtilsConfig.INSTANCE.getSidebarY() - (lines + 1) * getFontRenderer().FONT_HEIGHT - 1 - WyvtilsConfig.INSTANCE.getBorderNumber(), i + WyvtilsConfig.INSTANCE.getBorderNumber() * 2 + 2, (lines + 1) * getFontRenderer().FONT_HEIGHT + 1 + WyvtilsConfig.INSTANCE.getBorderNumber() * 2, WyvtilsConfig.INSTANCE.getBorderNumber(), WyvtilsConfig.INSTANCE.getBorderColor().getRGB());
                } else {
                    GlUtil.INSTANCE.drawHollowRectangle(x - 2 - WyvtilsConfig.INSTANCE.getBorderNumber(), bottom - (lines + 1) * getFontRenderer().FONT_HEIGHT - 1 - WyvtilsConfig.INSTANCE.getBorderNumber(), i + WyvtilsConfig.INSTANCE.getBorderNumber() * 2 + 4, (lines + 1) * getFontRenderer().FONT_HEIGHT + 1 + WyvtilsConfig.INSTANCE.getBorderNumber() * 2, WyvtilsConfig.INSTANCE.getBorderNumber(), WyvtilsConfig.INSTANCE.getBorderColor().getRGB());
                }
            }
            GlStateManager.popMatrix();
        }
    }

    private void checkFirstTime() {
        if (WyvtilsConfig.INSTANCE.getFirstLaunchBossbar()) {
            WyvtilsConfig.INSTANCE.setFirstLaunchBossbar(false);
            WyvtilsConfig.INSTANCE.setBossBarX(new ScaledResolution(mc).getScaledWidth() / 2);
            WyvtilsConfig.INSTANCE.setBossBarY(12);
            WyvtilsConfig.INSTANCE.markDirty();
            WyvtilsConfig.INSTANCE.writeData();
        }
    }

}
