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

package net.wyvest.wyvtils.eight.mixin;

import net.minecraft.client.Minecraft;
import net.wyvest.wyvtils.core.WyvtilsCore;
import net.wyvest.wyvtils.core.listener.events.MouseScrollEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * This mixin sends and handles the MouseScrollEvent which is used
 * in the core submodule to modify the direction of the scroll wheel.
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    /**
     * Calls a MouseScrollEvent and cancels and changes the direction
     * accordingly.
     */
    @ModifyArg(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;changeCurrentItem(I)V"), index = 0)
    private int invokeScrollEvent(int direction) {
        MouseScrollEvent scrollEvent = new MouseScrollEvent(direction, false);
        WyvtilsCore.INSTANCE.getEventBus().post(scrollEvent);
        if (scrollEvent.getCancelled()) return 0;
        return (int) Math.round(scrollEvent.getScroll());
    }

}