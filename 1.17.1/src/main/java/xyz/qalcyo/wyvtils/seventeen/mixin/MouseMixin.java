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

package xyz.qalcyo.wyvtils.seventeen.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.qalcyo.wyvtils.core.config.WyvtilsConfig;

@Mixin(Mouse.class)
public class MouseMixin {
    @ModifyArg(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"), index = 0)
    private double reverseScroll(double direction) {
        if (WyvtilsConfig.INSTANCE.getReverseScrolling()) {
            if (direction == 0) {
                return direction;
            } else {
                return direction * -1;
            }
        } else {
            return direction;
        }
    }
}
