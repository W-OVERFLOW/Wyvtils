package net.wyvest.wyvtilities

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.wyvtilities.commands.WyvtilsCommands
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.APIUtil
import net.wyvest.wyvtilities.utils.Notifications
import net.wyvest.wyvtilities.utils.Utils
import java.util.concurrent.atomic.AtomicBoolean


@Mod(modid = Wyvtilities.MODID,
    name = Wyvtilities.MOD_NAME,
    version = Wyvtilities.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true)
class Wyvtilities {
    companion object {
        const val MODID = "wyvtilities"
        const val MOD_NAME = "Wyvtilities"
        const val VERSION = "0.1.2"

        @JvmStatic
        val mc: Minecraft
            get() = Minecraft.getMinecraft()

        lateinit var config: WyvtilsConfig

        @JvmField
        var displayScreen: GuiScreen? = null

        fun sendMessage(message: String?) {
            mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(EnumChatFormatting.DARK_PURPLE.toString() + "[Wyvtilities] " + message))
        }
    }
    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        config = WyvtilsConfig
        config.preload()
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(Notifications)
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

    @SubscribeEvent
    fun onMessageReceived(event: ClientChatReceivedEvent) {
        /*/
            Adapted from Moulberry's NotEnoughUpdates, under the Attribution-NonCommercial 3.0 license.
            https://github.com/Moulberry/NotEnoughUpdates
         */
        val unformattedText = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.unformattedText)
        if (unformattedText.startsWith("Your new API key is ")) {
            val tempApiKey = unformattedText.substring("Your new API key is ".length)
            val shouldReturn = AtomicBoolean(false)
            Thread {
                if (!APIUtil.getJSONResponse("https://api.hypixel.net/key?key=$tempApiKey").get("success")
                        .asBoolean
                ) {
                    Utils.checkForHypixel()
                    if (Utils.isOnHypixel) {
                        sendMessage(EnumChatFormatting.RED.toString() + "You are not running this command on Hypixel! This mod needs an Hypixel API key!")
                    }
                    shouldReturn.set(true)
                }
            }.run()
            if (shouldReturn.get()) return
            WyvtilsConfig.apiKey = unformattedText.substring("Your new API key is ".length)
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
            sendMessage(EnumChatFormatting.GREEN.toString() + "Your API Key has been automatically configured.")
        }
    }
}