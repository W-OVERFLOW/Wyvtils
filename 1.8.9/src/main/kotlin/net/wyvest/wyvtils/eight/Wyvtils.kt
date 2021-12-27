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

package cc.woverflow.wyvtils.eight

import gg.essential.api.EssentialAPI
import gg.essential.lib.kbrewster.eventbus.Subscribe
import gg.essential.universal.UDesktop
import gg.essential.universal.UResolution
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import cc.woverflow.wyvtils.core.WyvtilsCore
import cc.woverflow.wyvtils.core.WyvtilsCore.eventBus
import cc.woverflow.wyvtils.core.WyvtilsInfo
import cc.woverflow.wyvtils.core.config.WyvtilsConfig
import cc.woverflow.wyvtils.core.listener.events.*
import cc.woverflow.wyvtils.core.utils.MinecraftVersions
import cc.woverflow.wyvtils.eight.commands.WyvtilsCommand
import cc.woverflow.wyvtils.eight.gui.ActionBarGui
import cc.woverflow.wyvtils.eight.gui.BossHealthGui
import cc.woverflow.wyvtils.eight.gui.DownloadGui
import cc.woverflow.wyvtils.eight.gui.SidebarGui
import cc.woverflow.wyvtils.eight.hooks.GuiScreenResourcePacksHook
import cc.woverflow.wyvtils.eight.mixin.gui.GuiNewChatAccessor
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

    var isSkytils = false
    var packY: Int? = null
    var packBottom: Int? = null

    val mc: Minecraft
        get() = Minecraft.getMinecraft()

    /**
     * Handles the pre-initialization of the mod.
     */
    @Mod.EventHandler
    fun onPreInit(e: FMLPreInitializationEvent) {
        WyvtilsCore.modDir =
            File(File(File(Minecraft.getMinecraft().mcDataDir, "W-OVERFLOW"), WyvtilsInfo.NAME), "1.8.9")
        if (!WyvtilsCore.modDir.exists()) WyvtilsCore.modDir.mkdirs()
        WyvtilsCore.jarFile = e.sourceFile
    }

    /**
     * Handles the initialization of the mod.
     */
    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        eventBus.register(this)
        WyvtilsCore.onInitialization(MinecraftVersions.EIGHT)
        WyvtilsCommand.register()
        EVENT_BUS.register(this)
        EVENT_BUS.register(GuiScreenResourcePacksHook)
    }

    /**
     * Sets some variables which can only be set accurately
     * when run during or after the post-initialization.
     */
    @Mod.EventHandler
    fun onPostInit(e: FMLPostInitializationEvent) {
        isSkytils = Loader.isModLoaded("skytils")
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (WyvtilsConfig.firstTime && mc.theWorld != null) {
            EssentialAPI.getNotifications().push(
                "Wyvtils",
                "Hello! As this is your first time using this mod, type in /wyvtils in the chat to configure the many features in Wyvtils!"
            )
            WyvtilsConfig.firstTime = false
            WyvtilsConfig.markDirty()
            WyvtilsConfig.writeData()
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
                "There was a critical error while trying to refresh the chat. Please go to inv.wtf/W-OVERFLOW or click on this notification to fix this issue."
            ) {
                UDesktop.browse(URI.create("https://inv.wtf/w-overflow"))
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

    @Subscribe
    fun onNotification(e: UpdateEvent) {
        EssentialAPI.getNotifications()
            .push(
                "Mod Update",
                "${WyvtilsInfo.NAME} ${e.version} is available!\nClick here to download it!",
                5f
            ) {
                EssentialAPI.getGuiUtil().openScreen(DownloadGui())
            }
    }

    @Subscribe
    fun onRenderGui(e: RenderGuiEvent) {
        when (e.gui) {
            Gui.BOSSBAR -> EssentialAPI.getGuiUtil().openScreen(BossHealthGui())
            Gui.ACTIONBAR -> EssentialAPI.getGuiUtil().openScreen(ActionBarGui())
            Gui.SIDEBAR -> EssentialAPI.getGuiUtil().openScreen(SidebarGui())
            Gui.UPDATER -> EssentialAPI.getGuiUtil().openScreen(DownloadGui())
        }
    }

    @Subscribe
    fun onBossBarReset(e: BossBarResetEvent) {
        EssentialAPI.getGuiUtil().openScreen(null)
        WyvtilsConfig.bossBarX = (UResolution.scaledWidth / 2)
        WyvtilsConfig.bossBarY = 12
        WyvtilsConfig.markDirty()
        WyvtilsConfig.writeData()
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

}