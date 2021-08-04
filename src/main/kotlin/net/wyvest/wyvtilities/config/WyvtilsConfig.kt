package net.wyvest.wyvtilities.config

import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.wyvest.wyvtilities.Wyvtilities.MOD_NAME
import net.wyvest.wyvtilities.Wyvtilities.VERSION
import net.wyvest.wyvtilities.gui.ActionBarGui
import net.wyvest.wyvtilities.gui.BossHealthGui
import net.wyvest.wyvtilities.listeners.Listener
import xyz.matthewtgm.requisite.util.GuiHelper
import java.io.File

@Suppress("unused")
object WyvtilsConfig : Vigilant(File("./config/wyvtilities.toml"), "Wyvtilities", sortingBehavior = ConfigSorting) {

    @Property(
        type = PropertyType.PARAGRAPH,
        name = "Info",
        description = "You are using $MOD_NAME version $VERSION, made by Wyvest.",
        category = "Information"
    )
    var paragraph = ""

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
        name = "Show Update Notification",
        description = "Show a notification when you start Minecraft informing you of new updates.",
        category = "General"
    )
    var showUpdateNotification = true

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
        if (bossBarCustomization) GuiHelper.open(BossHealthGui)
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
        GuiHelper.open(null)
        bossBarX = ScaledResolution(Minecraft.getMinecraft()).scaledWidth / 2
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
        if (actionBarPosition && actionBarCustomization) EssentialAPI.getGuiUtil().openScreen(ActionBarGui)
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
        type = PropertyType.PARAGRAPH,
        name = "Sk1er LLC",
        description = "Essential + Vigilance",
        category = "Information",
        subcategory = "Credits"
    )
    var credits0 = ""

    @Property(
        type = PropertyType.PARAGRAPH,
        name = "TGMDevelopment",
        description = "TGMLib",
        category = "Information",
        subcategory = "Credits"
    )
    var credits1 = ""

    @Property(
        type = PropertyType.PARAGRAPH,
        name = "Skytils",
        description = "Even more utilities",
        category = "Information",
        subcategory = "Credits"
    )
    var credits2 = ""

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
        setSubcategoryDescription(
            "Information",
            "Credits",
            "This mod would not be possible without OSS projects and other forms of help. This page lists the people / organizations who helped make this mod."
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
