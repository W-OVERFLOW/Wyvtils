package net.wyvest.wyvtilities.config

import gg.essential.universal.ChatColor
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.MinecraftClient
import net.wyvest.wyvtilities.gui.ActionBarGui
import net.wyvest.wyvtilities.gui.BossBarGui
import java.awt.Color
import java.io.File

@Suppress("unused")
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
        MinecraftClient.getInstance().setScreen(BossBarGui(gui()))
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

    @Property(
        type = PropertyType.COLOR,
        name = "Debug HUD Color",
        description = "Set the color of the Debug HUD background color.",
        category = "Debug"
    )
    var debugColor: Color = Color(-1873784752)

    @Property(
        type = PropertyType.SWITCH,
        name = "Debug HUD Shadow",
        description = "Turn on / off the shadow of Debug HUD text.",
        category = "Debug"
    )
    var debugShadow = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Action Bar Customization",
        description = "Toggle customization of the action bar.",
        category = "Action Bar"
    )
    var actionBarCustomization = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Action Bar",
        description = "Toggle the action bar.",
        category = "Action Bar"
    )
    var actionBar = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Action Bar Shadow",
        description = "Toggle the action bar shadow.",
        category = "Action Bar"
    )
    var actionBarShadow = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Action Bar Position",
        description = "Toggle the action bar position customization.",
        category = "Action Bar"
    )
    var actionBarPosition = false

    @Property(
        type = PropertyType.BUTTON,
        name = "Action Bar Editor",
        description = "Change the position of the action bar.",
        category = "Action Bar"
    )
    fun openActionBarGui() {
        if (actionBarPosition && actionBarCustomization) MinecraftClient.getInstance().setScreen(ActionBarGui(gui()))
    }

    @Property(
        type = PropertyType.NUMBER,
        name = "Action Bar X",
        description = "X",
        category = "Action Bar",
        hidden = true
    )
    var actionBarX: Int = 0

    @Property(
        type = PropertyType.NUMBER,
        name = "Action Bar Y",
        description = "Y",
        category = "Action Bar",
        hidden = true
    )
    var actionBarY: Int = 12

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Title Scale",
        description = "Set the scale for the title.",
        category = "Title"
    )
    var titleScale = 1.0F

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Subtitle Scale",
        description = "Set the scale for the subtitle.",
        category = "Title"
    )
    var subtitleScale = 1.0F

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Hitbox",
        category = "Hitbox",
        description = "Toggle the hitbox of entities."
    )
    var hitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Hitboxes for Non-Players",
        category = "Hitbox",
        description = "Toggle the hitboxes of non-players."
    )
    var nonplayerHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Force Hitboxes",
        category = "Hitbox",
        description = "Force the rendering of hitbox of entities."
    )
    var forceHitbox = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Disable for Self",
        category = "Hitbox",
        description = "Don't render the hitbox if the player's hitbox is you."
    )
    var disableForSelf = false
/*/
    @Property(
        type = PropertyType.COLOR,
        name = "Hitbox Color",
        category = "Hitbox",
        description = "Change the color of the hitbox.",
    )
    var hitboxColor: Color = Color.WHITE

    @Property(
        type = PropertyType.COLOR,
        name = "Hitbox Color (within crosshair)",
        category = "Hitbox",
        description = "Change the color of the hitbox of players when they are within the crosshair of the player.",
    )
    var hitboxRangeColor: Color = Color.RED

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Line Hitbox",
        category = "Hitbox",
        description = "Toggle the eye line hitbox of entities."
    )
    var hitboxEyeLine = true

    @Property(
        type = PropertyType.COLOR,
        name = "Hitbox Line Color",
        category = "Hitbox",
        description = "Change the color of the hitbox eye line.",
    )
    var hitboxEyelineColor: Color = Color(0, 0, 255, 255)

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Hitbox Line of Sight",
        category = "Hitbox",
        description = "Toggle the hitbox of entities."
    )
    var hitboxLineOfSight = true


    @Property(
        type = PropertyType.COLOR,
        name = "Line of Sight Color",
        category = "Hitbox",
        description = "Change the color of the hitbox's line of sight.",
    )
    var hitboxLineOfSightColor: Color = Color(255, 0, 0, 255)

 */

}