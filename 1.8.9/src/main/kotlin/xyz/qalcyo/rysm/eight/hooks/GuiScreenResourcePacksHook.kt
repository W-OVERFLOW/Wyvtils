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

package xyz.qalcyo.rysm.eight.hooks

import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiScreenResourcePacks
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.resources.ResourcePackListEntry
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import xyz.qalcyo.rysm.eight.Rysm.mc
import java.io.File
import java.io.FileFilter

var originalPackSize: Int? = null
var inputField: GuiTextField? = null
var sr: ScaledResolution? = null
var prevText = ""
var parentScreen: GuiScreen? = null
val resourcePackFilter = FileFilter { p_accept_1_ ->
    val zipCheck = p_accept_1_.isFile && p_accept_1_.name.endsWith(".zip")
    val fileCheck = p_accept_1_.isDirectory && (File(p_accept_1_, "pack.mcmeta").isFile || File(p_accept_1_, "folder.json").isFile)
    return@FileFilter zipCheck || fileCheck
}

fun addInputField() {
    sr = ScaledResolution(Minecraft.getMinecraft())
    inputField = GuiTextField(
        694209,
        Minecraft.getMinecraft().fontRendererObj,
        sr!!.scaledWidth * 4 / 5 - 1,
        sr!!.scaledHeight - 13,
        sr!!.scaledWidth / 5,
        12
    )
    inputField!!.maxStringLength = 100
    inputField!!.enableBackgroundDrawing = true
    inputField!!.isFocused = false
    inputField!!.text = ""
    inputField!!.setCanLoseFocus(true)
    prevText = ""
}


fun filterPacks(list: List<ResourcePackListEntry?>?, text: String?): List<ResourcePackListEntry?>? {
    if (text.isNullOrBlank() || list == null) return list
    return list.filter {
        it != null && UTextComponent.stripFormatting(it.func_148312_b()).lowercase().contains(text.lowercase())
    }
}

object GuiScreenResourcePacksHook {

    private val packFolder = File(mc.mcDataDir, "resourcepacks")

    @SubscribeEvent(priority = EventPriority.LOWEST) // needs to be lowest to make requisite open the screen at the next tick
    fun onTick(e: TickEvent.ClientTickEvent) {
        if (e.phase == TickEvent.Phase.START) {
            if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen is GuiScreenResourcePacks && inputField != null) {
                while (Keyboard.next()) {
                    if (Keyboard.getEventKeyState() && !Keyboard.isRepeatEvent()) inputField!!.textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey())
                }
                /*/
                if (packFolder.exists() && packFolder.isDirectory && originalPackSize != null) {
                    val array = packFolder.list()
                    if (array != null && array.size != originalPackSize) {
                        originalPackSize = array.size
                        println(originalPackSize)
                        println(array.size)
                        Requisite.getInstance().guiHelper.open(GuiScreenResourcePacks(parentScreen))
                    }
                }

                 */
            }
        }
    }
}