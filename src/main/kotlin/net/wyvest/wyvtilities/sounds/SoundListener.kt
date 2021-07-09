package net.wyvest.wyvtilities.sounds

import net.minecraft.client.audio.PositionedSound
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.equalsAny
import net.wyvest.wyvtilities.utils.startsWithAny

object SoundListener {
    //if making a fork, do not touch this right now as i am currently working on this feature!
    @SubscribeEvent
    fun onPlaySound(e : PlaySoundEvent) {
        if (e.result is PositionedSound) {
            if (e.name.equalsAny("random.successful_hit", "random.break", "random.drink", "random.eat", "random.bow", "random.bowhit", "mob.ghast.fireball", "mob.ghast.charge") || e.name.startsWithAny("dig.", "step.", "game.player.")) {
                (e.result as PositionedSound).volume *= WyvtilsConfig.soundMultiplier
            }
        }
    }
}