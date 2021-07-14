package net.wyvest.wyvtilities.listeners

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.listeners.ChatListener.victoryDetected
import net.wyvest.wyvtilities.utils.GexpUtils
import net.wyvest.wyvtilities.utils.containsAny
import xyz.matthewtgm.tgmlib.events.TitleEvent
import xyz.matthewtgm.tgmlib.util.Multithreading
import xyz.matthewtgm.tgmlib.util.Notifications

object TitleListener {
    @SubscribeEvent
    fun onTitle(event : TitleEvent) {
        if (WyvtilsConfig.autoGetGEXP && WyvtilsConfig.isRegexLoaded) {
            if (!victoryDetected) {
                if (event.title.containsAny("win", "won", "over", "end", "victory")) {
                    if (ChatListener.worldLeft) {
                        ChatListener.worldLeft = false
                        event.isCanceled = true
                        return
                    }
                    victoryDetected = true
                    Multithreading.runAsync {
                        GexpUtils.getGEXP()
                        Notifications.push("Wyvtilities", "You currently have " + GexpUtils.gexp + " guild EXP.")
                    }
                    return
                }
            }
        }
    }
}