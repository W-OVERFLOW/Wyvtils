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

package net.wyvest.wyvtils.seventeen.mixin.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.wyvest.wyvtils.core.config.WyvtilsConfig;
import net.wyvest.wyvtils.seventeen.hooks.PackScreenHookKt;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(PackScreen.class)
public class PackScreenMixin1 {
    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && ((PackScreen) (Object) this).shouldCloseOnEsc()) {
            ((PackScreen) (Object) this).onClose();
            return true;
        } else if (keyCode == 258) {
            boolean bl = !Screen.hasShiftDown();
            if (!((PackScreen) (Object) this).changeFocus(bl)) {
                ((PackScreen) (Object) this).changeFocus(bl);
            }

            return false;
        } else {
            return WyvtilsConfig.INSTANCE.getPackSearchBox() && Objects.requireNonNull(PackScreenHookKt.getTextField()).keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public boolean method_25400(char char_1, int int_1) {
        return WyvtilsConfig.INSTANCE.getPackSearchBox() && Objects.requireNonNull(PackScreenHookKt.getTextField()).charTyped(char_1, int_1);
    }
}
