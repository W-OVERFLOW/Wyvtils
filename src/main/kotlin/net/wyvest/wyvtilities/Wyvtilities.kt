package net.wyvest.wyvtilities

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.wyvtilities.commands.WyvtilsCommands
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.listener.ChatListener
import net.wyvest.wyvtilities.utils.Notifications
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

        @JvmField
        var displayScreen: GuiScreen? = null

        fun sendMessage(message: String?) {
            mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(EnumChatFormatting.DARK_PURPLE.toString() + "[Wyvtilities] " + message))
        }

        @JvmField
        val threadPool = Executors.newFixedThreadPool(10) as ThreadPoolExecutor
    }

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        config = WyvtilsConfig
        config.preload()
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(Notifications)
        MinecraftForge.EVENT_BUS.register(ChatListener)
        ClientCommandHandler.instance.registerCommand(WyvtilsCommands())
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

        if (displayScreen != null) {
            mc.displayGuiScreen(displayScreen)
            displayScreen = null
        }
    }
}