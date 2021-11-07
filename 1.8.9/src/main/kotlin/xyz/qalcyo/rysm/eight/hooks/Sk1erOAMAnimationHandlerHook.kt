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

package xyz.qalcyo.rysm.eight.hooks

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import xyz.qalcyo.rysm.core.config.RysmConfig.leftHand
import xyz.qalcyo.rysm.core.config.RysmConfig.swapBow

var already = false


fun onSk1erItemInFirstPersonRendered(
    stack: ItemStack?
) {
    if (swapBow && stack != null && stack.itemUseAction == EnumAction.BOW) {
        if (!leftHand && !already) {
            GL11.glScaled(-1.0, 1.0, 1.0)
            GlStateManager.disableCull()
            already = true
        }
    } else {
        if (leftHand && !already) {
            GL11.glScaled(-1.0, 1.0, 1.0)
            GlStateManager.disableCull()
            already = true
        }
    }
}