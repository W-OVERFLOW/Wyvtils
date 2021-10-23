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
import xyz.qalcyo.wyvtils.core.WyvtilsCore;
import xyz.qalcyo.wyvtils.core.listener.events.MouseScrollEvent;

@Mixin(Mouse.class)
public class MouseMixin {
    @ModifyArg(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"), index = 0)
    private double invokeScrollEvent(double direction) {
        MouseScrollEvent scrollEvent = new MouseScrollEvent(direction, false);
        WyvtilsCore.INSTANCE.getEventBus().post(scrollEvent);
        if (scrollEvent.getCancelled()) return 0D;
        return scrollEvent.getScroll();
    }
}
