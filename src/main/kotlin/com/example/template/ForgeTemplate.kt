package com.example.template

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

private const val NAME = "ForgeTemplate"
private const val VERSION = "1.0.0"
private const val ID = "forgetemplate"
private const val ADAPTER = "com.example.template.adapter.KotlinLanguageAdapter"

@Mod(name = NAME, modid = ID, version = VERSION, modLanguageAdapter = ADAPTER)
object ForgeTemplate {
    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        println("Hello, World!")
    }
}