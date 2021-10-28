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

package xyz.qalcyo.rysm.eight.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.core.RysmCore;
import xyz.qalcyo.rysm.core.listener.events.StringRenderEvent;

@Mixin(FontRenderer.class)
public class FontRendererMixin {
    private StringRenderEvent stringRenderEvent;

    @Inject(method = "renderString", at = @At("HEAD"))
    private void invokeStringDrawnEvent(String text, float x, float y, int colour, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        stringRenderEvent = new StringRenderEvent(text == null ? "" : text, Minecraft.getMinecraft().thePlayer == null ? null : Minecraft.getMinecraft().thePlayer.getName());
        RysmCore.INSTANCE.getEventBus().post(stringRenderEvent);
    }

    @ModifyVariable(method = "renderString", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String onStringRendered_modifyText(String original) {
        return stringRenderEvent.getString();
    }
}