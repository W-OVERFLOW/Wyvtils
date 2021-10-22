/*
 * Wyvtils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Wyvtils
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

package xyz.qalcyo.wyvtils.eight

import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import gg.essential.lib.kbrewster.eventbus.Subscribe
import gg.essential.universal.UDesktop
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import xyz.qalcyo.wyvtils.core.WyvtilsCore
import xyz.qalcyo.wyvtils.core.WyvtilsInfo
import xyz.qalcyo.wyvtils.core.listener.events.ChatRefreshEvent
import xyz.qalcyo.wyvtils.core.utils.MinecraftVersions
import xyz.qalcyo.wyvtils.core.utils.Updater
import xyz.qalcyo.wyvtils.eight.commands.WyvtilsCommand
import xyz.qalcyo.wyvtils.eight.gui.DownloadGui
import xyz.qalcyo.wyvtils.eight.mixin.GuiNewChatAccessor
import java.io.File
import java.net.URI

@Mod(
    modid = WyvtilsInfo.ID,
    name = WyvtilsInfo.NAME,
    version = WyvtilsInfo.VER,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object Wyvtils {

    val mc: Minecraft
        get() = Minecraft.getMinecraft()

    @Mod.EventHandler
    fun onPreInit(e: FMLPreInitializationEvent) {
        WyvtilsCore.modDir =
            File(File(File(File(Minecraft.getMinecraft().mcDataDir, "config"), "Qalcyo"), WyvtilsInfo.NAME), "1.8.9")
        if (!WyvtilsCore.modDir!!.exists()) WyvtilsCore.modDir!!.mkdirs()
        WyvtilsCore.jarFile = e.sourceFile
    }

    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        WyvtilsCore.onInitialization(MinecraftVersions.EIGHT)
        WyvtilsCommand.register()
        WyvtilsCore.eventBus.register(this)
        //Requisite.getInstance().keyBindRegistry.register(KeyBinds.from("Clear Title", WyvtilsInfo.NAME, Keyboard.KEY_NONE) {
        //(mc.ingameGUI as GuiIngameAccessor).displayedTitle = ""
        //(mc.ingameGUI as GuiIngameAccessor).setDisplayedSubTitle("")
        //})
    }

    @Mod.EventHandler
    fun onPostInit(e: FMLPostInitializationEvent) {
        WyvtilsCore.isPatcherLoaded = Loader.isModLoaded("patcher")
    }

    @Mod.EventHandler
    fun onLoadComplete(e: FMLLoadCompleteEvent) {
        Multithreading.runAsync {
            Updater.updateFuture!!.get()
            if (Updater.shouldShowNotification) {
                EssentialAPI.getNotifications()
                    .push(
                        "Mod Update",
                        "${WyvtilsInfo.NAME} ${Updater.latestTag} is available!\nClick here to download it!",
                        5f
                    ) {
                        EssentialAPI.getGuiUtil().openScreen(DownloadGui())
                    }
                Updater.shouldShowNotification = false
            }
        }
    }

    @Subscribe
    fun onChatRefresh(e: ChatRefreshEvent) {
        val chat = Minecraft.getMinecraft().ingameGUI.chatGUI as GuiNewChatAccessor
        try {
            Minecraft.getMinecraft().ingameGUI.chatGUI.refreshChat()
        } catch (e: Exception) {
            e.printStackTrace()
            EssentialAPI.getNotifications().push(
                WyvtilsInfo.NAME,
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
}