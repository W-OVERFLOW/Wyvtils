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

package xyz.qalcyo.rysm.seventeen.mixin.gui;

import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qalcyo.rysm.core.config.RysmConfig;

import java.util.List;
import java.util.stream.Stream;

@Mixin(ResourcePackOrganizer.class)
public class ResourcePackOrganizerMixin {
    @Shadow @Final
    List<ResourcePackProfile> disabledPacks;

    @Inject(method = "getDisabledPacks", at = @At("HEAD"), cancellable = true)
    private void filterIncompatiblePacks(CallbackInfoReturnable<Stream<ResourcePackOrganizer.Pack>> cir) {
        if (RysmConfig.INSTANCE.getHideIncompatiblePacks()) {
            List<ResourcePackProfile> newList = disabledPacks;
            newList.removeIf((resourcePackProfile -> !(resourcePackProfile.getCompatibility().isCompatible())));
            cir.setReturnValue(newList.stream().map(pack -> ((ResourcePackOrganizer) (Object) this).new DisabledPack(pack)));
        }
    }
}
