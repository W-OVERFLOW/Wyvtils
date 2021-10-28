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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.core.config.RysmConfig;

/**
 * This mixin handles compatibility with the left hand
 * and swap bow feature in Rysm with another mod called
 * Sk1er Old Animations, which is a mod which backports
 * 1.7 animations into 1.8.9.
 */
@SuppressWarnings("UnresolvedMixinReference")
@Pseudo
@Mixin(targets = "club.sk1er.oldanimations.AnimationHandler")
public class Sk1erOAMAnimationHandlerMixin {
    private boolean already = false;

    /**
     * Resets internal variables to their original value.
     */
    @Inject(method = "renderItemInFirstPerson", at = @At("HEAD"))
    private void resetInternalVariables(ItemRenderer renderer, ItemStack stack, float equipProgress, float partialTicks, CallbackInfoReturnable<Boolean> ci) {
        already = false;
    }

    /**
     * Rotates the hand.
     */
    @Inject(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V"), remap = true)
    protected void onItemInFirstPersonRendered(ItemRenderer renderer, ItemStack stack, float equipProgress, float partialTicks, CallbackInfoReturnable<Boolean> ci) {
        if (RysmConfig.INSTANCE.getSwapBow() && stack != null && stack.getItemUseAction() == EnumAction.BOW) {
            if (!RysmConfig.INSTANCE.getLeftHand() && !already) {
                GL11.glScaled(-1.0d, 1.0d, 1.0d);
                GlStateManager.disableCull();
                already = true;
            }
        } else {
            if (RysmConfig.INSTANCE.getLeftHand() && !already) {
                GL11.glScaled(-1.0d, 1.0d, 1.0d);
                GlStateManager.disableCull();
                already = true;
            }
        }
    }
}
