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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.wyvest.wyvtils.core.WyvtilsCore;
import net.wyvest.wyvtils.core.listener.events.StringRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin sends and handles the StringRenderEvent which is used
 * in the core submodule to modify rendered text ingame.
 */
@Mixin(FontRenderer.class)
public class FontRendererMixin {
    private StringRenderEvent stringRenderEvent;

    /**
     * Invokes the StringRenderEvent.
     */
    @Inject(method = "renderString", at = @At("HEAD"))
    private void invokeStringDrawnEvent(String text, float x, float y, int colour, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        stringRenderEvent = new StringRenderEvent(text == null ? "" : text, Minecraft.getMinecraft().thePlayer == null ? null : Minecraft.getMinecraft().thePlayer.getName());
        WyvtilsCore.INSTANCE.getEventBus().post(stringRenderEvent);
    }

    /**
     * Redirects the text to render with the modified text from the invoked
     * event.
     */
    @ModifyVariable(method = "renderString", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String onStringRendered_modifyText(String original) {
        return stringRenderEvent.getString();
    }
}
