package net.wyvest.wyvtilities.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import java.io.File

object WyvtilsConfig : Vigilant(File("./config/wyvtilities.toml"), "Wyvtilities", sortingBehavior = ConfigSorting)  {

    @Property(type = PropertyType.PARAGRAPH, name = "Info", description = "You are using Wyvtilities version 0.0.1, made by Wyvest.", category = "Information")
    var paragraph = ""

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.SWITCH, name = "Toggle Mod", description = "Toggle the mod.", category = "General")
    var modToggled = true

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.TEXT, name = "API Key", description = "The API key, used for some features.", category = "API")
    var apiKey = ""

    @Property(type = PropertyType.PARAGRAPH, name = "TGMDevelopment", description = "Utilities", category = "Information", subcategory = "Credits")
    var credits1 = ""

    @Property(type = PropertyType.PARAGRAPH, name = "Skytils", description = "Even more utilities", category = "Information", subcategory = "Credits")
    var credits2 = ""

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
    }

    private object ConfigSorting : SortingBehavior() {
        override fun getCategoryComparator(): Comparator<in Category> {
            return Comparator { o1, o2 ->
                if (o1.name == "General") return@Comparator -1
                if (o2.name == "General") return@Comparator 1
                else compareValuesBy(o2,o1) {
                    it.name
                }
            }
        }
    }

}