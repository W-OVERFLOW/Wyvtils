package xyz.qalcyo.wyvtils.gui

import com.mojang.blaze3d.systems.RenderSystem
import gg.essential.api.EssentialAPI
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UMatrixStack
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.hud.BackgroundHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import xyz.qalcyo.wyvtils.config.WyvtilsConfig
import xyz.qalcyo.wyvtils.config.WyvtilsConfig.actionBarX
import xyz.qalcyo.wyvtils.config.WyvtilsConfig.actionBarY
import java.awt.Color
import java.util.*

class ActionBarGui(private var parent: Screen?) : WindowScreen(version = ElementaVersion.V1) {
    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                actionBarX = mouseX.toInt()
                actionBarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                InputUtil.GLFW_KEY_UP -> actionBarY -= 5
                InputUtil.GLFW_KEY_DOWN -> actionBarY += 5
                InputUtil.GLFW_KEY_LEFT -> actionBarX -= 5
                InputUtil.GLFW_KEY_RIGHT -> actionBarX += 5
            }
        }
    }
    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
        client!!.profiler.push("overlayGuiMessage")
        matrixStack.push()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        this.drawTextBackground(matrixStack.toMC(), textRenderer)
        textRenderer.draw(
            matrixStack.toMC(),
            "Wyvtils Action Bar",
            actionBarX.toFloat(),
            actionBarY.toFloat(),
            Color.WHITE.rgb
        )
        RenderSystem.disableBlend()
        matrixStack.pop()

        client!!.profiler.pop()
    }


    override fun onClose() {
        WyvtilsConfig.markAndWrite()
        EssentialAPI.getGuiUtil().openScreen(parent)
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