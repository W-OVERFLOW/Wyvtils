/*
 * Wyvtils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Wyvtils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.qalcyo.wyvtils.core.config

import gg.essential.universal.ChatColor
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import xyz.qalcyo.wyvtils.core.WyvtilsCore
import xyz.qalcyo.wyvtils.core.WyvtilsInfo
import xyz.qalcyo.wyvtils.core.listener.Listener
import xyz.qalcyo.wyvtils.core.utils.MinecraftVersions
import java.awt.Color
import java.io.File

object WyvtilsConfig: Vigilant(
    File(WyvtilsCore.modDir, "${WyvtilsInfo.ID}.toml")
) {
    @Property(
        type = PropertyType.SWITCH,
        name = "Reverse Inventory Scrolling",
        description = "Reverse the direction of which the inventory scrolls to when scrolling on your mouse.",
        category = "General"
    )
    var reverseScrolling = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide Locraw Messages",
        description = "Hide locraw messages in chat (e.g {\"server\": \"something\"}).",
        category = "General"
    )
    var hideLocraw = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Disable Text Shadow",
        description = "Disable the shadow on text rendering. Can boost performance.",
        category = "General"
    )
    var disableTextShadow = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Render Own Nametag",
        description = "Render your own nametag in third person.",
        category = "General",
        subcategory = "Nametags"
    )
    var renderOwnNametag = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Nametag Background",
        description = "Remove the background of nametags.",
        category = "General",
        subcategory = "Nametags"
    )
    var removeNametagBackground = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Nametag Text Shadow",
        description = "Render a text shadow with the nametag text.",
        category = "General",
        subcategory = "Nametags"
    )
    var nametagTextShadow = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Highlight Name",
        description = "Highlight your name.",
        category = "Text"
    )
    var highlightName = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Only Highlight In Chat",
        description = "Makes the name highlight only appear in chat.",
        category = "Text"
    )
    var chatHightlight = false

    @Property(
        type = PropertyType.SELECTOR,
        name = "Text Color",
        description = "Change the text color for the highlight.",
        category = "Text",
        options = ["Black", "Dark Blue", "Dark Green", "Dark Aqua", "Dark Red", "Dark Purple", "Gold", "Gray", "Dark Gray", "Blue", "Green", "Aqua", "Red", "Light Purple", "Yellow", "White"]
    )
    var textColor = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Title",
        description = "Toggle the title on or off.",
        category = "Titles"
    )
    var title = true

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Title Scale Percentage",
        description = "Change the scale of the title.",
        category = "Titles",
    )
    var titleScale = 1.0F

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Subtitle Scale Percentage",
        description = "Change the scale of the subtitle.",
        category = "Titles",
    )
    var subtitleScale = 1.0F

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Title Shadow",
        description = "Toggle the title and subtitle's shadow.",
        category = "Titles"
    )
    var titleShadow = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Hitbox",
        category = "Hitbox",
        description = "Toggle the hitbox of entities."
    )
    var hitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Accurate Hitbox",
        category = "Hitbox",
        description = "In vanilla, hitboxes are a bit smaller compared to their actual size. When this is on, it make the hitboxes accurate."
    )
    var accurateHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Player Hitboxes",
        description = "Turn on and off player hitboxes from displaying.",
        category = "Hitbox"
    )
    var playerHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Passive Hitboxes",
        description = "Turn on and off passive entity hitboxes from displaying.",
        category = "Hitbox"
    )
    var passiveHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Monster Hitboxes",
        description = "Turn on and off monster hitboxes from displaying.",
        category = "Hitbox"
    )
    var monsterHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Armorstand Hitboxes",
        description = "Turn on and off player armorstand from displaying.",
        category = "Hitbox"
    )
    var armorstandHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Fireball Hitboxes",
        description = "Turn on and off fireball hitboxes from displaying.",
        category = "Hitbox"
    )
    var fireballHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Minecart Hitboxes",
        description = "Turn on and off minecart hitboxes from displaying.",
        category = "Hitbox"
    )
    var minecartHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Wither Skull Hitboxes",
        description = "Turn on and off wither skull hitboxes from displaying.",
        category = "Hitbox"
    )
    var witherSkullHitboxes = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Item Hitboxes",
        description = "Turn on and off item hitboxes from displaying.",
        category = "Hitbox"
    )
    var itemHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Firework Hitboxes",
        description = "Turn on and off firework hitboxes from displaying.",
        category = "Hitbox"
    )
    var fireworkHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle XP Orb Hitboxes",
        description = "Turn on and off XP Orb hitboxes from displaying.",
        category = "Hitbox"
    )
    var xpOrbHitbox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Projectile Hitboxes",
        description = "Turn on and off projectile hitboxes from displaying.",
        category = "Hitbox"
    )
    var projectileHitbox = true

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

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Hitbox Box",
        category = "Hitbox",
        description = "Toggle the hitbox box of entities."
    )
    var hitboxBox = true

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
    var hitboxCrosshairColor: Color = Color.WHITE

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

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Update Notification",
        description = "Show a notification when you start Minecraft informing you of new updates.",
        category = "Updater"
    )
    var showUpdateNotification = true

    @Property(
        type = PropertyType.SWITCH,
        name = "First time",
        description = "ok.",
        category = "General",
        hidden = true
    )
    var firstTime = true

    //TODO
    /*/
    @Property(
        type = PropertyType.BUTTON,
        name = "Update Now",
        description = "Update $NAME by clicking the button.",
        category = "Updater"
    )
    fun update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
            .openScreen(DownloadGui(mc.currentScreen)) else EssentialAPI.getNotifications()
            .push(MOD_NAME, "No update had been detected at startup, and thus the update GUI has not been shown.")
    }

     */

    init {
        initialize()
        hidePropertyIf("disableTextShadow") {
            WyvtilsCore.currentVersion == MinecraftVersions.EIGHT
        }
        hidePropertyIf("renderOwnNametag") {
            WyvtilsCore.currentVersion == MinecraftVersions.EIGHT
        }
        hidePropertyIf("removeNametagBackground") {
            WyvtilsCore.currentVersion == MinecraftVersions.EIGHT
        }
        hidePropertyIf("nametagTextShadow") {
            WyvtilsCore.currentVersion == MinecraftVersions.EIGHT
        }
        registerListener("textColor") { color: Int ->
            Listener.color = when (color) {
                0 -> ChatColor.BLACK.toString()
                1 -> ChatColor.DARK_BLUE.toString()
                2 -> ChatColor.DARK_GREEN.toString()
                3 -> ChatColor.DARK_AQUA.toString()
                4 -> ChatColor.DARK_RED.toString()
                5 -> ChatColor.DARK_PURPLE.toString()
                6 -> ChatColor.GOLD.toString()
                7 -> ChatColor.GRAY.toString()
                8 -> ChatColor.DARK_GRAY.toString()
                9 -> ChatColor.BLUE.toString()
                10 -> ChatColor.GREEN.toString()
                11 -> ChatColor.AQUA.toString()
                12 -> ChatColor.RED.toString()
                13 -> ChatColor.LIGHT_PURPLE.toString()
                14 -> ChatColor.YELLOW.toString()
                else -> ChatColor.WHITE.toString()
            }
        }
    }
}