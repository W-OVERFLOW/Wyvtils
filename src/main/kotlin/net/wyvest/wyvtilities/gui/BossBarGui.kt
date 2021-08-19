package net.wyvest.wyvtilities.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ClientBossBar
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.boss.BossBar
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.config.WyvtilsConfig.bossBarX
import net.wyvest.wyvtilities.config.WyvtilsConfig.bossBarY
import java.util.*

class BossBarGui(private var parent: Screen?) : Screen(Text.of("Wyvtilities")) {

    private val bossBar = ClientBossBar(
        UUID.fromString("cd899a14-de78-4de8-8d31-9d42fff31d7a"),
        Text.of("Wyvtilities"),
        1.0F,
        BossBar.Color.PURPLE,
        BossBar.Style.NOTCHED_20,
        false,
        false,
        false
    ) //cd899a14-de78-4de8-8d31-9d42fff31d7a is the UUID of EssentialBot which should never appear ingame
    private var prevX = 0
    private var prevY = 0
    private var bossBarDragging = false

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        updatePos(mouseX, mouseY)
        super.render(matrices, mouseX, mouseY, delta)

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, Identifier("textures/gui/bars.png"))
        this.drawTexture(matrices, bossBarX - 91, bossBarY, 0, bossBar.color.ordinal * 5 * 2, 182, 5)
        this.drawTexture(matrices, bossBarX - 91, bossBarY, 0, 80 + (bossBar.style.ordinal - 1) * 5 * 2, 182, 5)

        val text = bossBar.name
        client!!.textRenderer.drawWithShadow(
            matrices,
            text,
            (bossBarX - MinecraftClient.getInstance().textRenderer.getWidth("Wyvtilities") / 2).toString().toFloat(),
            bossBarY.toFloat() - 10,
            16777215
        )

    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        prevX = mouseX.toInt()
        prevY = mouseY.toInt()
        if (mouseButton == 0) {
            bossBarDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            InputUtil.GLFW_KEY_UP -> bossBarY -= 5
            InputUtil.GLFW_KEY_DOWN -> bossBarY += 5
            InputUtil.GLFW_KEY_LEFT -> bossBarX -= 5
            InputUtil.GLFW_KEY_RIGHT -> bossBarX += 5
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun updatePos(x: Int, y: Int) {
        if (bossBarDragging) {
            bossBarX = prevX
            bossBarY = prevY
        }
        prevX = x
        prevY = y
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, state: Int): Boolean {
        bossBarDragging = false
        return super.mouseReleased(mouseX, mouseY, state)
    }


    override fun onClose() {
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
        MinecraftClient.getInstance().setScreen(parent)
        parent = null
    }
}