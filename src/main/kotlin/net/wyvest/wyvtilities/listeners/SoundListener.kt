package net.wyvest.wyvtilities.listeners

import net.minecraft.client.audio.PositionedSound
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.wyvest.wyvtilities.Wyvtilities
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.mixin.AccessorPositionedSound

object SoundListener {
    //if making a fork, do not touch this right now as i am currently working on this feature!
    @SubscribeEvent
    fun onPlaySound(e : PlaySoundEvent) {
        if (WyvtilsConfig.soundBoost) {
            if (e.result is PositionedSound) {
                if (Wyvtilities.checkSound(e.name)) {
                    (e.result as PositionedSound as AccessorPositionedSound).volume *= WyvtilsConfig.soundMultiplier
                }
            }
        }
    }
}