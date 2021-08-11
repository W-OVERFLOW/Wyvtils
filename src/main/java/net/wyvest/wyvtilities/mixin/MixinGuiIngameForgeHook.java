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
import net.wyvest.wyvtilities.config.WyvtilsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.hooks.GuiIngameForgeHook")
public class MixinGuiIngameForgeHook {

    @SuppressWarnings({"UnresolvedMixinReference", "DefaultAnnotationParam"})
    @Inject(remap = false, method = "drawActionbarText", at = @At(remap = true, value = "HEAD"), cancellable = true)
    private static void onActionBarTextDrawn(String recordPlaying, int color, CallbackInfo ci) {
        if (!WyvtilsConfig.INSTANCE.getActionBarCustomization()) return;
        int newX;
        int newY;
        if (WyvtilsConfig.INSTANCE.getActionBarPosition()) {
            newX = WyvtilsConfig.INSTANCE.getActionBarX();
            newY = WyvtilsConfig.INSTANCE.getActionBarY();
        } else {
            newX = -Minecraft.getMinecraft().fontRendererObj.getStringWidth(recordPlaying) >> 1;
            newY = -4;
        }
        Minecraft.getMinecraft().fontRendererObj.drawString(
                recordPlaying,
                newX, newY,
                color, WyvtilsConfig.INSTANCE.getActionBarShadow()
        );
        ci.cancel();
    }

}
