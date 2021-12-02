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

package net.wyvest.wyvtils.eight.hooks

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.resources.model.IBakedModel
import net.minecraft.entity.item.EntityItem
import net.wyvest.wyvtils.core.config.WyvtilsConfig.itemPhysics
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

fun onRender(
    p_177077_1_: EntityItem,
    p_177077_2_: Double,
    p_177077_4_: Double,
    p_177077_6_: Double,
    i: Int,
    p_177077_9_: IBakedModel,
    cir: CallbackInfoReturnable<Int?>
) {
    if (itemPhysics) {
        val flag = p_177077_9_.isGui3d
        var f1: Float
        f1 = -0.125f
        if (!flag) f1 = -0.175f
        val f2 = p_177077_9_.itemCameraTransforms.getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y
        GlStateManager.translate(
            p_177077_2_.toFloat(), p_177077_4_.toFloat() + f1 + 0.25f * f2,
            p_177077_6_.toFloat()
        )
        if (!flag) if (p_177077_1_.onGround) GlStateManager.rotate(180f, 0.0f, 1.0f, 1.0f)
        val speed = 10f
        if (!p_177077_1_.onGround) {
            val rotAmount = p_177077_1_.age.toFloat() * speed % 360
            GlStateManager.rotate(rotAmount, 1f, 0f, 1f)
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        cir.returnValue = i
    }
}