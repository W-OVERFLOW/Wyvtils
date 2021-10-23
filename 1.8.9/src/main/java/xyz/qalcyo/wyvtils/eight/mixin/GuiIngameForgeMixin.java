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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.wyvtils.core.WyvtilsCore;
import xyz.qalcyo.wyvtils.core.listener.events.TitleEvent;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(value = GuiIngameForge.class, remap = false)
public class GuiIngameForgeMixin {
    private TitleEvent titleEvent;
    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void removeTitle(int width, int height, float partialTicks, CallbackInfo ci) {
        titleEvent = new TitleEvent(false, 1.0F, 1.0F, true);
        WyvtilsCore.INSTANCE.getEventBus().post(titleEvent);
        if (titleEvent.getCancelled()) ci.cancel();
    }

    @Redirect(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"), remap = true)
    private void modifyTitleScale1(float x, float y, float z) {
        if (x == 4.0F) {
            //Title
            GlStateManager.scale(x * titleEvent.getTitleScale(), y * titleEvent.getTitleScale(), z * titleEvent.getTitleScale());
            return;
        }
        if (x == 2.0F) {
            //Subtitle
            GlStateManager.scale(x * titleEvent.getSubtitleScale(), y * titleEvent.getSubtitleScale(), z * titleEvent.getSubtitleScale());
        }
    }

    @ModifyArg(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"), index = 4, remap = true)
    private boolean setShadow(boolean shadow) {
        return titleEvent.getShadow();
    }
}
