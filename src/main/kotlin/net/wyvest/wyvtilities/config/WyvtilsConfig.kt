package net.wyvest.wyvtilities.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import net.wyvest.wyvtilities.bossbar.BossHealthGui
import net.wyvest.wyvtilities.chat.ChatListener
import xyz.matthewtgm.tgmlib.util.GuiHelper
import java.awt.Color
import java.io.File

object WyvtilsConfig : Vigilant(File("./config/wyvtilities.toml"), "Wyvtilities", sortingBehavior = ConfigSorting)  {

    @Property(type = PropertyType.PARAGRAPH, name = "Info", description = "You are using Wyvtilities version 0.5.0-BETA1, made by Wyvest.", category = "Information")
    var paragraph = ""

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.TEXT, name = "API Key", description = "The API key, used for some features.", category = "General", protectedText = true)
    var apiKey = ""

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Automatically Check GEXP", description = "Automatically check your GEXP after you win a Hypixel game. Requires an API Key.", category = "GEXP")
    var autoGetGEXP = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Block Discord Invites", description = "Filter out discord invites in chat.", category = "Chat", subcategory = "Discord")
    var removeDiscordInvites = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Don't Block in Party Chat", description = "Disables the discord invite blocking feature in party chat.", category = "Chat", subcategory = "Discord")
    var showInPartyChat = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Don't Block in Guild Chat", description = "Disables the discord invite blocking feature in guild chat.", category = "Chat", subcategory = "Discord")
    var showInGuildChat = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Don't Block in Officer Chat", description = "Disables the discord invite blocking feature in officer chat.", category = "Chat", subcategory = "Discord")
    var showInOfficerChat = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "is regex loaded", category = "GEXP", hidden = true)
    var isRegexLoaded = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Show Update Notification", description = "Show a notification when you start Minecraft informing you of new updates.", category = "General")
    var showUpdateNotification = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Automatically get API Key", description = "Automatically get the API Key when /api new is sent.", category = "General")
    var autoGetAPI = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Highlight Name", description = "Highlight your name.", category = "Chat")
    var highlightName = false

    @kotlin.jvm.JvmField
    @Property(
        type = PropertyType.SELECTOR,
        name = "Text Color",
        description = "Change the text color for the highlight.",
        category = "Chat",
        options = ["Black", "Dark Blue", "Dark Green", "Dark Aqua", "Dark Red", "Dark Purple", "Gold", "Gray", "Dark Gray", "Blue", "Green", "Aqua", "Red", "Light Purple", "Yellow", "White"]
    )
    var textColor = 0

    @JvmField
    @Property(
        type = PropertyType.SWITCH,
        name = "Sound Boost",
        description = "Make the volume of sounds that are important for PVP louder.",
        category = "Sound",
    )
    var soundBoost = false

    @JvmField
    @Property(
        type = PropertyType.NUMBER,
        name = "Sound Multiplier",
        description = "How much louder the sound is. There is a volume limit, so it doesn't break your ears.",
        category = "Sound",
        min = 1,
        max = 400
    )
    var soundMultiplier = 10

    @JvmField
    @Property(
        type = PropertyType.SWITCH,
        name = "Bossbar Customization",
        description = "Toggle Bossbar customization",
        category = "Bossbar"
    )
    var bossBarCustomization = true

    @JvmField
    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Bossbar",
        description = "Toggle the bossbar.",
        category = "Bossbar"
    )
    var bossBar = true

    @JvmField
    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Shadow",
        description = "Toggle the text shadow for the bossbar.",
        category = "Bossbar"
    )
    var bossBarShadow = true

    @JvmField
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
    @Suppress("unused")
    fun openBossHealthGui() {
        GuiHelper.open(BossHealthGui)
    }

    @JvmField
    @Property(
        type = PropertyType.NUMBER,
        name = "Bossbar X",
        description = "X",
        category = "Bossbar",
        hidden = true
    )
    var bossBarX : Int = 0

    @JvmField
    @Property(
        type = PropertyType.NUMBER,
        name = "Bossbar Y",
        description = "Y",
        category = "Bossbar",
        hidden = true
    )
    var bossBarY : Int = 12

    @JvmField
    @Property(
        type = PropertyType.SWITCH,
        name = "first launch",
        description = "a",
        category = "General",
        hidden = true
    )
    var firstLaunch = true


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
            "Chat",
            "Configure chat-related features in Wyvtils."
        )
        setCategoryDescription(
            "GEXP",
            "Configure GEXP-related features in Wyvtils."
        )
        setCategoryDescription(
            "Discord",
            "Configure discord-related features in Wyvtils."
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
            ChatListener.changeTextColor = true
        }
        addDependency("textColor", "highlightName")
        addDependency("autoGetGEXP", "isRegexLoaded")
        addDependency("soundMultiplier", "soundBoost")
        addDependency("showInPartyChat", "removeDiscordInvites")
        addDependency("showInGuildChat", "removeDiscordInvites")
        addDependency("showInOfficerChat", "removeDiscordInvites")
        addDependency("bossBar", "bossBarCustomization")
        addDependency("bossBarShadow", "bossBarCustomization")
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
