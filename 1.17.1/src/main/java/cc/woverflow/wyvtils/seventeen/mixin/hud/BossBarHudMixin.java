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

package cc.woverflow.wyvtils.seventeen.mixin.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import cc.woverflow.wyvtils.core.config.WyvtilsConfig;
import cc.woverflow.wyvtils.seventeen.gui.BossHealthGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    @Shadow @Final
    Map<UUID, ClientBossBar> bossBars;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancel(MatrixStack matrices, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof BossHealthGui) {
            ci.cancel();
        } else if (!WyvtilsConfig.INSTANCE.getBossBar()) {
            ci.cancel();
        } else {
            if (WyvtilsConfig.INSTANCE.getFirstLaunchBossbar()) {
                WyvtilsConfig.INSTANCE.setFirstLaunchBossbar(false);
                WyvtilsConfig.INSTANCE.setBossBarX(MinecraftClient.getInstance().getWindow().getScaledWidth());
                WyvtilsConfig.INSTANCE.markDirty();
                WyvtilsConfig.INSTANCE.writeData();
            }
            if (!bossBars.isEmpty()) {
                matrices.push();
                double iHaveNoIdeaWhatToNameThisFloat = (double) WyvtilsConfig.INSTANCE.getBossbarScale() - 1.0f;
                matrices.translate(-WyvtilsConfig.INSTANCE.getBossBarX() * iHaveNoIdeaWhatToNameThisFloat / 2, -WyvtilsConfig.INSTANCE.getBossBarY() * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
                matrices.scale(WyvtilsConfig.INSTANCE.getBossbarScale(), WyvtilsConfig.INSTANCE.getBossbarScale(), 1F);
            }
        }
    }

    @ModifyVariable(method = "render", at = @At("STORE"), index = 2)
    private int getX(int x) {
        return WyvtilsConfig.INSTANCE.getBossBarX();
    }

    @ModifyVariable(method = "render", at = @At("STORE"), index = 3)
    private int getY(int y) {
        return WyvtilsConfig.INSTANCE.getBossBarY();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int modifyText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (WyvtilsConfig.INSTANCE.getBossBarText()) {
            if (WyvtilsConfig.INSTANCE.getBossBarShadow()) {
                return textRenderer.drawWithShadow(matrices, text, x, y, color);
            } else {
                return textRenderer.draw(matrices, text, x, y, color);
            }
        } else {
            return 0;
        }
    }

    @Inject(method = "renderBossBar", at = @At("HEAD"), cancellable = true)
    private void cancelBar(MatrixStack matrices, int x, int y, BossBar bossBar, CallbackInfo ci) {
        if (!WyvtilsConfig.INSTANCE.getBossBarBar()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void pop(MatrixStack matrices, CallbackInfo ci) {
        if (!bossBars.isEmpty()) {
            matrices.pop();
        }
    }

}