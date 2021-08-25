package net.wyvest.wyvtilities.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.hud.BackgroundHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarX
import net.wyvest.wyvtilities.config.WyvtilsConfig.actionBarY
import java.awt.Color
import java.util.*

class ActionBarGui(private var parent: Screen?) : Screen(Text.of("Wyvtilities")) {

    private var prevX = 0
    private var prevY = 0
    private var actionBarDragging = false

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        updatePos(mouseX, mouseY)
        super.render(matrices, mouseX, mouseY, delta)
        client!!.profiler.push("overlayGuiMessage")
        matrices.push()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        this.drawTextBackground(matrices, textRenderer)
        textRenderer.draw(
            matrices,
            "Wyvtilities Action Bar",
            actionBarX.toFloat(),
            actionBarY.toFloat(),
            Color.WHITE.rgb
        )
        RenderSystem.disableBlend()
        matrices.pop()

        client!!.profiler.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        prevX = mouseX.toInt()
        prevY = mouseY.toInt()
        if (mouseButton == 0) {
            actionBarDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            InputUtil.GLFW_KEY_UP -> actionBarY -= 5
            InputUtil.GLFW_KEY_DOWN -> actionBarY += 5
            InputUtil.GLFW_KEY_LEFT -> actionBarX -= 5
            InputUtil.GLFW_KEY_RIGHT -> actionBarX += 5
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun updatePos(x: Int, y: Int) {
        if (actionBarDragging) {
            actionBarX = prevX
            actionBarY = prevY
        }
        prevX = x
        prevY = y
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, state: Int): Boolean {
        actionBarDragging = false
        return super.mouseReleased(mouseX, mouseY, state)
    }


    override fun onClose() {
        WyvtilsConfig.markAndWrite()
        MinecraftClient.getInstance().setScreen(parent)
        parent = null
    }

    private fun drawTextBackground(
        matrices: MatrixStack,
        textRenderer: TextRenderer
    ) {
        val i = client!!.options.getTextBackgroundColor(0.0f)
        if (i != 0) {
            val j = 2
            val var10001 = j - 2
            val var10002 = -6
            val var10003 = j - 2
            Objects.requireNonNull(textRenderer)
            fill(
                matrices,
                var10001,
                var10002,
                var10003,
                7,
                BackgroundHelper.ColorMixer.mixColor(i, Color.WHITE.rgb)
            )
        }
    }
}