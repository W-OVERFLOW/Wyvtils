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

package xyz.qalcyo.wyvtils.eight.mixin;

import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {
    @Inject(method = "printChatMessage", at = @At("HEAD"), cancellable = true)
    private void cancelLocraw(IChatComponent chatComponent, CallbackInfo ci) {
        if (WyvtilsConfig.INSTANCE.getHideLocraw()) {
            String stripped = EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText());
            if (stripped.startsWith("{") && stripped.contains("server") && stripped.endsWith("}")) {
                ci.cancel();
            }
        }
    }
}