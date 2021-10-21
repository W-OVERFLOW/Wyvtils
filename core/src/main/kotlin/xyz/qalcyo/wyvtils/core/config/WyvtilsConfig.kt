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

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import xyz.qalcyo.wyvtils.core.WyvtilsCore
import xyz.qalcyo.wyvtils.core.WyvtilsInfo
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
        name = "Toggle Hitboxes for Items",
        category = "Hitbox",
        description = "Toggle the hitboxes of items."
    )
    var itemHitBox = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Hitboxes for Itemframes",
        category = "Hitbox",
        description = "Toggle the hitboxes of itemframes."
    )
    var itemFrameHitBox = true

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
        registerListener("textColor") { _: Int ->
            WyvtilsCore.changeTextColor = true
        }
    }
}