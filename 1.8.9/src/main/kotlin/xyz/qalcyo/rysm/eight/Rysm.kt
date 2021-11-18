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

import gg.essential.api.EssentialAPI
import gg.essential.lib.kbrewster.eventbus.Subscribe
import gg.essential.universal.UDesktop
import gg.essential.universal.UResolution
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.LoaderState.ModState
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion
import org.lwjgl.input.Keyboard
import xyz.qalcyo.mango.Multithreading
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.requisite.core.keybinds.KeyBinds
import xyz.qalcyo.rysm.core.RysmCore
import xyz.qalcyo.rysm.core.RysmCore.eventBus
import xyz.qalcyo.rysm.core.RysmInfo
import xyz.qalcyo.rysm.core.config.RysmConfig
import xyz.qalcyo.rysm.core.listener.events.BossBarResetEvent
import xyz.qalcyo.rysm.core.listener.events.ChatRefreshEvent
import xyz.qalcyo.rysm.core.listener.events.Gui
import xyz.qalcyo.rysm.core.listener.events.RenderGuiEvent
import xyz.qalcyo.rysm.core.utils.MinecraftVersions
import xyz.qalcyo.rysm.core.utils.Updater
import xyz.qalcyo.rysm.eight.commands.RysmCommand
import xyz.qalcyo.rysm.eight.gui.ActionBarGui
import xyz.qalcyo.rysm.eight.gui.BossHealthGui
import xyz.qalcyo.rysm.eight.gui.DownloadGui
import xyz.qalcyo.rysm.eight.gui.SidebarGui
import xyz.qalcyo.rysm.eight.hooks.GuiScreenResourcePacksHook
import xyz.qalcyo.rysm.eight.mixin.gui.GuiNewChatAccessor
import xyz.qalcyo.rysm.eight.mixin.hud.GuiIngameAccessor
import java.io.File
import java.net.URI

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
    var isNewToggleChat = false
    var packY: Int? = null
    var packBottom: Int? = null

    val mc: Minecraft
        get() = Minecraft.getMinecraft()

    /**
     * Handles the pre-initialization of the mod.
     */
    @Mod.EventHandler
    fun onPreInit(e: FMLPreInitializationEvent) {
        RysmCore.modDir = File(File(File(Minecraft.getMinecraft().mcDataDir, "Qalcyo"), RysmInfo.NAME), "1.8.9")
        if (!RysmCore.modDir.exists()) RysmCore.modDir.mkdirs()
        RysmCore.jarFile = e.sourceFile
    }

    /**
     * Handles the initialization of the mod.
     */
    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        RysmCore.onInitialization(MinecraftVersions.EIGHT)
        RysmCommand.register()
        eventBus.register(this)
        EVENT_BUS.register(this)
        EVENT_BUS.register(GuiScreenResourcePacksHook)
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

    /**
     * Sets some variables which can only be set accurately
     * when run during or after the post-initialization.
     */
    @Mod.EventHandler
    fun onPostInit(e: FMLPostInitializationEvent) {
        isSkytils = Loader.isModLoaded("skytils")
        isNewToggleChat = run {
            for (mod in Loader.instance().activeModList) {
                if (mod.modId == "togglechatmod") {
                    return@run DefaultArtifactVersion(mod.version) > DefaultArtifactVersion("3.1.1") && Loader.instance().getModState(mod) != ModState.DISABLED
                }
            }
            return@run false
        }
    }

    /**
     * Handles the ingame Updater.
     */
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

    @Subscribe
    fun onRenderGui(e: RenderGuiEvent) {
        when (e.gui) {
            Gui.BOSSBAR -> EssentialAPI.getGuiUtil().openScreen(BossHealthGui())
            Gui.ACTIONBAR -> EssentialAPI.getGuiUtil().openScreen(ActionBarGui())
            Gui.SIDEBAR -> EssentialAPI.getGuiUtil().openScreen(SidebarGui())
        }
    }

    @Subscribe
    fun onBossBarReset(e: BossBarResetEvent) {
        EssentialAPI.getGuiUtil().openScreen(null)
        RysmConfig.bossBarX = (UResolution.scaledWidth / 2)
        RysmConfig.bossBarY = 12
        RysmConfig.markDirty()
        RysmConfig.writeData()
        EssentialAPI.getGuiUtil().openScreen(RysmConfig.gui())
    }

}