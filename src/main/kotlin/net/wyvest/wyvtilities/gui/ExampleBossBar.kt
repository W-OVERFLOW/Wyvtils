package net.wyvest.wyvtilities.gui

import net.minecraft.client.gui.hud.ClientBossBar
import net.minecraft.text.Text
import java.util.UUID

class ExampleBossBar : ClientBossBar(UUID.fromString("cd899a14-de78-4de8-8d31-9d42fff31d7a"), Text.of("Wyvtilities"), 1.0F, Color.PURPLE, Style.NOTCHED_20, false, false, false)