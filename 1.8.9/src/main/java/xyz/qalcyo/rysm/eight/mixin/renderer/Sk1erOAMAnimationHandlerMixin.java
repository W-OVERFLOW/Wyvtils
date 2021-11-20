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

package xyz.qalcyo.rysm.eight.mixin.renderer;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.eight.hooks.Sk1erOAMAnimationHandlerHookKt;

/**
 * This mixin handles compatibility with the left hand
 * and swap bow feature in Rysm with another mod called
 * Sk1er Old Animations, which is a mod which backports
 * 1.7 animations into 1.8.9.
 */

@Pseudo
@Mixin(targets = "club.sk1er.oldanimations.AnimationHandler")
public class Sk1erOAMAnimationHandlerMixin {

    /**
     * Resets internal variables to their original value.
     */
    @Dynamic("Injecting into overwritten item render code from Sk1er Old Animations mod")
    @Inject(method = "renderItemInFirstPerson", at = @At("HEAD"))
    private void resetInternalVariables(ItemRenderer renderer, ItemStack stack, float equipProgress, float partialTicks, CallbackInfoReturnable<Boolean> ci) {
        Sk1erOAMAnimationHandlerHookKt.setAlready(false);
    }

    /**
     * Rotates the hand.
     */
    @Dynamic("Injecting into overwritten item render code from Sk1er Old Animations mod")
    @Inject(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V"))
    private void onItemInFirstPersonRendered(ItemRenderer renderer, ItemStack stack, float equipProgress, float partialTicks, CallbackInfoReturnable<Boolean> ci) {
        Sk1erOAMAnimationHandlerHookKt.onSk1erItemInFirstPersonRendered(stack);
    }
}
