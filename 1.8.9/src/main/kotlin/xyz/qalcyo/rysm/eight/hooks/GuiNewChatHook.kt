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

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiUtilRenderComponents
import net.minecraft.util.IChatComponent
import skytils.skytilsmod.features.impl.handlers.ChatTabs
import xyz.qalcyo.rysm.core.RysmCore
import xyz.qalcyo.rysm.core.listener.events.MessageRenderEvent
import xyz.qalcyo.rysm.eight.Rysm
import java.util.*

fun handleChatSent(p_178908_0_: IChatComponent, p_178908_1_: Int, p_178908_2_: FontRenderer, p_178908_3_: Boolean, p_178908_4_: Boolean): List<IChatComponent> {
    if (Rysm.isSkytils) {
        if (!ChatTabs.shouldAllow(p_178908_0_)) return Collections.emptyList()
    }
    val event = MessageRenderEvent(p_178908_0_.unformattedText, false)
    RysmCore.eventBus.post(event)
    return if (event.cancelled) emptyList() else GuiUtilRenderComponents.splitText(
        p_178908_0_,
        p_178908_1_,
        p_178908_2_,
        p_178908_3_,
        p_178908_4_
    )
}