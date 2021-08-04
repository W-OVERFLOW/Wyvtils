package net.wyvest.wyvtilities.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarX
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarY
import net.wyvest.wyvtilities.mixin.AccessorGuiIngame
import xyz.matthewtgm.requisite.util.GuiHelper
import java.awt.Color
import java.io.IOException

object ActionBarGui : GuiScreen() {

    private var dragging = false
    private var prevX = 0
    private var prevY = 0

    override fun initGui() {
        buttonList.add(GuiButton(0, width / 2 - 50, height - 20, 100, 20, "Close"))
        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> GuiHelper.open(WyvtilsConfig.gui())
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        updatePos(mouseX, mouseY)
        val text = if ((mc.ingameGUI as AccessorGuiIngame).recordIsPlaying) {
            (mc.ingameGUI as AccessorGuiIngame).recordPlaying
        } else {
            "Wyvtilities Action Bar"
        }
        GlStateManager.pushMatrix()
        mc.fontRendererObj.drawString(
            text,
            actionBarX - mc.fontRendererObj.getStringWidth(text) / 2,
            actionBarY,
            Color.WHITE.rgb
        )
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        prevX = mouseX - mc.fontRendererObj.getStringWidth("Wyvtilities Action Bar") / 2
        prevY = mouseY
        if (mouseButton == 0) {
            dragging = true
        }
    }

    private fun updatePos(x: Int, y: Int) {
        if (dragging) {
            actionBarX = prevX
            actionBarY = prevY
        }
        prevX = x - mc.fontRendererObj.getStringWidth("Wyvtilities Action Bar") / 2
        prevY = y
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        dragging = false
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun onGuiClosed() {
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
        super.onGuiClosed()
    }

}