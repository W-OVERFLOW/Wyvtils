package net.wyvest.wyvtilities.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class WyvtilsGui(private var parent: Screen?) : Screen(Text.of("Wyvtilities")) {

    private val bossBar = ExampleBossBar()

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        val x = client!!.window.scaledWidth
        val y = 12

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, Identifier("textures/gui/bars.png"))
        this.drawTexture(matrices, x / 2 - 91, y, 0, bossBar.color.ordinal * 5 * 2, 182, 5)
        this.drawTexture(matrices, x / 2 - 91, y, 0, 80 + (bossBar.style.ordinal - 1) * 5 * 2, 182, 5)

        val text = bossBar.name
        val n = x / 2 - client!!.textRenderer.getWidth(text as StringVisitable) / 2
        val o = y - 9
        client!!.textRenderer.drawWithShadow(matrices, text, n.toFloat(), o.toFloat(), 16777215)

    }

    override fun onClose() {
        MinecraftClient.getInstance().setScreen(parent)
        parent = null
    }
}