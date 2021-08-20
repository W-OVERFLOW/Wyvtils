/*
 * Wyvtilities - Utilities for Hypixel 1.8.9.
 * Copyright (C) 2021 Wyvtilities
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

package net.wyvest.wyvtilities.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.DisplayName
import gg.essential.api.commands.SubCommand
import gg.essential.api.utils.Multithreading
import net.wyvest.wyvtilities.config.WyvtilsConfig
import net.wyvest.wyvtilities.utils.HypixelUtils

@Suppress("unused")
object WyvtilsCommands : Command("wyvtilities", true) {

    override val commandAliases = setOf(Alias("wyvtils"), Alias("wytils"), Alias("wyvtil"), Alias("wytil"), Alias("wyvest")) //dont ask about the wyvest alias

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for Wyvtils")
    fun config() {
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

    @SubCommand("gexp", description = "Gets the GEXP of the player specified")
    fun gexp(@DisplayName("username") username: String?, @DisplayName("type") type: String?) {
        try {
            Multithreading.runAsync {
                if (username != null) {
                    if (type == null) {
                        if (HypixelUtils.getGEXP(username)) {
                            EssentialAPI.getNotifications()
                                .push("Wyvtilities", "$username currently has " + HypixelUtils.gexp + " guild EXP.")
                        } else {
                            EssentialAPI.getNotifications()
                                .push("Wyvtilities", "There was a problem trying to get $username's GEXP.")
                        }
                    } else {
                        if (type == "daily") {
                            if (HypixelUtils.getGEXP(username)) {
                                EssentialAPI.getNotifications()
                                    .push("Wyvtilities", "$username currently has " + HypixelUtils.gexp + " daily guild EXP.")
                            } else {
                                EssentialAPI.getNotifications()
                                    .push("Wyvtilities", "There was a problem trying to get $username's daily GEXP.")
                            }
                        } else if (type == "weekly") {
                            if (HypixelUtils.getWeeklyGEXP(username)) {
                                EssentialAPI.getNotifications()
                                    .push("Wyvtilities", "$username currently has " + HypixelUtils.gexp + " weekly guild EXP.")
                            } else {
                                EssentialAPI.getNotifications()
                                    .push("Wyvtilities", "There was a problem trying to get $username's weekly GEXP.")
                            }
                        } else {
                            EssentialAPI.getNotifications()
                                .push("Wyvtilities", "The type argument was not valid.")
                        }
                    }
                } else {
                    if (HypixelUtils.getGEXP()) {
                        EssentialAPI.getNotifications()
                            .push("Wyvtilities", "You currently have " + HypixelUtils.gexp + " guild EXP.")
                    } else {
                        EssentialAPI.getNotifications()
                            .push("Wyvtilities", "There was a problem trying to get your GEXP.")
                    }
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    @SubCommand("winstreak", description = "Gets the winstreak of the player specified")
    fun winstreak(@DisplayName("username") username: String?, @DisplayName("gamemode") gamemode: String?) {
        try {
            Multithreading.runAsync {
                if (username != null) {
                    if (gamemode != null) {
                        if (HypixelUtils.getWinstreak(username, gamemode)) {
                            EssentialAPI.getNotifications()
                                .push(
                                    "Wyvtilities",
                                    "$username currently has a " + HypixelUtils.winstreak + " winstreak in $gamemode."
                                )
                        } else {
                            EssentialAPI.getNotifications()
                                .push(
                                    "Wyvtilities",
                                    "There was a problem trying to get $username's winstreak in $gamemode."
                                )
                        }
                    } else {
                        if (HypixelUtils.getWinstreak(username)) {
                            EssentialAPI.getNotifications()
                                .push(
                                    "Wyvtilities",
                                    "$username currently has a " + HypixelUtils.winstreak + " winstreak."
                                )
                        } else {
                            EssentialAPI.getNotifications()
                                .push("Wyvtilities", "There was a problem trying to get $username's winstreak.")
                        }
                    }
                } else {
                    if (HypixelUtils.getWinstreak()) {
                        EssentialAPI.getNotifications()
                            .push("Wyvtilities", "You currently have a " + HypixelUtils.winstreak + " winstreak.")
                    } else {
                        EssentialAPI.getNotifications()
                            .push("Wyvtilities", "There was a problem trying to get your winstreak.")
                    }
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

}