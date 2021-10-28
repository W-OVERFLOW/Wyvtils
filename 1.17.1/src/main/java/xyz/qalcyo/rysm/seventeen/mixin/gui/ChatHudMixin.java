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

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.qalcyo.rysm.core.RysmCore;
import xyz.qalcyo.rysm.core.listener.events.MessageRenderEvent;

import java.util.Collections;
import java.util.List;

/**
 * This mixin handles the ChatRefreshEvent which is used
 * in the core submodule to handle messages in chat.
 */
@Mixin(ChatHud.class)
public class ChatHudMixin {

    /**
     * Sends and handles a MessageRenderEvent.
     */
    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    private List<OrderedText> invokeMessageEvent(StringVisitable message, int width, TextRenderer textRenderer) {
        MessageRenderEvent event = new MessageRenderEvent(message.getString(), false);
        RysmCore.INSTANCE.getEventBus().post(event);
        return event.getCancelled() ? Collections.emptyList() : ChatMessages.breakRenderedChatMessageLines(message, width, textRenderer);
    }
}
