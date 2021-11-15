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

import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.rysm.core.config.RysmConfig;
import xyz.qalcyo.rysm.seventeen.hooks.PackScreenHookKt;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Mixin(PackScreen.class)
public abstract class PackScreenMixin {

    @Shadow protected abstract void updatePackList(PackListWidget widget, Stream<ResourcePackOrganizer.Pack> packs);

    @Shadow private PackListWidget availablePackList;

    @Shadow @Final private ResourcePackOrganizer organizer;

    @Shadow private long refreshTimeout;

    @Shadow @Final private Map<String, Identifier> iconTextures;

    @Inject(method = "init", at = @At("HEAD"))
    private void setTextField(CallbackInfo ci) {
        PackScreenHookKt.setupTextField();
        ((PackScreen) (Object) this).addSelectableChild(PackScreenHookKt.getTextField());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        Objects.requireNonNull(PackScreenHookKt.getTextField()).tick();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void handleTextField(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (PackScreenHookKt.getHasChanged()) {
            updatePackList(availablePackList, PackScreenHookKt.filter(organizer.getDisabledPacks()));
            PackScreenHookKt.setHasChanged(false);
            refreshTimeout = 0L;
            iconTextures.clear();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/pack/PackScreen;renderBackgroundTexture(I)V"))
    private void redirectBackground(PackScreen instance, int i, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (RysmConfig.INSTANCE.getTransparentPackGUI()) {
            instance.renderBackground(matrices);
        } else {
            instance.renderBackgroundTexture(i);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTextField(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!Objects.requireNonNull(PackScreenHookKt.getTextField()).isVisible()) {
            Objects.requireNonNull(PackScreenHookKt.getTextField()).setVisible(true);
        }
        Objects.requireNonNull(PackScreenHookKt.getTextField()).render(matrices, mouseX, mouseY, delta);
    }
}
