package net.wyvest.wyvtilities.keybind

import org.lwjgl.input.Keyboard
import xyz.matthewtgm.tgmlib.keybinds.KeyBind


object ChatKeybind : KeyBind(Keyboard.KEY_B) {
    override fun name(): String {
        return "ok"
    }

    override fun category(): String {
        return "Wyvtilities"
    }

    override fun pressed() {
        //println("pressed")
    }

    override fun held() {
        //println("held")
    }

    override fun released() {}
}