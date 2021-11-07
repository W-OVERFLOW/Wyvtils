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

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.eight.hooks.RenderEntityItemHookKt;

/**
 * This mixin sends and handles the item physics feature which is a
 * version-independent feature.
 * Original code from Terbium by Deftu with permission.
 */
@Mixin(RenderEntityItem.class)
public abstract class RenderEntityItemMixin extends Render<EntityItem> {

    protected RenderEntityItemMixin(RenderManager renderManager) {
        super(renderManager);
    }

    @Shadow
    protected abstract int func_177078_a(ItemStack stack);

    /**
     * Handles item physics.
     */
    @Inject(method = "func_177077_a", at = @At("HEAD"), cancellable = true)
    protected void onRender(EntityItem p_177077_1_, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_, CallbackInfoReturnable<Integer> cir) {
        if (p_177077_1_.getEntityItem().getItem() == null) {
            cir.setReturnValue(0);
            return;
        }
        RenderEntityItemHookKt.onRender(p_177077_1_, p_177077_2_, p_177077_4_, p_177077_6_, func_177078_a(p_177077_1_.getEntityItem()), p_177077_9_, cir);
    }

}