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

package cc.woverflow.wyvtils.eight.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackListEntry;
import cc.woverflow.wyvtils.core.config.WyvtilsConfig;
import cc.woverflow.wyvtils.eight.Wyvtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourcePackListEntry.class)
public class ResourcePackListEntryMixin {

    @Shadow @Final protected Minecraft mc;

    @Inject(method = "drawEntry", at = @At("HEAD"), cancellable = true)
    private void cancel(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, CallbackInfo ci) {
        if (mc.theWorld == null) return;
        if (WyvtilsConfig.INSTANCE.getTransparentPackGUI()) {
            if ((Wyvtils.INSTANCE.getPackY() != null && Wyvtils.INSTANCE.getPackY() > y + slotHeight) || (Wyvtils.INSTANCE.getPackBottom() != null && Wyvtils.INSTANCE.getPackBottom() < y)) {
                ci.cancel();
            }
        }
    }
}
