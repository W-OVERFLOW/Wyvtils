package net.wyvest.wyvtilities.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.wyvest.wyvtilities.bossbar.BossHealthGui
import net.wyvest.wyvtilities.listeners.Listener
import xyz.matthewtgm.tgmlib.util.GuiHelper
import java.awt.Color
import java.io.File

@Suppress("unused")
object WyvtilsConfig : Vigilant(File("./config/wyvtilities.toml"), "Wyvtilities", sortingBehavior = ConfigSorting)  {

    @Property(type = PropertyType.PARAGRAPH, name = "Info", description = "You are using Wyvtilities version 0.5.0, made by Wyvest.", category = "Information")
    var paragraph = ""

    @Property(type = PropertyType.TEXT, name = "API Key", description = "The API key, used for some features.", category = "General", protectedText = true)
    var apiKey = ""

    @Property(type = PropertyType.SWITCH, name = "Automatically Check GEXP", description = "Automatically check your GEXP after you win a Hypixel game. Requires an API Key.", category = "GEXP")
    var autoGetGEXP = true

    @Property(type = PropertyType.SWITCH, name = "is regex loaded", category = "GEXP", hidden = true)
    var isRegexLoaded = true

    @Property(type = PropertyType.SWITCH, name = "Show Update Notification", description = "Show a notification when you start Minecraft informing you of new updates.", category = "General")
    var showUpdateNotification = true

    @Property(type = PropertyType.SWITCH, name = "Automatically get API Key", description = "Automatically get the API Key when /api new is sent.", category = "General")
    var autoGetAPI = true

    @Property(type = PropertyType.SWITCH, name = "Highlight Name", description = "Highlight your name.", category = "Text")
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
        description = "How much louder the sound is. There is a volume limit, so it doesn't break your ears.",
        category = "Sound",
        min = 0,
        max = 100
    )
    var soundMultiplier = 1

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
        type = PropertyType.COLOR,
        name = "Bossbar Color",
        description = "Change the color of the bossbar..",
        category = "Bossbar"
    )
    var bossBarColor : Color = Color.WHITE

    @Property(
        type = PropertyType.BUTTON,
        name = "Bossbar Editor",
        description = "Change the position of the bossbar.",
        category = "Bossbar"
    )
    fun openBossHealthGui() {
        if (bossBarCustomization) GuiHelper.open(BossHealthGui)
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Reset Position",
        description = "Reset the position of the bossbar to its original position.",
        category = "Bossbar"
    )
    fun resetBossbar() {
        GuiHelper.open(null)
        bossBarX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2 - 91
        bossBarY = 12
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
        GuiHelper.open(this.gui())
    }

    @Property(
        type = PropertyType.NUMBER,
        name = "Bossbar X",
        description = "X",
        category = "Bossbar",
        hidden = true
    )
    var bossBarX : Int = 0

    @Property(
        type = PropertyType.NUMBER,
        name = "Bossbar Y",
        description = "Y",
        category = "Bossbar",
        hidden = true
    )
    var bossBarY : Int = 12



    @Property(
        type = PropertyType.SWITCH,
        name = "bossbar first time",
        description = "a",
        category = "General",
        hidden = true
    )
    var firstLaunchBossbar = true

    /*/
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
     */

    @Property(type = PropertyType.PARAGRAPH, name = "TGMDevelopment", description = "Utilities", category = "Information", subcategory = "Credits")
    var credits1 = ""

    @Property(type = PropertyType.PARAGRAPH, name = "Skytils", description = "Even more utilities", category = "Information", subcategory = "Credits")
    var credits2 = ""

    init {
        initialize()

        setCategoryDescription(
            "General",
            "This category is for configuring general parts of Wyvtils."
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
        setSubcategoryDescription(
            "Information",
            "Credits",
            "This mod would not be possible without OSS projects and other forms of help. This page lists the people who helped make this mod."
        )
        registerListener("textColor") {
                _: Int ->
            Listener.changeTextColor = true
        }
        addDependency("textColor", "highlightName")
        addDependency("autoGetGEXP", "isRegexLoaded")
        addDependency("soundMultiplier", "soundBoost")
        addDependency("bossBar", "bossBarCustomization")
        addDependency("bossBarText", "bossBarCustomization")
        addDependency("bossBarShadow", "bossBarText")
        addDependency("bossBarShadow", "bossBarCustomization")
        addDependency("bossBarBar", "bossBarCustomization")
        addDependency("bossBarColor", "bossBarCustomization")
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
