/*
 * Qaltils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Qaltils
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

package xyz.qalcyo.qaltils.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import xyz.qalcyo.qaltils.config.QaltilsConfig;
import xyz.qalcyo.qaltils.gui.BossHealthGui;
import xyz.qalcyo.qaltils.gui.SidebarGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Shadow @Final protected Minecraft mc;
    private int i;

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    protected void renderBossHealth(CallbackInfo ci) {
        if (mc.currentScreen instanceof BossHealthGui) {
            ci.cancel();
            return;
        }
        if (QaltilsConfig.INSTANCE.getBossBarCustomization() && !QaltilsConfig.INSTANCE.getBossBar()) {
            ci.cancel();
            return;
        }
        GlStateManager.pushMatrix();
        float iHaveNoIdeaWhatToNameThisFloat = QaltilsConfig.INSTANCE.getBossbarScale() - 1.0f;
        GlStateManager.translate(-QaltilsConfig.INSTANCE.getBossBarX() * iHaveNoIdeaWhatToNameThisFloat, -QaltilsConfig.INSTANCE.getBossBarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
        GlStateManager.scale(QaltilsConfig.INSTANCE.getBossbarScale(), QaltilsConfig.INSTANCE.getBossbarScale(), 1.0F);
    }

    @Inject(method = "renderBossHealth", at = @At("TAIL"))
    private void pop(CallbackInfo ci) {
        GlStateManager.popMatrix();
    }

    @Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int injected(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (QaltilsConfig.INSTANCE.getBossBarCustomization()) {
            if (QaltilsConfig.INSTANCE.getBossBarText()) {
                checkFirstTime();
                return fontRenderer.drawString(
                        BossStatus.bossName,
                        Float.parseFloat(String.valueOf(QaltilsConfig.INSTANCE.getBossBarX() - (fontRenderer.getStringWidth(text) / 2))), QaltilsConfig.INSTANCE.getBossBarY() - 10,
                        Color.WHITE.getRGB(), QaltilsConfig.INSTANCE.getBossBarShadow()
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
        if (QaltilsConfig.INSTANCE.getBossBarCustomization()) {
            if (QaltilsConfig.INSTANCE.getBossBarBar()) {
                mc.ingameGUI.drawTexturedModalRect(QaltilsConfig.INSTANCE.getBossBarX() - 91, QaltilsConfig.INSTANCE.getBossBarY(), textureX, textureY, width, height);
            }
        } else {
            mc.ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
        }
    }

    @Inject(method = "renderScoreboard", at = @At(value = "HEAD"), cancellable = true)
    private void removeScoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (!QaltilsConfig.INSTANCE.getSidebar() || mc.currentScreen instanceof SidebarGui) {
            ci.cancel();
        } else {
            GlStateManager.pushMatrix();
            float iHaveNoIdeaWhatToNameThisFloat = QaltilsConfig.INSTANCE.getSidebarScale() - 1.0f;
            GlStateManager.translate(-QaltilsConfig.INSTANCE.getSidebarX() * iHaveNoIdeaWhatToNameThisFloat, -QaltilsConfig.INSTANCE.getSidebarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
            GlStateManager.scale(QaltilsConfig.INSTANCE.getSidebarScale(), QaltilsConfig.INSTANCE.getSidebarScale(), 1.0F);
        }
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
    private ArrayList<Score> compactSidebar(Iterable<? extends Score> elements) {
        if (QaltilsConfig.INSTANCE.getCompactSidebar()) {
            ArrayList<Score> newElement = new ArrayList<>();
            for (Score input : elements) {
                if (!ScorePlayerTeam.formatPlayerName(input.getScoreScoreboard().getPlayersTeam(input.getPlayerName()), input.getPlayerName()).replaceAll("[\\ud83c\\udf00-\\ud83d\\ude4f]|[\\ud83d\\ude80-\\ud83d\\udeff]", "").trim()
                        .isEmpty()) {
                    newElement.add(input);
                }
            }
            return newElement;
        } else {
            return Lists.newArrayList(elements);
        }
    }

    @ModifyArg(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"), index = 0)
    private int max(int i, int width) {
        this.i = Math.max(i, width);
        return i;
    }

    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 2)
    private int modifyHeight(int x) {
        return (QaltilsConfig.INSTANCE.getSidebarPosition() ? QaltilsConfig.INSTANCE.getSidebarY() : x);
    }

    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 4)
    private int modifyWidth(int x) {
        return (QaltilsConfig.INSTANCE.getSidebarPosition() ? QaltilsConfig.INSTANCE.getSidebarX() - i : x);
    }

    @ModifyVariable(method = "renderScoreboard", at = @At("STORE"), ordinal = 7)
    private int modifyWidth2(int x) {
        return (QaltilsConfig.INSTANCE.getSidebarPosition() ? QaltilsConfig.INSTANCE.getSidebarX() : x);
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    private int handleDrawString(FontRenderer fontRenderer, String text, int x, int y, int color, ScoreObjective objective, ScaledResolution scaledRes) {
        if (QaltilsConfig.INSTANCE.getSidebarScorePoints() || (QaltilsConfig.INSTANCE.getSidebarPosition() ? (x != QaltilsConfig.INSTANCE.getSidebarX() - fontRenderer.getStringWidth(text)) : (x != scaledRes.getScaledWidth() - 1 - fontRenderer.getStringWidth(text)))) {
            return fontRenderer.drawString(text, x, y, color, QaltilsConfig.INSTANCE.getSidebarTextShadow());
        } else {
            return 0;
        }
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawRect(IIIII)V"))
    private void handleBackground(int left, int top, int right, int bottom, int color) {
        if (QaltilsConfig.INSTANCE.getSidebarBackground()) {
            Gui.drawRect(left, top, right, bottom, QaltilsConfig.INSTANCE.getSidebarBackgroundColor().getRGB());
        }
    }

    @Inject(method = "renderScoreboard", at = @At("TAIL"))
    private void popMatrix(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo ci) {
        if (QaltilsConfig.INSTANCE.getSidebar()) {
            GlStateManager.popMatrix();
        }
    }

    private void checkFirstTime() {
        if (QaltilsConfig.INSTANCE.getFirstLaunchBossbar()) {
            QaltilsConfig.INSTANCE.setFirstLaunchBossbar(false);
            QaltilsConfig.INSTANCE.setBossBarX(new ScaledResolution(mc).getScaledWidth() / 2);
            QaltilsConfig.INSTANCE.setBossBarY(12);
            QaltilsConfig.INSTANCE.markDirty();
            QaltilsConfig.INSTANCE.writeData();
        }
    }

}
