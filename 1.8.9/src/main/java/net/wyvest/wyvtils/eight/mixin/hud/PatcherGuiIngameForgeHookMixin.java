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

package net.wyvest.wyvtils.eight.mixin.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.wyvest.wyvtils.core.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.hooks.GuiIngameForgeHook")
public class PatcherGuiIngameForgeHookMixin {

    @Dynamic("patcher")
    @Inject(method = "drawActionbarText", at = @At(value = "HEAD"), cancellable = true)
    private static void onActionBarTextDrawn(String recordPlaying, int color, CallbackInfo ci) {
        if (recordPlaying == null || recordPlaying.trim().isEmpty()) ci.cancel();
        if (!WyvtilsConfig.INSTANCE.getActionBarCustomization()) return;
        int newX;
        int newY;
        int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(recordPlaying);
        if (WyvtilsConfig.INSTANCE.getActionBarPosition()) {
            newX = WyvtilsConfig.INSTANCE.getActionBarX();
            newY = WyvtilsConfig.INSTANCE.getActionBarY();
        } else {
            newX = -width >> 1;
            newY = -4;
        }
        if (WyvtilsConfig.INSTANCE.getActionBarBackground()) {
            if (WyvtilsConfig.INSTANCE.getActionBarBackgroundColor().getAlpha() != 0) {
                Gui.drawRect(
                        newX - WyvtilsConfig.INSTANCE.getActionBarPadding(),
                        newY - WyvtilsConfig.INSTANCE.getActionBarPadding(),
                        width + newX + WyvtilsConfig.INSTANCE.getActionBarPadding(),
                        Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + newY + WyvtilsConfig.INSTANCE.getActionBarPadding(),
                        WyvtilsConfig.INSTANCE.getActionBarBackgroundColor().getRGB()
                );
            }
        }
        Minecraft.getMinecraft().fontRendererObj.drawString(
                recordPlaying,
                newX, newY,
                color, WyvtilsConfig.INSTANCE.getActionBarShadow()
        );
        ci.cancel();
    }

}
