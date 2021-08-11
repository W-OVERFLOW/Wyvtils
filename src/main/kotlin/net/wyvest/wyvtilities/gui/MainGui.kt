/*
 * Wyvtilities - Utilities for Hypixel 1.8.9.
 * Copyright (C) 2021 Wyvtilities
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

package net.wyvest.wyvtilities.gui

import gg.essential.api.EssentialAPI
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.config.WyvtilsConfig
import xyz.matthewtgm.requisite.gui.GuiTransButton


class MainGui : GuiScreen() {

    override fun initGui() {
        buttonList.add(GuiTransButton(1, width / 2 - fontRendererObj.getStringWidth("Config Editor") / 2 - 5, height / 2 - 20, fontRendererObj.getStringWidth("Config Editor") + 5, 20, "Config Editor"))
        buttonList.add(GuiTransButton(0, width / 2 - fontRendererObj.getStringWidth("Close") / 2 - 5, height / 2 + 20, fontRendererObj.getStringWidth("Close") + 5, 20, "Close"))
        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(null)
            1 -> EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        GlStateManager.pushMatrix()
        val scale = 3
        GlStateManager.scale(scale.toFloat(), scale.toFloat(), 0f)
        drawCenteredString(
            fontRendererObj,
            EnumChatFormatting.DARK_PURPLE.toString() + "Wyvtilities",
            width / 2 / scale,
            5 / scale + 10,
            -1
        )
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
}