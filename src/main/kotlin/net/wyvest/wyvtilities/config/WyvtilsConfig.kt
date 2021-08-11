/*
 * Wyvtilities - Utilities for Hypixel 1.8.9.
 * Copyright (C) 2021 Wyvtilities
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

package net.wyvest.wyvtilities.config

import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.wyvest.wyvtilities.Wyvtilities.mc
import net.wyvest.wyvtilities.gui.ActionBarGui
import net.wyvest.wyvtilities.gui.BossHealthGui
import net.wyvest.wyvtilities.gui.DownloadConfirmGui
import net.wyvest.wyvtilities.listeners.Listener
import net.wyvest.wyvtilities.utils.Updater
import java.awt.Color
import java.io.File
import kotlin.math.roundToInt

@Suppress("unused")
object WyvtilsConfig :
    Vigilant(File("./config/Wyvtilities/wyvtilities.toml"), "Wyvtilities", sortingBehavior = ConfigSorting) {

    @Property(
        type = PropertyType.TEXT,
        name = "API Key",
        description = "The API key, used for some features.",
        category = "General",
        protectedText = true
    )
    var apiKey = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "Automatically Check GEXP",
        description = "Automatically check your GEXP after you win a Hypixel game. Requires an API Key.",
        category = "Automatic"
    )
    var autoGetGEXP = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Automatically Check Winstreak",
        description = "Automatically check your winstreak after you win a Hypixel game. Requires an API Key.",
        category = "Automatic"
    )
    var autoGetWinstreak = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Automatically get API Key",
        description = "Automatically get the API Key when /api new is sent.",
        category = "General"
    )
    var autoGetAPI = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Highlight Name",
        description = "Highlight your name.",
        category = "Text"
    )
    var highlightName = false

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
        name = "Sound Boost",
        description = "Make the volume of sounds that are important for PVP louder.",
        category = "Sound"
    )
    var soundBoost = false

    @Property(
        type = PropertyType.SLIDER,
        name = "Sound Multiplier",
        description = "How much louder the sound is. There is a volume limit, so it doesn't break your ears.\n" +
                "1 will make the sound the same.",
        category = "Sound",
        min = 0,
        max = 100
    )
    var soundMultiplier = 1

    @Property(
        type = PropertyType.SLIDER,
        name = "Sound Decrease",
        description = "How much quieter the non-important sounds are.\n" +
                "1 will make the sound the same.",
        category = "Sound",
        min = 0,
        max = 100
    )
    var soundDecrease = 1

    @Property(
        type = PropertyType.SWITCH,
        name = "Bossbar Customization",
        description = "Toggle Bossbar customization",
        category = "Bossbar"
    )
    var bossBarCustomization = true

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
        type = PropertyType.BUTTON,
        name = "Bossbar Editor",
        description = "Change the position of the bossbar.",
        category = "Bossbar"
    )
    fun openBossHealthGui() {
        if (bossBarCustomization) EssentialAPI.getGuiUtil().openScreen(BossHealthGui())
        else EssentialAPI.getNotifications()
            .push("Wyvtilities", "You can't do that, you haven't enabled Bossbar Customization!")
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Reset Position",
        description = "Reset the position of the bossbar to its original position.",
        category = "Bossbar"
    )
    fun resetBossbar() {
        mc.displayGuiScreen(null)
        bossBarX = ((ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2) / bossbarScale).roundToInt()
        bossBarY = 12
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
        EssentialAPI.getGuiUtil().openScreen(gui())
    }

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Bossbar Scale",
        description = "Change the scale of the bossbar (THIS MAY BE BUGGY SOMETIMES!).",
        category = "Bossbar"
    )
    var bossbarScale = 1.0F

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
        if (actionBarPosition && actionBarCustomization) EssentialAPI.getGuiUtil().openScreen(ActionBarGui())
        else EssentialAPI.getNotifications()
            .push("Wyvtilities", "You can't do that, you don't have Action Bar position enabled!")
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
        type = PropertyType.SELECTOR,
        name = "Chat Type 1",
        category = "Chat Switcher",
        options = ["All", "Party", "Guild", "Officer"]
    )
    var chatType1 = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Chat Type 2",
        category = "Chat Switcher",
        options = ["All", "Party", "Guild", "Officer", "None"]
    )
    var chatType2 = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Hitbox",
        category = "Hitbox",
        description = "Toggle the hitbox of entities."
    )
    var hitbox = true

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
        name = "Hitbox Color (within range)",
        category = "Hitbox",
        description = "Change the color of the hitbox of players when they are within range of the player.",
    )
    var hitboxRangeColor: Color = Color.WHITE

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
        type = PropertyType.BUTTON,
        name = "Update Now",
        description = "Update Wyvtilities by clicking the button.",
        category = "Updater"
    )
    fun update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil().openScreen(DownloadConfirmGui(mc.currentScreen)) else EssentialAPI.getNotifications().push("Wyvtilities", "No update had been detected at startup, and thus the update GUI has not been shown.")
    }

    init {
        initialize()

        setCategoryDescription(
            "General",
            "This category is for configuring general parts of Wyvtils."
        )
        setCategoryDescription(
            "Action Bar",
            "Configure action bar-related features in Wyvtils."
        )
        setCategoryDescription(
            "Text",
            "Configure text-related features in Wyvtils."
        )
        setCategoryDescription(
            "GEXP",
            "Configure GEXP-related features in Wyvtils."
        )
        setCategoryDescription(
            "Bossbar",
            "Configure bossbar-related features in Wyvtils."
        )
        setCategoryDescription(
            "Sound",
            "Configure sound-related features in Wyvtils."
        )
        setCategoryDescription(
            "Hitboxes",
            "Configure hitbox-related features in Wyvtils."
        )
        registerListener("textColor") { _: Int ->
            Listener.changeTextColor = true
        }
        addDependency("textColor", "highlightName")
        addDependency("soundMultiplier", "soundBoost")
        listOf(
            "bossBar",
            "bossBarText",
            "bossBarShadow",
            "bossBarBar",
        ).forEach { propertyName -> addDependency(propertyName, "bossBarCustomization") }
        listOf(
            "actionBar",
            "actionBarShadow",
            "actionBarPosition"
        ).forEach { propertyName -> addDependency(propertyName, "actionBarCustomization") }
    }

    private object ConfigSorting : SortingBehavior() {
        override fun getCategoryComparator(): Comparator<in Category> {
            return Comparator { o1, o2 ->
                if (o1.name == "General") return@Comparator -1
                if (o2.name == "General") return@Comparator 1
                else compareValuesBy(o1, o2) {
                    it.name
                }
            }
        }
    }

}
