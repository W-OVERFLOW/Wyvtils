package net.wyvest.wyvtilities.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screen.Screen

class WyvtilsModMenu : ModMenuApi {

    private val screenFactory : ConfigScreenFactory<Screen> = ConfigScreenFactory {
        WyvtilsConfig.gui()
    }

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return screenFactory
    }
}