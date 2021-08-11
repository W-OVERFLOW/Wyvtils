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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import net.wyvest.wyvtilities.gui.BossHealthGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    private boolean shouldScale = false;

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    protected void renderBossHealth(CallbackInfo ci) {
        if (Minecraft.getMinecraft().currentScreen instanceof BossHealthGui) {
            ci.cancel();
            return;
        }
        if (WyvtilsConfig.INSTANCE.getBossBarCustomization() && !WyvtilsConfig.INSTANCE.getBossBar()) ci.cancel();

        if (!shouldScale) shouldScale = true;
        if (WyvtilsConfig.INSTANCE.getBossBarCustomization()) GlStateManager.pushMatrix();
    }

    @Inject(method = "renderBossHealth", at = @At("TAIL"), cancellable = true)
    protected void tail(CallbackInfo ci) {
        if (shouldScale) shouldScale = false;
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

    @Inject(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawTexturedModalRect(IIIIII)V"))
    private void applyScale(CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getBossBarCustomization() && shouldScale) {
            if (WyvtilsConfig.INSTANCE.getBossBarBar() || WyvtilsConfig.INSTANCE.getBossBarText()) {
                GlStateManager.scale(WyvtilsConfig.INSTANCE.getBossbarScale(), WyvtilsConfig.INSTANCE.getBossbarScale(), 1F);
                shouldScale = false;
            }
        }
    }

    @Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawTexturedModalRect(IIIIII)V"))
    private void removeBar(GuiIngame guiIngame, int x, int y, int textureX, int textureY, int width, int height) {
        if (WyvtilsConfig.INSTANCE.getBossBarCustomization()) {
            if (WyvtilsConfig.INSTANCE.getBossBarBar()) {
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(WyvtilsConfig.INSTANCE.getBossBarX() - 91, WyvtilsConfig.INSTANCE.getBossBarY(), textureX, textureY, width, height);
            }
        } else {
            Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
        }
    }

    private void checkFirstTime() {
        if (WyvtilsConfig.INSTANCE.getFirstLaunchBossbar()) {
            WyvtilsConfig.INSTANCE.setFirstLaunchBossbar(false);
            WyvtilsConfig.INSTANCE.setBossBarX(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / 2);
            WyvtilsConfig.INSTANCE.setBossBarY(12);
            WyvtilsConfig.INSTANCE.markDirty();
            WyvtilsConfig.INSTANCE.writeData();
        }
    }

}
