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

package xyz.qalcyo.rysm.mixin;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.config.RysmConfig;

//Original code from Terbium by Deftu
@Mixin(RenderEntityItem.class)
public abstract class RenderEntityItemMixin extends Render<EntityItem> {

    protected RenderEntityItemMixin(RenderManager renderManager) {
        super(renderManager);
    }

    @Shadow
    protected abstract int func_177078_a(ItemStack stack);

    @Inject(method = "func_177077_a", at = @At("HEAD"), cancellable = true)
    protected void onRender(EntityItem p_177077_1_, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_, CallbackInfoReturnable<Integer> cir) {
        if (RysmConfig.INSTANCE.getItemPhysics()) {
            ItemStack itemstack = p_177077_1_.getEntityItem();
            Item item = itemstack.getItem();
            if (item == null)
                cir.setReturnValue(0);
            boolean flag = p_177077_9_.isGui3d();
            int i = this.func_177078_a(itemstack);
            float f1;
            f1 = -0.125f;
            if (!flag)
                f1 = -0.175f;
            float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float) p_177077_2_, (float) p_177077_4_ + f1 + 0.25F * f2, (float) p_177077_6_);
            if (!flag)
                if (p_177077_1_.onGround)
                    GlStateManager.rotate(180, 0.0f, 1.0f, 1.0f);
            float speed = 10;
            if (!p_177077_1_.onGround) {
                float rotAmount = ((float) p_177077_1_.getAge() * speed) % 360;
                GlStateManager.rotate(rotAmount, 1f, 0f, 1f);
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            cir.setReturnValue(i);
        }
    }

}