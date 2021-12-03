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

package net.wyvest.wyvtils.eight.hooks

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreenResourcePacks
import net.wyvest.wyvtils.eight.Wyvtils.mc
import net.wyvest.wyvtils.eight.mixin.gui.GuiScreenResourcePacksAccessor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.*
import java.util.concurrent.Executors


object PackRefresher {
   private lateinit var watchService: WatchService
   private val logger: Logger = LogManager.getLogger("Wyvtils Resource Pack Refresher")

   fun startWatchService() {
      Executors.newSingleThreadExecutor().execute {
         try {
            watchService = FileSystems.getDefault().newWatchService()
            Minecraft.getMinecraft().resourcePackRepository.dirResourcepacks.toPath().register(
               watchService,
               StandardWatchEventKinds.ENTRY_CREATE,
               StandardWatchEventKinds.ENTRY_DELETE,
               StandardWatchEventKinds.ENTRY_MODIFY
            )
            var key: WatchKey
            while (watchService.take().also { key = it } != null) {
               val iterator: Iterator<*> = key.pollEvents().iterator()
               while (iterator.hasNext()) {
                  val event = iterator.next() as WatchEvent<*>
                  if (mc.currentScreen is GuiScreenResourcePacks) {
                     mc.addScheduledTask {
                        logger.info(
                           "Updating for event {} on file {}",
                           event.kind(), event.context()
                        )
                        if (mc.currentScreen is GuiScreenResourcePacks) {
                           Minecraft.getMinecraft()
                              .displayGuiScreen(GuiScreenResourcePacks((mc.currentScreen as GuiScreenResourcePacks as GuiScreenResourcePacksAccessor).parentScreen))
                        }
                     }
                  }
               }
               key.reset()
            }
         } catch (e: Exception) {
            logger.error("An error occurred in the WatchService:")
            e.printStackTrace()
         }
      }
   }
}