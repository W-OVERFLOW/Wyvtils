package net.wyvest.wyvtilities.gui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.config.WyvtilsConfig.sidebarScale
import net.wyvest.wyvtilities.config.WyvtilsConfig.sidebarX
import net.wyvest.wyvtilities.config.WyvtilsConfig.sidebarY

class SidebarGui(private var parent: Screen?) : Screen(Text.of("Wyvtilities")) {
    private var prevX = 0
    private var prevY = 0
    private var sidebarDragging = false

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        updatePos(mouseX, mouseY)
        super.render(matrices, mouseX, mouseY, delta)
        client!!.profiler.push("sidebarGui")
        matrices!!.push()
        val iHaveNoIdeaWhatToNameThisFloat = sidebarScale - 1.0f
        matrices.translate(
            (-sidebarX * iHaveNoIdeaWhatToNameThisFloat).toDouble(),
            (-sidebarY * iHaveNoIdeaWhatToNameThisFloat).toDouble(),
            0.0
        )
        matrices.scale(sidebarScale, sidebarScale, 1.0f)
        val x = sidebarX - textRenderer.getWidth("Wyvtilities") - 3
        val y = sidebarY
        var p = 0
        val list = listOf("ok", "Wyvest", "Wyvtilities")
        for (s in list) {
            ++p
            val t: Int = y - p * 9
            val var10001: Int = x - 2
            if (WyvtilsConfig.sidebarBackground) fill(
                matrices,
                var10001,
                t,
                sidebarX - 1,
                t + 9,
                WyvtilsConfig.sidebarBackgroundColor.rgb
            )
            if (WyvtilsConfig.sidebarTextShadow) {
                textRenderer.drawWithShadow(matrices, s, x.toFloat(), t.toFloat(), -1)
            } else {
                textRenderer.draw(matrices, s, x.toFloat(), t.toFloat(), -1)
            }
            if (p != 3 && WyvtilsConfig.sidebarScorePoints) {
                if (WyvtilsConfig.sidebarTextShadow) {
                    textRenderer.drawWithShadow(
                        matrices,
                        p.toString(),
                        (sidebarX - 1 - textRenderer.getWidth(p.toString())).toFloat(),
                        t.toFloat(),
                        -1
                    )
                } else {
                    textRenderer.draw(
                        matrices,
                        p.toString(),
                        (sidebarX - 1 - textRenderer.getWidth(p.toString())).toFloat(),
                        t.toFloat(),
                        -1
                    )
                }
            }
        }
        matrices.pop()

        client!!.profiler.pop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        prevX = mouseX.toInt()
        prevY = mouseY.toInt()
        if (mouseButton == 0) {
            sidebarDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            InputUtil.GLFW_KEY_UP -> sidebarY -= 5
            InputUtil.GLFW_KEY_DOWN -> sidebarY += 5
            InputUtil.GLFW_KEY_LEFT -> sidebarX -= 5
            InputUtil.GLFW_KEY_RIGHT -> sidebarX += 5
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun updatePos(x: Int, y: Int) {
        if (sidebarDragging) {
            sidebarX = prevX
            sidebarY = prevY
        }
        prevX = x
        prevY = y
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, state: Int): Boolean {
        sidebarDragging = false
        return super.mouseReleased(mouseX, mouseY, state)
    }


    override fun onClose() {
        WyvtilsConfig.markAndWrite()
        MinecraftClient.getInstance().setScreen(parent)
        parent = null
    }
}