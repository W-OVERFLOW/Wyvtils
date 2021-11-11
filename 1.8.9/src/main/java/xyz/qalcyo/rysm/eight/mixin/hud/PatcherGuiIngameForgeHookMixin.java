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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.requisite.Requisite;
import xyz.qalcyo.rysm.core.config.RysmConfig;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.hooks.GuiIngameForgeHook")
public class PatcherGuiIngameForgeHookMixin {

    @SuppressWarnings({"UnresolvedMixinReference", "DefaultAnnotationParam"})
    @Inject(remap = false, method = "drawActionbarText", at = @At(remap = true, value = "HEAD"), cancellable = true)
    private static void onActionBarTextDrawn(String recordPlaying, int color, CallbackInfo ci) {
        if (recordPlaying == null || recordPlaying.trim().isEmpty()) ci.cancel();
        if (!RysmConfig.INSTANCE.getActionBarCustomization()) return;
        int newX;
        int newY;
        int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(recordPlaying);
        if (RysmConfig.INSTANCE.getActionBarPosition()) {
            newX = RysmConfig.INSTANCE.getActionBarX();
            newY = RysmConfig.INSTANCE.getActionBarY();
        } else {
            newX = -width >> 1;
            newY = -4;
        }
        if (RysmConfig.INSTANCE.getActionBarBackground()) {
            if (RysmConfig.INSTANCE.getActionBarBackgroundColor().getAlpha() != 0) {
                if (RysmConfig.INSTANCE.getActionBarRound()) {
                    Requisite.getInstance().getRenderHelper().drawRoundedRect(newX - RysmConfig.INSTANCE.getActionBarPadding(), newY - RysmConfig.INSTANCE.getActionBarPadding(), width + RysmConfig.INSTANCE.getActionBarPadding(), Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + RysmConfig.INSTANCE.getActionBarPadding(), RysmConfig.INSTANCE.getActionBarRoundRadius(), RysmConfig.INSTANCE.getActionBarBackgroundColor().getRGB());
                } else {
                    Gui.drawRect(
                            newX - RysmConfig.INSTANCE.getActionBarPadding(),
                            newY - RysmConfig.INSTANCE.getActionBarPadding(),
                            width + newX + RysmConfig.INSTANCE.getActionBarPadding(),
                            Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + newY + RysmConfig.INSTANCE.getActionBarPadding(),
                            RysmConfig.INSTANCE.getActionBarBackgroundColor().getRGB()
                    );
                }
            }
        }
        Minecraft.getMinecraft().fontRendererObj.drawString(
                recordPlaying,
                newX, newY,
                color, RysmConfig.INSTANCE.getActionBarShadow()
        );
        ci.cancel();
    }

}
