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

package xyz.qalcyo.rysm.eight

import gg.essential.lib.kbrewster.eventbus.Subscribe
import gg.essential.universal.UDesktop
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiUtilRenderComponents
import net.minecraft.util.IChatComponent
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import skytils.skytilsmod.features.impl.handlers.ChatTabs
import xyz.qalcyo.mango.Multithreading
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.requisite.core.keybinds.KeyBinds
import xyz.qalcyo.rysm.core.RysmCore
import xyz.qalcyo.rysm.core.RysmCore.eventBus
import xyz.qalcyo.rysm.core.RysmInfo
import xyz.qalcyo.rysm.core.config.RysmConfig
import xyz.qalcyo.rysm.core.listener.events.ChatRefreshEvent
import xyz.qalcyo.rysm.core.listener.events.MessageRenderEvent
import xyz.qalcyo.rysm.core.utils.MinecraftVersions
import xyz.qalcyo.rysm.core.utils.Updater
import xyz.qalcyo.rysm.eight.commands.RysmCommand
import xyz.qalcyo.rysm.eight.gui.DownloadGui
import xyz.qalcyo.rysm.eight.mixin.GuiIngameAccessor
import xyz.qalcyo.rysm.eight.mixin.GuiNewChatAccessor
import java.io.File
import java.net.URI
import java.util.*

@Mod(
    modid = RysmInfo.ID,
    name = RysmInfo.NAME,
    version = RysmInfo.VER,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Rysm {

    var isSkytils = false

    val mc: Minecraft
        get() = Minecraft.getMinecraft()

    @Mod.EventHandler
    fun onPreInit(e: FMLPreInitializationEvent) {
        RysmCore.modDir =
            File(File(File(File(Minecraft.getMinecraft().mcDataDir, "config"), "Qalcyo"), RysmInfo.NAME), "1.8.9")
        if (!RysmCore.modDir.exists()) RysmCore.modDir.mkdirs()
        RysmCore.jarFile = e.sourceFile
    }

    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        RysmCore.onInitialization(MinecraftVersions.EIGHT)
        RysmCommand.register()
        eventBus.register(this)
        EVENT_BUS.register(this)
        Requisite.getInstance().keyBindRegistry.register(
            KeyBinds.from(
                "Clear Title",
                RysmInfo.NAME,
                Keyboard.KEY_NONE
            ) {
                (mc.ingameGUI as GuiIngameAccessor).displayedTitle = ""
                (mc.ingameGUI as GuiIngameAccessor).setDisplayedSubTitle("")
            })
    }

    @Mod.EventHandler
    fun onPostInit(e: FMLPostInitializationEvent) {
        isSkytils = Loader.isModLoaded("skytils")
    }

    @Mod.EventHandler
    fun onLoadComplete(e: FMLLoadCompleteEvent) {
        Multithreading.runAsync {
            Updater.updateFuture!!.get()
            if (Updater.shouldShowNotification) {
                Requisite.getInstance().notifications
                    .push(
                        "Mod Update",
                        "${RysmInfo.NAME} ${Updater.latestTag} is available!\nClick here to download it!"
                    ) {
                        Requisite.getInstance().guiHelper.open(DownloadGui())
                    }
                Updater.shouldShowNotification = false
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (RysmConfig.firstTime && mc.theWorld != null) {
            Requisite.getInstance().notifications.push(
                "Rysm",
                "Hello! As this is your first time using this mod, type in /rysm in the chat to configure the many features in Rysm!"
            )
            RysmConfig.firstTime = false
            RysmConfig.markDirty()
            RysmConfig.writeData()
        }
    }

    @Subscribe
    fun onChatRefresh(e: ChatRefreshEvent) {
        val chat = Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatAccessor
        try {
            Minecraft.getMinecraft().ingameGUI.chatGUI.refreshChat()
        } catch (e: Exception) {
            e.printStackTrace()
            Requisite.getInstance().notifications.push(
                RysmInfo.NAME,
                "There was a critical error while trying to refresh the chat. Please go to inv.wtf/qalcyo or click on this notification to fix this issue."
            ) {
                UDesktop.browse(URI.create("https://inv.wtf/qalcyo"))
            }
            chat.drawnChatLines.clear()
            Minecraft.getMinecraft().ingameGUI.chatGUI.resetScroll()
            for (line in chat.chatLines.asReversed()) {
                if (line?.chatComponent == null) continue
                chat.invokeSetChatLine(
                    line.chatComponent,
                    line.chatLineID,
                    line.updatedCounter,
                    true
                )
            }
        }
    }

    fun handleChatSent(p_178908_0_: IChatComponent, p_178908_1_: Int, p_178908_2_: FontRenderer, p_178908_3_: Boolean, p_178908_4_: Boolean): List<IChatComponent> {
        if (isSkytils) {
            if (!ChatTabs.shouldAllow(p_178908_0_)) return Collections.emptyList()
        }
        val event = MessageRenderEvent(p_178908_0_.unformattedText, false)
        eventBus.post(event)
        return if (event.cancelled) emptyList() else GuiUtilRenderComponents.splitText(
            p_178908_0_,
            p_178908_1_,
            p_178908_2_,
            p_178908_3_,
            p_178908_4_
        )
    }
}