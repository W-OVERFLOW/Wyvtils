package net.wyvest.wyvtilities.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.MinecraftClient
import net.wyvest.wyvtilities.gui.WyvtilsGui
import gg.essential.universal.ChatColor
import java.io.File

object WyvtilsConfig : Vigilant(
    File("config/Wyvtilities/1.17.1/wyvtilities.toml"),
    "${ChatColor.DARK_PURPLE}Wyvtilities"
) {
    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Bossbar",
        description = "Toggle the bossbar.",
        category = "Bossbar"
    )
    var bossBar = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Text",
        description = "Toggle the text for the bossbar.",
        category = "Bossbar"
    )
    var bossBarText = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Shadow",
        description = "Toggle the text shadow for the bossbar.",
        category = "Bossbar"
    )
    var bossBarShadow = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Bar",
        description = "Toggle the bar for the bossbar.",
        category = "Bossbar"
    )
    var bossBarBar = true

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Bossbar Scale",
        description = "Set the scale for the bossbar.",
        category = "Bossbar"
    )
    var bossbarScale = 1.0F

    @Property(
        type = PropertyType.BUTTON,
        name = "Bossbar Editor",
        description = "Change the position of the bossbar.",
        category = "Bossbar"
    )
    fun openBossHealthGui() {
        MinecraftClient.getInstance().setScreen(WyvtilsGui(gui()))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Reset Position",
        description = "Reset the position of the bossbar to its original position.",
        category = "Bossbar"
    )
    fun resetBossbar() {
        MinecraftClient.getInstance().setScreen(null)
        bossBarX = (MinecraftClient.getInstance().window.scaledWidth)
        bossBarY = 12
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
        MinecraftClient.getInstance().setScreen(gui())
    }

    @Property(
        type = PropertyType.NUMBER,
        name = "Bossbar X",
        description = "X",
        category = "Bossbar",
        hidden = true
    )
    var bossBarX: Int = 0

    @Property(
        type = PropertyType.NUMBER,
        name = "Bossbar Y",
        description = "Y",
        category = "Bossbar",
        hidden = true
    )
    var bossBarY: Int = 12


    @Property(
        type = PropertyType.SWITCH,
        name = "bossbar first time",
        description = "a",
        category = "General",
        hidden = true
    )
    var firstLaunchBossbar = true
}