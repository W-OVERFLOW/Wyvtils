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

package xyz.qalcyo.rysm.mixin;

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
import xyz.qalcyo.requisite.Requisite;
import xyz.qalcyo.rysm.config.RysmConfig;
import xyz.qalcyo.rysm.gui.BossHealthGui;
import xyz.qalcyo.rysm.gui.SidebarGui;
import xyz.qalcyo.rysm.utils.HypixelUtils;

import java.awt.*;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {

    @Shadow
    @Final
    protected Minecraft mc;

    @Shadow public abstract FontRenderer getFontRenderer();

    private int i;
    private int x;
    private int bottom;
    private int lines;
    private int count;

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    protected void renderBossHealth(CallbackInfo ci) {
        if (mc.currentScreen instanceof BossHealthGui) {
            ci.cancel();
            return;
        }
        if (RysmConfig.INSTANCE.getBossBarCustomization() && !RysmConfig.INSTANCE.getBossBar()) {
            ci.cancel();
            return;
        }
        if (RysmConfig.INSTANCE.getAutoBossbarLobby() && HypixelUtils.INSTANCE.getLobby()) {
            ci.cancel();
            return;
        }
        GlStateManager.pushMatrix();
        float iHaveNoIdeaWhatToNameThisFloat = RysmConfig.INSTANCE.getBossbarScale() - 1.0f;
        GlStateManager.translate(-RysmConfig.INSTANCE.getBossBarX() * iHaveNoIdeaWhatToNameThisFloat, -RysmConfig.INSTANCE.getBossBarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
        GlStateManager.scale(RysmConfig.INSTANCE.getBossbarScale(), RysmConfig.INSTANCE.getBossbarScale(), 1.0F);
        if (RysmConfig.INSTANCE.getBossbarBarColorOn()) {
            GlStateManager.color(RysmConfig.INSTANCE.getBossbarBarColor().getRed(), RysmConfig.INSTANCE.getBossbarBarColor().getGreen(), RysmConfig.INSTANCE.getBossbarBarColor().getBlue(), RysmConfig.INSTANCE.getBossbarBarColor().getAlpha());
        }
        checkFirstTime();
    }

    @Inject(method = "renderBossHealth", at = @At("TAIL"))
    private void pop(CallbackInfo ci) {
        GlStateManager.popMatrix();
    }

    @Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int injected(FontRenderer fontRenderer, String text, float x, float y, int color) {
        GlStateManager.color(RysmConfig.INSTANCE.getBossbarTextColor().getRed(), RysmConfig.INSTANCE.getBossbarTextColor().getGreen(), RysmConfig.INSTANCE.getBossbarTextColor().getBlue(), RysmConfig.INSTANCE.getBossbarTextColor().getAlpha());
        if (RysmConfig.INSTANCE.getBossBarCustomization()) {
            if (RysmConfig.INSTANCE.getBossBarText()) {
                fontRenderer.drawString(
                        BossStatus.bossName,
                        Float.parseFloat(String.valueOf(RysmConfig.INSTANCE.getBossBarX() - (fontRenderer.getStringWidth(text) / 2))), RysmConfig.INSTANCE.getBossBarY() - 10,
                        Color.WHITE.getRGB(), RysmConfig.INSTANCE.getBossBarShadow()
                );
            }
        } else {
            fontRenderer.drawStringWithShadow(text, x, y, color);
        }
        GlStateManager.resetColor();
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
            if (RysmConfig.INSTANCE.getBackgroundBorder()) {
                if (RysmConfig.INSTANCE.getSidebarPosition()) {
                    Requisite.getInstance().getRenderHelper().drawHollowRect(RysmConfig.INSTANCE.getSidebarX() - i - 2 - RysmConfig.INSTANCE.getBorderNumber(), RysmConfig.INSTANCE.getSidebarY() - (lines + 1) * getFontRenderer().FONT_HEIGHT - 1 - RysmConfig.INSTANCE.getBorderNumber(), i + RysmConfig.INSTANCE.getBorderNumber() * 2 + 2, (lines + 1) * getFontRenderer().FONT_HEIGHT + 1 + RysmConfig.INSTANCE.getBorderNumber() * 2, RysmConfig.INSTANCE.getBorderNumber(), RysmConfig.INSTANCE.getBorderColor().getRGB());
                } else {
                    Requisite.getInstance().getRenderHelper().drawHollowRect(x - 2 - RysmConfig.INSTANCE.getBorderNumber(), bottom - (lines + 1) * getFontRenderer().FONT_HEIGHT - 1 - RysmConfig.INSTANCE.getBorderNumber(), i + RysmConfig.INSTANCE.getBorderNumber() * 2 + 4, (lines + 1) * getFontRenderer().FONT_HEIGHT + 1 + RysmConfig.INSTANCE.getBorderNumber() * 2, RysmConfig.INSTANCE.getBorderNumber(), RysmConfig.INSTANCE.getBorderColor().getRGB());
                }
            }
            GlStateManager.popMatrix();
        }
    }

    private void checkFirstTime() {
        if (RysmConfig.INSTANCE.getFirstLaunchBossbar()) {
            RysmConfig.INSTANCE.setFirstLaunchBossbar(false);
            RysmConfig.INSTANCE.setBossBarX(new ScaledResolution(mc).getScaledWidth() / 2);
            RysmConfig.INSTANCE.setBossBarY(12);
            RysmConfig.INSTANCE.markDirty();
            RysmConfig.INSTANCE.writeData();
        }
    }

}
