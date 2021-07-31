package net.wyvest.wyvtilities.keybind

import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.config.WyvtilsConfig
import org.lwjgl.input.Keyboard
import xyz.matthewtgm.tgmlib.keybinds.KeyBind


object ChatKeybind : KeyBind(Keyboard.KEY_B) {

    var current : Int = 1

    override fun name(): String {
        return "ok"
    }

    override fun category(): String {
        return "Wyvtilities"
    }

    override fun pressed() {
        when (current) {
            1 -> {
                check(WyvtilsConfig.chatType2)
                current += 1
            }
            2 -> {
                check(WyvtilsConfig.chatType1)
                current -= 1
            }
            //3 -> check(WyvtilsConfig.chatType4)
        }
    }

    override fun held() {
        //println("held")
    }

    override fun released() {}

    private fun check(option : Int) {
        when (option) {
            0 -> mc.thePlayer.sendChatMessage("/chat a")
            1 -> mc.thePlayer.sendChatMessage("/chat p")
            2 -> mc.thePlayer.sendChatMessage("/chat g")
            3 -> mc.thePlayer.sendChatMessage("/chat o")
            else -> return
        }
    }
}