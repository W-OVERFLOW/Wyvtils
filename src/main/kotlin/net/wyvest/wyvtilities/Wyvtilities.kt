package net.wyvest.wyvtilities

import gg.essential.universal.ChatColor
import gg.essential.universal.UDesktop
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion
import net.wyvest.wyvtilities.commands.WyvtilsCommand
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.listeners.Listener
import net.wyvest.wyvtilities.utils.equalsAny
import net.wyvest.wyvtilities.utils.startsWithAny
import xyz.matthewtgm.json.entities.JsonArray
import xyz.matthewtgm.json.util.JsonApiHelper
import xyz.matthewtgm.tgmlib.commands.CommandManager
import xyz.matthewtgm.tgmlib.launchwrapper.TGMLibLaunchwrapper
import xyz.matthewtgm.tgmlib.util.ChatHelper
import xyz.matthewtgm.tgmlib.util.ForgeHelper
import xyz.matthewtgm.tgmlib.util.Multithreading
import xyz.matthewtgm.tgmlib.util.Notifications
import java.net.URI


@Mod(modid = Wyvtilities.MODID,
    name = Wyvtilities.MOD_NAME,
    version = Wyvtilities.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "net.wyvest.wyvtilities.adapter.KotlinLanguageAdapter")
object Wyvtilities {
    var isRegexLoaded: Boolean = false
    const val MODID = "wyvtilities"
    const val MOD_NAME = "Wyvtilities"
    const val VERSION = "0.6.1"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()

    lateinit var autoGGRegex : JsonArray

    @JvmField
    var isConfigInitialized = false

    fun sendMessage(message: String?) {
        ChatHelper.sendMessage(EnumChatFormatting.DARK_PURPLE.toString() + "[Wyvtilities] ", message)
    }

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        TGMLibLaunchwrapper.initialize(Minecraft.getMinecraft().mcDataDir)
        WyvtilsConfig.preload()
        isConfigInitialized = true
        if (WyvtilsConfig.highlightName) {
            Listener.color = when (WyvtilsConfig.textColor) {
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
                15 -> ChatColor.WHITE.toString()
                else -> ""
            }
        }
        ForgeHelper.registerEventListeners(this, Listener)
        CommandManager.register(WyvtilsCommand.getInstance().javaClass)
        Multithreading.runAsync {
            try {
                autoGGRegex = JsonApiHelper.getJsonObject("https://wyvest.net/wyvtilities.json", true).getAsArray("triggers")
                isRegexLoaded = true
            } catch (e : Exception) {
                e.printStackTrace()
                isRegexLoaded = false
                Notifications.push("Wyvtilities", "Wyvtilities failed to get regexes required for the Auto Get GEXP feature!")
            }
        }
    }

    /**
     * Adapted from SimpleToggleSprint under AGPLv3
     * https://github.com/My-Name-Is-Jeff/SimpleToggleSprint/blob/1.8.9/LICENSE
     */
    @Mod.EventHandler
    fun onFMLLoad(event : FMLLoadCompleteEvent) {
        if (ForgeHelper.isModLoaded("bossbar_customizer")) {
            WyvtilsConfig.bossBarCustomization = false
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
            Notifications.push("Wyvtilities", "Bossbar Customizer (the mod) has been detected, and so the Wyvtils Bossbar related features have been disabled.")
        }
        CoroutineScope(Dispatchers.IO + CoroutineName("Wyvtilities-UpdateChecker")).launch {
            val latestRelease = JsonApiHelper.getJsonObject("https://api.github.com/repos/Wyvest/Wyvtilities/releases/latest")
            val latestTag = latestRelease.get("tag_name").asString
            val currentTag = VERSION

            val currentVersion = DefaultArtifactVersion(currentTag.substringBefore("-"))
            val latestVersion = DefaultArtifactVersion(latestTag.substringAfter("v").substringBefore("-"))

            var updateUrl: String? = null
            if ((currentTag.contains("BETA") && currentVersion >= latestVersion)) {
                return@launch
            } else if (currentVersion < latestVersion) {
                updateUrl = latestRelease.getAsArray("assets")[0].asJsonObject["browser_download_url"].asString
            }
            if (updateUrl != null) {
                Notifications.push("Mod Update", "Wyvtilities $latestTag is available!\nClick to open!", Runnable {
                    UDesktop.browse(URI("https://github.com/wyvest/Wyvtilities/releases/latest"))
                })
            }
        }
    }

    fun checkSound(name : String) : Boolean {
        if (name.equalsAny(
                "random.successful_hit",
                "random.break",
                "random.drink",
                "random.eat",
                "random.bow",
                "random.bowhit",
                "mob.ghast.fireball",
                "mob.ghast.charge"
            ) || (name.startsWithAny("dig.", "step.", "game.player.") && name != "game.player.hurt")
        ) {
            return true
        }
        return false
    }

}

/*/
        private const val PARTY_TALK = "Party > (.*)"
    private const val PARTY_TALK_HYTILS = "P > (.*)"
    private const val PARTY_TALK_NO_PARTY = "You are not in a party right now\\."
    private const val PARTY_TALK_MUTED = "This party is currently muted\\."
    private const val PARTY_INVITE =
        "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) invited (?<tags1>(?:\\[[^]]+] ?)*)(?<senderUsername1>[^ ]{1,16}) to the party! They have 60 seconds to accept\\."
    private const val PARTY_OTHER_LEAVE = "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) has left the party\\."
    private const val PARTY_OTHER_JOIN = "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) joined the party\\."
    private const val PARTY_LEAVE = "You left the party\\."
    private const val PARTY_JOIN = "You have joined (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16})'s party!"
    private const val PARTY_DISBANDED =
        "The party was disbanded because all invites expired and the party was empty"
    private const val PARTY_INVITE_NOT_ONLINE = "You cannot invite that player since they're not online\\."

    private const val PARTY_HOUSING_WARP =
        "The party leader, (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}), warped you to (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername1>[^ ]{1,16})'s house\\."

    private const val PARTY_SB_WARP = "SkyBlock Party Warp \\([0-9]+ players?\\)"
    private const val PARTY_WARPED =
        ". (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) warped to your server"

    private const val PARTY_SUMMONED =
        "You summoned (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) to your server\\."

    private const val PARTY_WARP_HOUSING =
        "The party leader, (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}), warped you to their house\\."

    private const val PARTY_PRIVATE_ON = "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) enabled Private Game"

    private const val PARTY_PRIVATE_OFF = "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) disabled Private Game"

    private const val PARTY_MUTE_ON = "The party is now muted\\."

    private const val PARTY_MUTE_OFF = "The party is no longer muted\\."

    private const val PARTY_NOOFFLINE = "There are no offline players to remove\\."

    private const val PARTY_KICK = "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) has been removed from the party\\."

    private const val PARTY_TRANSFER =
        "The party was transferred to (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) by (?<tags>(?:\\[[^]]+] ?)*)(?<sender1Username>[^ ]{1,16})"

    private const val PARTY_PROMOTE =
        "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) has promoted (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername1>[^ ]{1,16}) to Party Leader"

    private const val PARTY_PROMOTE_MODERATOR =
        "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) has promoted (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername1>[^ ]{1,16}) to Party Moderator"
    private const val PARTY_DEMOTE_MODERATOR = "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) is now a Party Moderator"

    private const val PARTY_DEMOTE_MEMBER =
        "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) has demoted (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername1>[^ ]{1,16}) to Party Member"

    private const val PARTY_DEMOTE_SELF = "You can't demote yourself!"

    private const val PARTY_LIST_NUM = "Party Members \\([0-9]+\\)"

    private const val PARTY_LIST_LEADER = "Party Leader: (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16})" //works

    private const val PARTY_LIST_MEMBERS =
        "Party Members: (?:(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) . )+"

    private const val PARTY_LIST_MODS =
        "Party Moderators: (?:(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) . )+" //works

    private const val PARTY_INVITE_EXPIRE =
        "The party invite to (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) has expired" //works

    private const val PARTY_ALLINVITE_OFF = "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) disabled All Invite" //works

    private const val PARTY_ALLINVITE_ON = "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) enabled All Invite" //works

    private const val PARTY_INVITES_OFF = "You cannot invite that player\\." //works

    private const val PARTY_INVITE_NOPERMS = "You are not allowed to invite players\\." //works

    private const val PARTY_DC_LEADER =
        "The party leader, (?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) has disconnected, they have 5 minutes to rejoin before the party is disbanded\\."
    private const val PARTY_DC_OTHER =
        "(?<tags>(?:\\[[^]]+] ?)*)(?<senderUsername>[^ ]{1,16}) has disconnected, they have 5 minutes to rejoin before they are removed from the party\\."
         */