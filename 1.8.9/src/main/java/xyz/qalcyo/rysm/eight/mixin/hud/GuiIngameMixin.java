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

package xyz.qalcyo.rysm.eight.mixin.hud;

import gg.essential.universal.UResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.rysm.core.config.RysmConfig;
import xyz.qalcyo.rysm.eight.gui.BossHealthGui;
import xyz.qalcyo.rysm.eight.gui.SidebarGui;

import java.awt.*;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {
    @Shadow
    @Final
    protected Minecraft mc;

    @Shadow public abstract FontRenderer getFontRenderer();

    private int i;
    private int x;
    private int bottom;
    private int lines;
    private int count;

    private int the = 0;
    private boolean theFunny = false;

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    protected void renderBossHealth(CallbackInfo ci) {
        if (BossStatus.bossName == null || BossStatus.statusBarTime <= 0) {
            ci.cancel();
        } else if (mc.currentScreen instanceof BossHealthGui) {
            ci.cancel();
        } else if (RysmConfig.INSTANCE.getBossBarCustomization() && ((!RysmConfig.INSTANCE.getBossBar()) || (!RysmConfig.INSTANCE.getBossBarBar() && !RysmConfig.INSTANCE.getBossBarText()))) {
            ci.cancel();
        } else if (RysmConfig.INSTANCE.getBossbarScale() == 0.0F) {
            ci.cancel();
        } else {
            GlStateManager.pushMatrix();
            float iHaveNoIdeaWhatToNameThisFloat = RysmConfig.INSTANCE.getBossbarScale() - 1.0f;
            GlStateManager.translate(-RysmConfig.INSTANCE.getBossBarX() * iHaveNoIdeaWhatToNameThisFloat, -RysmConfig.INSTANCE.getBossBarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
            GlStateManager.scale(RysmConfig.INSTANCE.getBossbarScale(), RysmConfig.INSTANCE.getBossbarScale(), 1.0F);
            checkFirstTime();
        }
    }

    @Inject(method = "renderBossHealth", at = @At("TAIL"))
    private void pop(CallbackInfo ci) {
        GlStateManager.popMatrix();
    }

    @Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int injected(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (RysmConfig.INSTANCE.getBossBarCustomization()) {
            if (RysmConfig.INSTANCE.getBossBarText()) {
                return fontRenderer.drawString(
                        BossStatus.bossName, RysmConfig.INSTANCE.getBossBarX() - ((float) fontRenderer.getStringWidth(text) / 2), RysmConfig.INSTANCE.getBossBarY() - 10,
                        Color.WHITE.getRGB(), RysmConfig.INSTANCE.getBossBarShadow()
                );
            }
        } else {
            return fontRenderer.drawStringWithShadow(text, x, y, color);
        }
        return 1;
    }

    @Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawTexturedModalRect(IIIIII)V"))
    private void removeBar(GuiIngame guiIngame, int x, int y, int textureX, int textureY, int width, int height) {
        if (RysmConfig.INSTANCE.getBossBarCustomization()) {
            if (RysmConfig.INSTANCE.getBossBarBar()) {
                mc.ingameGUI.drawTexturedModalRect(RysmConfig.INSTANCE.getBossBarX() - 91, RysmConfig.INSTANCE.getBossBarY(), textureX, textureY, width, height);
            }
        } else {
            mc.ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
        }
    }

    @Inject(method = "renderScoreboard", at = @At(value = "HEAD"), cancellable = true)
    private void removeScoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (!RysmConfig.INSTANCE.getSidebar() || mc.currentScreen instanceof SidebarGui) {
            ci.cancel();
        } else {
            GlStateManager.pushMatrix();
            lines = 0;
            count = 0;
            the = 0;
            theFunny = false;
        }
    }

    @ModifyArg(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"), index = 0)
    private int max(int i, int width) {
        ++lines;
        this.i = Math.max(i, width);
        return i;
    }

    @ModifyArg(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I"), index = 0)
    private String yes(String original) {
        ++the;
        if (the == 1) {
            theFunny = true;
            return original;
        }
        if (theFunny && !RysmConfig.INSTANCE.getSidebarScorePoints()) {
            return StringUtils.substringBeforeLast(original, ": " + EnumChatFormatting.RED);
        }
        return original;
    }

    @Inject(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ScaledResolution;getScaledHeight()I"))
    private void yeah(ScoreObjective p_renderScoreboard_1_, ScaledResolution p_renderScoreboard_2_, CallbackInfo ci) {
        theFunny = false;
    }


    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 2)
    private int modifyHeight(int x) {
        bottom = x;
        return (RysmConfig.INSTANCE.getSidebarPosition() ? RysmConfig.INSTANCE.getSidebarY() : x);
    }

    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 4)
    private int modifyWidth(int x) {
        this.x = x;
        return (RysmConfig.INSTANCE.getSidebarPosition() ? RysmConfig.INSTANCE.getSidebarX() - i : x);
    }

    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 7)
    private int modifyWidth2(int x) {
        count++;
        if (count == 1) {
            float iHaveNoIdeaWhatToNameThisFloat = RysmConfig.INSTANCE.getSidebarScale() - 1.0f;
            if (RysmConfig.INSTANCE.getSidebarPosition()) {
                GlStateManager.translate(-RysmConfig.INSTANCE.getSidebarX() * iHaveNoIdeaWhatToNameThisFloat, -RysmConfig.INSTANCE.getSidebarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
            } else {
                GlStateManager.translate(-(this.x + i) * iHaveNoIdeaWhatToNameThisFloat, -bottom * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
            }
            GlStateManager.scale(RysmConfig.INSTANCE.getSidebarScale(), RysmConfig.INSTANCE.getSidebarScale(), 1.0F);
        }
        return (RysmConfig.INSTANCE.getSidebarPosition() ? RysmConfig.INSTANCE.getSidebarX() : x);
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    private int handleDrawString(FontRenderer fontRenderer, String text, int x, int y, int color, ScoreObjective objective, ScaledResolution scaledRes) {
        if (RysmConfig.INSTANCE.getSidebarScorePoints() || (RysmConfig.INSTANCE.getSidebarPosition() ? (x != RysmConfig.INSTANCE.getSidebarX() - fontRenderer.getStringWidth(text)) : (x != scaledRes.getScaledWidth() - 1 - fontRenderer.getStringWidth(text)))) {
            return fontRenderer.drawString(text, x, y, color, RysmConfig.INSTANCE.getSidebarTextShadow());
        } else {
            return 0;
        }
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawRect(IIIII)V"))
    private void handleBackground(int left, int top, int right, int bottom, int color) {
        if (RysmConfig.INSTANCE.getSidebarBackground()) {
            Gui.drawRect(left, top, right, bottom, RysmConfig.INSTANCE.getSidebarBackgroundColor().getRGB());
        }
    }

    @Inject(method = "renderScoreboard", at = @At("TAIL"))
    private void popMatrix(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (RysmConfig.INSTANCE.getSidebar()) {
        /*/
            if (RysmConfig.INSTANCE.getBackgroundBorder()) {
                if (RysmConfig.INSTANCE.getSidebarPosition()) {
                    Requisite.getInstance().getRenderHelper().drawHollowRect(RysmConfig.INSTANCE.getSidebarX() - i - 2 - RysmConfig.INSTANCE.getBorderNumber(), RysmConfig.INSTANCE.getSidebarY() - (lines + 1) * getFontRenderer().FONT_HEIGHT - 1 - RysmConfig.INSTANCE.getBorderNumber(), i + RysmConfig.INSTANCE.getBorderNumber() * 2 + 2, (lines + 1) * getFontRenderer().FONT_HEIGHT + 1 + RysmConfig.INSTANCE.getBorderNumber() * 2, RysmConfig.INSTANCE.getBorderNumber(), RysmConfig.INSTANCE.getBorderColor().getRGB());
                } else {
                    Requisite.getInstance().getRenderHelper().drawHollowRect(x - 2 - RysmConfig.INSTANCE.getBorderNumber(), bottom - (lines + 1) * getFontRenderer().FONT_HEIGHT - 1 - RysmConfig.INSTANCE.getBorderNumber(), i + RysmConfig.INSTANCE.getBorderNumber() * 2 + 4, (lines + 1) * getFontRenderer().FONT_HEIGHT + 1 + RysmConfig.INSTANCE.getBorderNumber() * 2, RysmConfig.INSTANCE.getBorderNumber(), RysmConfig.INSTANCE.getBorderColor().getRGB());
                }
            }

             */
            GlStateManager.popMatrix();
        }
    }

    private void checkFirstTime() {
        if (RysmConfig.INSTANCE.getFirstLaunchBossbar()) {
            RysmConfig.INSTANCE.setFirstLaunchBossbar(false);
            RysmConfig.INSTANCE.setBossBarX(Math.round((float) UResolution.getScaledWidth() / 2));
            RysmConfig.INSTANCE.setBossBarY(12);
            RysmConfig.INSTANCE.markDirty();
            RysmConfig.INSTANCE.writeData();
        }
    }


}