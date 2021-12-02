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

package net.wyvest.wyvtils.eight.mixin.gui;

import net.minecraft.util.IChatComponent;
import net.wyvest.wyvtils.eight.hooks.SkytilsChatTabsHookKt;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin handles compatibility with the MessageRenderEvent and
 * another mod called Skytils, a skyblock utility mod.
 */
@Pseudo
@Mixin(targets = "skytils.skytilsmod.features.impl.handlers.ChatTabs")
public class SkytilsChatTabsMixin {

    /**
     * Sends the MessageRenderEvent and makes the shouldAllow method return whether the event
     * was cancelled or not.
     */
    @Dynamic("Skittles")
    @Inject(method = "shouldAllow", at = @At("HEAD"), cancellable = true)
    private void handleWyvtilsChatEvent(IChatComponent chatComponent, CallbackInfoReturnable<Boolean> booleanCallbackInfoReturnable) {
        SkytilsChatTabsHookKt.handleWyvtilsChatEvent(chatComponent, booleanCallbackInfoReturnable);
    }
}
