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

package net.wyvest.wyvtils.seventeen.mixin.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.wyvest.wyvtils.core.config.WyvtilsConfig;
import net.wyvest.wyvtils.seventeen.Wyvtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin {

    @Shadow protected abstract void renderBackground(MatrixStack matrices);

    @Shadow protected int top;

    @Shadow protected int bottom;

    @Shadow private boolean renderBackground;

    @Shadow private boolean renderHorizontalShadows;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderBackground(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void setBackground(EntryListWidget instance, MatrixStack matrices) {
        if (MinecraftClient.getInstance().world == null) {
            renderBackground(matrices);
            return;
        }
        if (!WyvtilsConfig.INSTANCE.getTransparentPackGUI() || !(instance instanceof PackListWidget)) {
            renderBackground(matrices);
        }
        renderBackground = !(WyvtilsConfig.INSTANCE.getTransparentPackGUI() && instance instanceof PackListWidget);
        renderHorizontalShadows = renderBackground;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void yeah(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world == null) return;
        if (WyvtilsConfig.INSTANCE.getTransparentPackGUI()) {
            Wyvtils.INSTANCE.setPackY(top);
            Wyvtils.INSTANCE.setPackBottom(bottom);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void yea5h(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world == null) return;
        if (WyvtilsConfig.INSTANCE.getTransparentPackGUI()) {
            Wyvtils.INSTANCE.setPackY(null);
            Wyvtils.INSTANCE.setPackBottom(null);
        }
    }
}
