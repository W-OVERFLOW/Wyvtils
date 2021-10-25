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

package xyz.qalcyo.rysm.commands

import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.DisplayName
import gg.essential.api.commands.SubCommand
import net.minecraft.util.EnumChatFormatting
import xyz.qalcyo.mango.Multithreading
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.rysm.Rysm
import xyz.qalcyo.rysm.config.RysmConfig
import xyz.qalcyo.rysm.utils.HypixelUtils

@Suppress("unused")
object RysmCommands : Command(Rysm.MODID, true) {

    override val commandAliases = setOf(
        Alias("wyvtilities"),
        Alias("wyvtils")
    )

    @DefaultHandler
    fun handle() {
        Requisite.getInstance().guiHelper.open(RysmConfig.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for ${Rysm.MOD_NAME}")
    fun config() {
        Requisite.getInstance().guiHelper.open(RysmConfig.gui())
    }

    @SubCommand("setkey", description = "Sets the API key for ${Rysm.MOD_NAME}.")
    fun setKey(@DisplayName("api key") apiKey: String) {
        Multithreading.runAsync {
            try {
                if (HypixelUtils.isValidKey(apiKey)
                ) {
                    RysmConfig.apiKey = apiKey
                    RysmConfig.markDirty()
                    RysmConfig.writeData()
                    Rysm.sendMessage(EnumChatFormatting.GREEN.toString() + "Saved API key successfully!")
                } else {
                    Rysm.sendMessage(EnumChatFormatting.RED.toString() + "Invalid API key! Please try again.")
                }
            } catch (ex: Throwable) {
                Rysm.sendMessage(EnumChatFormatting.RED.toString() + "Invalid API key! Please try again.")
                ex.printStackTrace()
            }
        }
    }

    @SubCommand("gexp", description = "Gets the GEXP of the player specified")
    fun gexp(@DisplayName("username") username: String?, @DisplayName("type") type: String?) {
        Multithreading.runAsync {
            if (RysmConfig.apiKey.isEmpty() || !HypixelUtils.isValidKey(RysmConfig.apiKey)) {
                Rysm.sendMessage(EnumChatFormatting.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
                return@runAsync
            }
            if (username != null) {
                if (type == null) {
                    if (HypixelUtils.getGEXP(username)) {
                        Requisite.getInstance().notifications
                            .push(Rysm.MOD_NAME, "$username currently has " + HypixelUtils.gexp + " guild EXP.")
                    } else {
                        Requisite.getInstance().notifications
                            .push(Rysm.MOD_NAME, "There was a problem trying to get $username's GEXP.")
                    }
                } else {
                    if (type == "daily") {
                        if (HypixelUtils.getGEXP(username)) {
                            Requisite.getInstance().notifications
                                .push(
                                    Rysm.MOD_NAME,
                                    "$username currently has " + HypixelUtils.gexp + " daily guild EXP."
                                )
                        } else {
                            Requisite.getInstance().notifications
                                .push(Rysm.MOD_NAME, "There was a problem trying to get $username's daily GEXP.")
                        }
                    } else if (type == "weekly") {
                        if (HypixelUtils.getWeeklyGEXP(username)) {
                            Requisite.getInstance().notifications
                                .push(
                                    Rysm.MOD_NAME,
                                    "$username currently has " + HypixelUtils.gexp + " weekly guild EXP."
                                )
                        } else {
                            Requisite.getInstance().notifications
                                .push(Rysm.MOD_NAME, "There was a problem trying to get $username's weekly GEXP.")
                        }
                    } else {
                        Requisite.getInstance().notifications
                            .push(Rysm.MOD_NAME, "The type argument was not valid.")
                    }
                }
            } else {
                if (HypixelUtils.getGEXP()) {
                    Requisite.getInstance().notifications
                        .push(Rysm.MOD_NAME, "You currently have " + HypixelUtils.gexp + " guild EXP.")
                } else {
                    Requisite.getInstance().notifications
                        .push(Rysm.MOD_NAME, "There was a problem trying to get your GEXP.")
                }
            }
        }
    }

    @SubCommand("winstreak", description = "Gets the winstreak of the player specified")
    fun winstreak(@DisplayName("username") username: String?, @DisplayName("gamemode") gamemode: String?) {
        Multithreading.runAsync {
            if (RysmConfig.apiKey.isEmpty() || !HypixelUtils.isValidKey(RysmConfig.apiKey)) {
                Rysm.sendMessage(EnumChatFormatting.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
                return@runAsync
            }
            if (username != null) {
                if (gamemode != null) {
                    if (HypixelUtils.getWinstreak(username, gamemode)) {
                        Requisite.getInstance().notifications
                            .push(
                                Rysm.MOD_NAME,
                                "$username currently has a " + HypixelUtils.winstreak + " winstreak in $gamemode."
                            )
                    } else {
                        Requisite.getInstance().notifications
                            .push(
                                Rysm.MOD_NAME,
                                "There was a problem trying to get $username's winstreak in $gamemode."
                            )
                    }
                } else {
                    if (HypixelUtils.getWinstreak(username)) {
                        Requisite.getInstance().notifications
                            .push(
                                Rysm.MOD_NAME,
                                "$username currently has a " + HypixelUtils.winstreak + " winstreak."
                            )
                    } else {
                        Requisite.getInstance().notifications
                            .push(Rysm.MOD_NAME, "There was a problem trying to get $username's winstreak.")
                    }
                }
            } else {
                if (HypixelUtils.getWinstreak()) {
                    Requisite.getInstance().notifications
                        .push(Rysm.MOD_NAME, "You currently have a " + HypixelUtils.winstreak + " winstreak.")
                } else {
                    Requisite.getInstance().notifications
                        .push(Rysm.MOD_NAME, "There was a problem trying to get your winstreak.")
                }
            }
        }
    }

}