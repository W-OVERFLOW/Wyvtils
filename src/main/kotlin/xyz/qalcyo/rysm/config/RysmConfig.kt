/*
 * Rysm, a utility mod for 1.8.9.
 * Copyright (C) 2021 Rysm
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

package xyz.qalcyo.rysm.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.EnumChatFormatting
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.rysm.Rysm
import xyz.qalcyo.rysm.Rysm.mc
import xyz.qalcyo.rysm.RysmInfo
import xyz.qalcyo.rysm.gui.ActionBarGui
import xyz.qalcyo.rysm.gui.BossHealthGui
import xyz.qalcyo.rysm.gui.DownloadGui
import xyz.qalcyo.rysm.gui.SidebarGui
import xyz.qalcyo.rysm.listeners.Listener
import xyz.qalcyo.rysm.utils.Updater
import java.awt.Color
import java.io.File

@Suppress("unused")
object RysmConfig :
    Vigilant(
        File(Rysm.modDir, "${RysmInfo.ID}.toml"),
        "${EnumChatFormatting.DARK_PURPLE}${RysmInfo.NAME}",
        sortingBehavior = ConfigSorting
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
        name = "Left Hand",
        description = "Move the player's hand to the left in first-person.",
        category = "General"
    )
    var leftHand = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Swap Bow Hand",
        description = "Make the bow render in the opposite hand when equipped.",
        category = "General"
    )
    var swapBow = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Item Physics",
        description = "Make dropped items have physics.",
        category = "General"
    )
    var itemPhysics = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide Locraw Messages",
        description = "Hide locraw messages in chat (e.g {\"server\": \"something\"}).",
        category = "General"
    )
    var hideLocraw = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Non-NPCs",
        description = "Remove every entity from rendering except NPCs, armorstands, and your own player.",
        category = "General"
    )
    var removeNonNPCS = false

    @Property(
        type = PropertyType.TEXT,
        name = "API Key",
        description = "The API key, used for some features. Can be also automatically set by typing in /api new ingame.",
        category = "General",
        protectedText = true
    )
    var apiKey = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "Automatically Get API Key",
        description = "Automatically get the API Key from /api new.",
        category = "General"
    )
    var autoGetAPI = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Automatically Check GEXP",
        description = "Automatically check your GEXP after you win a Hypixel game. \\u00a7cRequires an API Key.",
        category = "Automatic"
    )
    var autoGetGEXP = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "GEXP Mode",
        description = "Choose which GEXP to get.",
        category = "Automatic",
        options = ["Daily", "Weekly"]
    )
    var gexpMode = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Automatically Check Winstreak",
        description = "Automatically check your winstreak after you win a Hypixel game. \\u00a7cRequires an API Key.",
        category = "Automatic"
    )
    var autoGetWinstreak = false

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
        name = "Automatically Turn Off Bossbar When In Lobby",
        description = "Automatically turn off the bossbar when the player goes into a lobby, and turn it back on when exiting it.",
        category = "Bossbar"
    )
    var autoBossbarLobby = false

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
        type = PropertyType.COLOR,
        name = "Text Color",
        description = "Change the text color of the bossbar.",
        category = "Bossbar"
    )
    var bossbarTextColor: Color = Color.WHITE

    @Property(
        type = PropertyType.SWITCH,
        name = "Bar Colour Customization",
        description = "Change the bar colour of the bossbar.",
        category = "Bossbar"
    )
    var bossbarBarColorOn = false

    @Property(
        type = PropertyType.COLOR,
        name = "Bar Color",
        description = "Change the bar color of the bossbar.",
        category = "Bossbar"
    )
    var bossbarBarColor: Color = Color.BLUE

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
        if (bossBarCustomization) Requisite.getInstance().guiHelper.open(BossHealthGui())
        else Requisite.getInstance().notifications
            .push(RysmInfo.NAME, "You can't do that, you haven't enabled Bossbar Customization!")
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Reset Position",
        description = "Reset the position of the bossbar to its original position.",
        category = "Bossbar"
    )
    fun resetBossbar() {
        Requisite.getInstance().guiHelper.open(null)
        bossBarX = (ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2)
        bossBarY = 12
        RysmConfig.markDirty()
        RysmConfig.writeData()
        Requisite.getInstance().guiHelper.open(gui())
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
        name = "Action Bar Background",
        description = "Add a background to the action bar.",
        category = "Action Bar"
    )
    var actionBarBackground = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Action Bar Round Background",
        description = "Make the background of the action bar rounded.",
        category = "Action Bar"
    )
    var actionBarRound = false

    @Property(
        type = PropertyType.NUMBER,
        name = "Rounded Background Radius",
        description = "Set the corner radius of the rounded background.",
        category = "Action Bar",
        min = 0,
        max = 10
    )
    var actionBarRoundRadius = 4

    @Property(
        type = PropertyType.NUMBER,
        name = "Background Padding Amount",
        description = "Change the amount of padding added to the background.",
        category = "Action Bar",
        min = 0,
        max = 10
    )
    var actionBarPadding = 2

    @Property(
        type = PropertyType.COLOR,
        name = "Background Color",
        description = "Change the text color for the HUD.",
        category = "Action Bar"
    )
    var actionBarBackgroundColor: Color = Color(0, 0, 0, 128)

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
        if (actionBarPosition && actionBarCustomization) Requisite.getInstance().guiHelper.open(ActionBarGui())
        else Requisite.getInstance().notifications
            .push(RysmInfo.NAME, "You can't do that, you don't have Action Bar position enabled!")
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
        name = "Toggle Sidebar",
        category = "Sidebar",
        description = "Toggle the sidebar from rendering."
    )
    var sidebar = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Sidebar Text Shadow",
        category = "Sidebar",
        description = "Toggle the sidebar text's shadow from rendering."
    )
    var sidebarTextShadow = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Score Points",
        category = "Sidebar",
        description = "Toggle the sidebar score points (aka red numbers) from rendering."
    )
    var sidebarScorePoints = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Background",
        category = "Sidebar",
        description = "Toggle the background from rendering."
    )
    var sidebarBackground = true

    @Property(
        type = PropertyType.COLOR,
        name = "Sidebar Background Color",
        category = "Sidebar",
        description = "Change the text color for the sidebar."
    )
    var sidebarBackgroundColor: Color = Color(0, 0, 0, 50)

    @Property(
        type = PropertyType.SWITCH,
        name = "Background Border",
        category = "Sidebar",
        description = "Enable a border to border the background."
    )
    var backgroundBorder = false

    @Property(
        type = PropertyType.COLOR,
        name = "Border Color",
        category = "Sidebar",
        description = "Select a color to set for the background border."
    )
    var borderColor: Color = Color.WHITE

    @Property(
        type = PropertyType.NUMBER,
        name = "Border Width",
        category = "Sidebar",
        description = "Set the width of the background border.",
        min = 1,
        max = 20
    )
    var borderNumber = 2

    @Property(
        type = PropertyType.SWITCH,
        name = "Sidebar Position",
        category = "Sidebar",
        description = "Toggle the sidebar position editor."
    )
    var sidebarPosition = false

    @Property(
        type = PropertyType.BUTTON,
        name = "Sidebar Editor",
        description = "Change the position of the sidebar.",
        category = "Sidebar"
    )
    fun openSidebarGui() {
        if (sidebarPosition) Requisite.getInstance().guiHelper.open(SidebarGui())
        else Requisite.getInstance().notifications
            .push(RysmInfo.NAME, "You can't do that, you don't have Sidebar position enabled!")
    }

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Sidebar Scale",
        description = "Set the scale for the sidebar.",
        category = "Sidebar"
    )
    var sidebarScale = 1.0F

    @Property(
        type = PropertyType.NUMBER,
        name = "Sidebar X",
        description = "X",
        category = "Sidebar",
        hidden = true
    )
    var sidebarX: Int = 0

    @Property(
        type = PropertyType.NUMBER,
        name = "Sidebar Y",
        description = "Y",
        category = "Sidebar",
        hidden = true
    )
    var sidebarY: Int = 12

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

    @Property(
        type = PropertyType.BUTTON,
        name = "Update Now",
        description = "Update ${RysmInfo.NAME} by clicking the button.",
        category = "Updater"
    )
    fun update() {
        if (Updater.shouldUpdate) Requisite.getInstance().guiHelper.open(DownloadGui(mc.currentScreen)) else Requisite.getInstance().notifications
            .push(RysmInfo.NAME, "No update had been detected at startup, and thus the update GUI has not been shown.")
    }

    init {
        initialize()
        setCategoryDescription(
            "General",
            "This category is for configuring general parts of ${RysmInfo.NAME}."
        )
        setCategoryDescription(
            "Action Bar",
            "Configure action bar-related features in ${RysmInfo.NAME}."
        )
        setCategoryDescription(
            "Text",
            "Configure text-related features in ${RysmInfo.NAME}."
        )
        setCategoryDescription(
            "GEXP",
            "Configure GEXP-related features in ${RysmInfo.NAME}."
        )
        setCategoryDescription(
            "Bossbar",
            "Configure bossbar-related features in ${RysmInfo.NAME}."
        )
        setCategoryDescription(
            "Hitboxes",
            "Configure hitbox-related features in ${RysmInfo.NAME}."
        )
        registerListener("textColor") { _: Int ->
            Listener.changeTextColor = true
        }
        addDependency("textColor", "highlightName")
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
