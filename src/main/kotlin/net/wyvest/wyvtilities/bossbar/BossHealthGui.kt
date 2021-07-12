package net.wyvest.wyvtilities.bossbar

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.config.WyvtilsConfig
import xyz.matthewtgm.tgmlib.util.GuiHelper
import java.io.IOException


object BossHealthGui : GuiScreen() {

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
        BossHealth.renderBossHealth()
        val scale = 1
        GlStateManager.pushMatrix()
        GlStateManager.scale(scale.toFloat(), scale.toFloat(), 0f)
        drawCenteredString(
            fontRendererObj,
            EnumChatFormatting.WHITE.toString() + "(drag hud to edit position!)",
            width / 2 / scale,
            5 / scale + 55,
            -1
        )
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        prevX = mouseX
        this.prevY = mouseY
        if (mouseButton == 0) {
            dragging = true
        }
    }

    private fun updatePos(x: Int, y: Int) {
        if (dragging) {
            WyvtilsConfig.bossBarX = prevX - 10
            WyvtilsConfig.bossBarY = this.prevY - 10
        }
        prevX = x
        this.prevY = y
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