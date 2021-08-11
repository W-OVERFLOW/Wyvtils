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
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.config.WyvtilsConfig
import xyz.matthewtgm.requisite.gui.GuiTransButton


class MainGui : GuiScreen() {

    override fun initGui() {

        buttonList.add(object : GuiTransButton(0, width / 2 - 50, height / 2 - 10, 100, 20, "Config Editor") {
            override fun mousePressed(mc: Minecraft, mouseX: Int, mouseY: Int): Boolean {
                if (super.mousePressed(mc, mouseX, mouseY)) EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
                return false
            }
        })
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        GlStateManager.pushMatrix()
        GlStateManager.scale(2f, 2f, 0f)
        drawCenteredString(
            fontRendererObj,
            EnumChatFormatting.DARK_PURPLE.toString() + "Wyvtilities",
            width / 4,
            3,
            -1
        )
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
}