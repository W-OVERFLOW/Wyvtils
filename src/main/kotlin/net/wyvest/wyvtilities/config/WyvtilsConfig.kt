package net.wyvest.wyvtilities.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import net.wyvest.wyvtilities.listener.ChatListener
import java.io.File


object WyvtilsConfig : Vigilant(File("./config/wyvtilities.toml"), "Wyvtilities", sortingBehavior = ConfigSorting)  {

    @Property(type = PropertyType.PARAGRAPH, name = "Info", description = "You are using Wyvtilities version 0.3.3, made by Wyvest.", category = "Information")
    var paragraph = ""

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.TEXT, name = "API Key", description = "The API key, used for some features.", category = "General", protectedText = true)
    var apiKey = ""

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Automatically Check GEXP", description = "Automatically check your GEXP after you win a Hypixel game. Requires an API Key.", category = "GEXP")
    var autoGetGEXP = true

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
        description = "Change the text color for the HUD.",
        category = "Chat",
        options = ["Black", "Dark Blue", "Dark Green", "Dark Aqua", "Dark Red", "Dark Purple", "Gold", "Gray", "Dark Gray", "Blue", "Green", "Aqua", "Red", "Light Purple", "Yellow", "White"]
    )
    var textColor = 0

    @Property(type = PropertyType.PARAGRAPH, name = "TGMDevelopment", description = "Utilities", category = "Information", subcategory = "Credits")
    var credits1 = ""

    @Property(type = PropertyType.PARAGRAPH, name = "Skytils", description = "Even more utilities", category = "Information", subcategory = "Credits")
    var credits2 = ""

    @Property(type = PropertyType.PARAGRAPH, name = "isXander", description = "Notifications", category = "Information", subcategory = "Credits")
    var credits3 = ""

    init {
        initialize()

        setCategoryDescription(
            "General",
            "This category is for configuring general parts of the timer."
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