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
import net.minecraft.client.util.math.MatrixStack;
import net.wyvest.wyvtils.core.config.WyvtilsConfig;
import net.wyvest.wyvtils.seventeen.Wyvtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackListWidget.ResourcePackEntry.class)
public class ResourcePackEntryMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancelRender(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world == null) return;
        if (WyvtilsConfig.INSTANCE.getTransparentPackGUI()) {
            if ((Wyvtils.INSTANCE.getPackY() != null && Wyvtils.INSTANCE.getPackY() > y + entryHeight) || (Wyvtils.INSTANCE.getPackBottom() != null && Wyvtils.INSTANCE.getPackBottom() < y)) {
                ci.cancel();
            }
        }
    }
}
