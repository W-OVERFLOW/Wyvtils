package net.wyvest.wyvtilities

import com.google.gson.JsonArray
import gg.essential.universal.UDesktop
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.wyvtilities.commands.WyvtilsCommands
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.listener.ChatListener
import net.wyvest.wyvtilities.utils.APIUtil
import net.wyvest.wyvtilities.utils.Notifications
import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor


@Mod(modid = Wyvtilities.MODID,
    name = Wyvtilities.MOD_NAME,
    version = Wyvtilities.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true)
class Wyvtilities {
    companion object {
        const val MODID = "wyvtilities"
        const val MOD_NAME = "Wyvtilities"
        const val VERSION = "0.2.0"

        @JvmStatic
        val mc: Minecraft
            get() = Minecraft.getMinecraft()

        lateinit var config: WyvtilsConfig
        lateinit var autoGGRegex : JsonArray
        @JvmField
        var displayScreen: GuiScreen? = null

        fun sendMessage(message: String?) {
            mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(EnumChatFormatting.DARK_PURPLE.toString() + "[Wyvtilities] " + message))
        }
        lateinit var latestVersion : String

        @JvmField
        val threadPool = Executors.newFixedThreadPool(10) as ThreadPoolExecutor
    }

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        try {
            latestVersion = APIUtil.getJSONResponse("https://wyvest.net/wyvtilities.json").get("latest").asString
        } catch (e : Exception) {
            e.printStackTrace()
        }
        config = WyvtilsConfig
        config.preload()
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(Notifications)
        MinecraftForge.EVENT_BUS.register(ChatListener)
        ClientCommandHandler.instance.registerCommand(WyvtilsCommands())
        autoGGRegex = APIUtil.getJSONResponse("https://wyvest.net/wyvtilities.json").getAsJsonArray("triggers")
    }

    @Mod.EventHandler
    fun onPostInitialization(event: FMLPostInitializationEvent) {
        if (VERSION != latestVersion) {
            Notifications.push("Wyvtilities", "Wyvtilities is outdated! Update to the latest version by clicking here!", this::openDownloadURI)
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

        if (displayScreen != null) {
            mc.displayGuiScreen(displayScreen)
            displayScreen = null
        }
    }

    private fun openDownloadURI() {
        UDesktop.browse(URI("https://github.com/wyvest/Wyvtilities/releases/latest"))
    }

}