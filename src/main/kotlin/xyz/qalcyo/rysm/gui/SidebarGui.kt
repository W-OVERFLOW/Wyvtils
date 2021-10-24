package xyz.qalcyo.rysm.gui

import gg.essential.api.EssentialAPI
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UMatrixStack
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import xyz.qalcyo.rysm.config.RysmConfig
import xyz.qalcyo.rysm.config.RysmConfig.sidebarScale
import xyz.qalcyo.rysm.config.RysmConfig.sidebarX
import xyz.qalcyo.rysm.config.RysmConfig.sidebarY

class SidebarGui(private var parent: Screen?) : WindowScreen(version = ElementaVersion.V1) {

    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)
        window.onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton == 0) {
                sidebarX = mouseX.toInt()
                sidebarY = mouseY.toInt()
            }
        }.onKeyType { _, keyCode ->
            when (keyCode) {
                InputUtil.GLFW_KEY_UP -> sidebarY -= 5
                InputUtil.GLFW_KEY_DOWN -> sidebarY += 5
                InputUtil.GLFW_KEY_LEFT -> sidebarX -= 5
                InputUtil.GLFW_KEY_RIGHT -> sidebarX += 5
            }
        }
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
        client!!.profiler.push("sidebarGui")
        matrixStack.push()
        val iHaveNoIdeaWhatToNameThisFloat = sidebarScale - 1.0f
        matrixStack.translate(
            (-sidebarX * iHaveNoIdeaWhatToNameThisFloat).toDouble(),
            (-sidebarY * iHaveNoIdeaWhatToNameThisFloat).toDouble(),
            0.0
        )
        matrixStack.scale(sidebarScale, sidebarScale, 1.0f)
        val x = sidebarX - textRenderer.getWidth("Qalcyo!!!") - 3
        val y = sidebarY
        var p = 0
        val list = listOf("ok", "Wyvest", "Qalcyo!!!")
        for (s in list) {
            ++p
            val t: Int = y - p * 9
            val var10001: Int = x - 2
            if (RysmConfig.sidebarBackground) fill(
                matrixStack.toMC(),
                var10001,
                t,
                sidebarX - 1,
                t + 9,
                RysmConfig.sidebarBackgroundColor.rgb
            )
            if (RysmConfig.sidebarTextShadow) {
                textRenderer.drawWithShadow(matrixStack.toMC(), s, x.toFloat(), t.toFloat(), -1)
            } else {
                textRenderer.draw(matrixStack.toMC(), s, x.toFloat(), t.toFloat(), -1)
            }
            if (p != 3 && RysmConfig.sidebarScorePoints) {
                if (RysmConfig.sidebarTextShadow) {
                    textRenderer.drawWithShadow(
                        matrixStack.toMC(),
                        p.toString(),
                        (sidebarX - 1 - textRenderer.getWidth(p.toString())).toFloat(),
                        t.toFloat(),
                        -1
                    )
                } else {
                    textRenderer.draw(
                        matrixStack.toMC(),
                        p.toString(),
                        (sidebarX - 1 - textRenderer.getWidth(p.toString())).toFloat(),
                        t.toFloat(),
                        -1
                    )
                }
            }
        }
        matrixStack.pop()

        client!!.profiler.pop()
    }


    override fun onClose() {
        RysmConfig.markAndWrite()
        EssentialAPI.getGuiUtil().openScreen(parent)
        parent = null
    }
}