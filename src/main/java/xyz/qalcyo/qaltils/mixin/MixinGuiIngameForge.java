/*
 * Qaltils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Qaltils
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

package xyz.qalcyo.qaltils.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import xyz.qalcyo.qaltils.config.QaltilsConfig;
import xyz.qalcyo.qaltils.gui.ActionBarGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(value = GuiIngameForge.class, remap = false)
public class MixinGuiIngameForge {

    @Inject(method = "renderRecordOverlay", at = @At("HEAD"), cancellable = true)
    private void removeActionBar(int width, int height, float partialTicks, CallbackInfo ci) {
        if ((QaltilsConfig.INSTANCE.getActionBarCustomization() && !QaltilsConfig.INSTANCE.getActionBar()) || Minecraft.getMinecraft().currentScreen instanceof ActionBarGui) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderRecordOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"), remap = true)
    private void removeTranslation(float x, float y, float z) {
        if ((!QaltilsConfig.INSTANCE.getActionBarPosition() && QaltilsConfig.INSTANCE.getActionBarCustomization()) || !QaltilsConfig.INSTANCE.getActionBarCustomization()) {
            GlStateManager.translate(x, y, z);

        }
    }

    @Redirect(method = "renderRecordOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"), remap = true)
    private int modifyDrawString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        if (!QaltilsConfig.INSTANCE.getActionBarCustomization()) {
            return fontRenderer.drawString(text, x, y, color);
        } else {
            int newX;
            int newY;
            if (QaltilsConfig.INSTANCE.getActionBarPosition()) {
                newX = QaltilsConfig.INSTANCE.getActionBarX();
                newY = QaltilsConfig.INSTANCE.getActionBarY();
            } else {
                newX = x;
                newY = y;
            }
            return fontRenderer.drawString(
                    text,
                    newX,
                    newY,
                    color,
                    QaltilsConfig.INSTANCE.getActionBarShadow()
            );
        }
    }

    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void removeTitle(int width, int height, float partialTicks, CallbackInfo ci) {
        if (!QaltilsConfig.INSTANCE.getTitle()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"), remap = true)
    private void modifyTitleScale1(float x, float y, float z) {
        if (QaltilsConfig.INSTANCE.getTitleScale() == 1.0F && QaltilsConfig.INSTANCE.getSubtitleScale() == 1.0F) {
            GlStateManager.scale(x, y, z);
        } else {
            if (x == 4.0F) {
                //Title
                GlStateManager.scale(x * QaltilsConfig.INSTANCE.getTitleScale(), y * QaltilsConfig.INSTANCE.getTitleScale(), z * QaltilsConfig.INSTANCE.getTitleScale());
                return;
            }
            if (x == 2.0F) {
                //Subtitle
                GlStateManager.scale(x * QaltilsConfig.INSTANCE.getSubtitleScale(), y * QaltilsConfig.INSTANCE.getSubtitleScale(), z * QaltilsConfig.INSTANCE.getSubtitleScale());
            }
        }
    }

    @ModifyArg(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"), index = 4)
    private boolean setShadow() {
        return QaltilsConfig.INSTANCE.getTitleShadow();
    }

}
