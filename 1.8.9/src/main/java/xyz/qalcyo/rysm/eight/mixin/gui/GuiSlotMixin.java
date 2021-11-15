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

package xyz.qalcyo.rysm.eight.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiResourcePackList;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.rysm.core.config.RysmConfig;
import xyz.qalcyo.rysm.eight.Rysm;

@Mixin(GuiSlot.class)
public abstract class GuiSlotMixin {
    @Shadow protected abstract void drawContainerBackground(Tessellator tessellator);

    @Shadow protected abstract void overlayBackground(int startY, int endY, int startAlpha, int endAlpha);

    @Shadow public int top;

    @Shadow public int bottom;

    @Shadow @Final protected Minecraft mc;

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void yeah(int l1, int i, float j, CallbackInfo ci) {
        if (mc.theWorld == null) return;
        if (RysmConfig.INSTANCE.getTransparentPackGUI()) {
            Rysm.INSTANCE.setPackY(top);
            Rysm.INSTANCE.setPackBottom(bottom);
        }
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void yea5h(int l1, int i, float j, CallbackInfo ci) {
        if (mc.theWorld == null) return;
        if (RysmConfig.INSTANCE.getTransparentPackGUI()) {
            Rysm.INSTANCE.setPackY(null);
            Rysm.INSTANCE.setPackBottom(null);
        }
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiSlot;drawContainerBackground(Lnet/minecraft/client/renderer/Tessellator;)V"))
    private void redirectBackground(GuiSlot instance, Tessellator tessellator) {
        if (mc.theWorld == null) {
            drawContainerBackground(tessellator);
            return;
        }
        if (!RysmConfig.INSTANCE.getTransparentPackGUI() || !(instance instanceof GuiResourcePackList)) {
            drawContainerBackground(tessellator);
        }
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiSlot;overlayBackground(IIII)V"))
    private void redirectOverlay(GuiSlot instance, int startY, int endY, int startAlpha, int endAlpha) {
        if (mc.theWorld == null) {
            overlayBackground(startY, endY, startAlpha, endAlpha);
            return;
        }
        if (!RysmConfig.INSTANCE.getTransparentPackGUI() || !(instance instanceof GuiResourcePackList)) {
            overlayBackground(startY, endY, startAlpha, endAlpha);
        }
    }
}
