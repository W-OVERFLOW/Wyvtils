/*
 * Qaltils, a utility mod for 1.8.9.
 * Copyright (C) 2021 Qaltils
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

package xyz.qalcyo.qaltils.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.DisplayName
import gg.essential.api.commands.SubCommand
import gg.essential.api.utils.Multithreading
import net.minecraft.util.EnumChatFormatting
import xyz.qalcyo.qaltils.Qaltils
import xyz.qalcyo.qaltils.config.QaltilsConfig
import xyz.qalcyo.qaltils.utils.HypixelUtils

@Suppress("unused")
object QaltilsCommands : Command("qaltils", true) {

    override val commandAliases = setOf(
        Alias("qaltil"),
        Alias("wyvtilities") // will be removed soon
    )

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(QaltilsConfig.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for Qaltils")
    fun config() {
        EssentialAPI.getGuiUtil().openScreen(QaltilsConfig.gui())
    }

    @SubCommand("setkey", description = "Sets the API key for Qaltils.")
    fun setKey(@DisplayName("api key") apiKey: String) {
        Multithreading.runAsync {
            try {
                if (HypixelUtils.isValidKey(apiKey)
                ) {
                    QaltilsConfig.apiKey = apiKey
                    QaltilsConfig.markDirty()
                    QaltilsConfig.writeData()
                    Qaltils.sendMessage(EnumChatFormatting.GREEN.toString() + "Saved API key successfully!")
                } else {
                    Qaltils.sendMessage(EnumChatFormatting.RED.toString() + "Invalid API key! Please try again.")
                }
            } catch (ex: Throwable) {
                Qaltils.sendMessage(EnumChatFormatting.RED.toString() + "Invalid API key! Please try again.")
                ex.printStackTrace()
            }
        }
    }

    @SubCommand("gexp", description = "Gets the GEXP of the player specified")
    fun gexp(@DisplayName("username") username: String?, @DisplayName("type") type: String?) {
        Multithreading.runAsync {
            if (QaltilsConfig.apiKey.isEmpty() || !HypixelUtils.isValidKey(QaltilsConfig.apiKey)) {
                Qaltils.sendMessage(EnumChatFormatting.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
                return@runAsync
            }
            if (username != null) {
                if (type == null) {
                    if (HypixelUtils.getGEXP(username)) {
                        EssentialAPI.getNotifications()
                            .push("Qaltils", "$username currently has " + HypixelUtils.gexp + " guild EXP.")
                    } else {
                        EssentialAPI.getNotifications()
                            .push("Qaltils", "There was a problem trying to get $username's GEXP.")
                    }
                } else {
                    if (type == "daily") {
                        if (HypixelUtils.getGEXP(username)) {
                            EssentialAPI.getNotifications()
                                .push(
                                    "Qaltils",
                                    "$username currently has " + HypixelUtils.gexp + " daily guild EXP."
                                )
                        } else {
                            EssentialAPI.getNotifications()
                                .push("Qaltils", "There was a problem trying to get $username's daily GEXP.")
                        }
                    } else if (type == "weekly") {
                        if (HypixelUtils.getWeeklyGEXP(username)) {
                            EssentialAPI.getNotifications()
                                .push(
                                    "Qaltils",
                                    "$username currently has " + HypixelUtils.gexp + " weekly guild EXP."
                                )
                        } else {
                            EssentialAPI.getNotifications()
                                .push("Qaltils", "There was a problem trying to get $username's weekly GEXP.")
                        }
                    } else {
                        EssentialAPI.getNotifications()
                            .push("Qaltils", "The type argument was not valid.")
                    }
                }
            } else {
                if (HypixelUtils.getGEXP()) {
                    EssentialAPI.getNotifications()
                        .push("Qaltils", "You currently have " + HypixelUtils.gexp + " guild EXP.")
                } else {
                    EssentialAPI.getNotifications()
                        .push("Qaltils", "There was a problem trying to get your GEXP.")
                }
            }
        }
    }

    @SubCommand("winstreak", description = "Gets the winstreak of the player specified")
    fun winstreak(@DisplayName("username") username: String?, @DisplayName("gamemode") gamemode: String?) {
        Multithreading.runAsync {
            if (QaltilsConfig.apiKey.isEmpty() || !HypixelUtils.isValidKey(QaltilsConfig.apiKey)) {
                Qaltils.sendMessage(EnumChatFormatting.RED.toString() + "You need to provide a valid API key to run this command! Type /api new to autoset a key.")
                return@runAsync
            }
            if (username != null) {
                if (gamemode != null) {
                    if (HypixelUtils.getWinstreak(username, gamemode)) {
                        EssentialAPI.getNotifications()
                            .push(
                                "Qaltils",
                                "$username currently has a " + HypixelUtils.winstreak + " winstreak in $gamemode."
                            )
                    } else {
                        EssentialAPI.getNotifications()
                            .push(
                                "Qaltils",
                                "There was a problem trying to get $username's winstreak in $gamemode."
                            )
                    }
                } else {
                    if (HypixelUtils.getWinstreak(username)) {
                        EssentialAPI.getNotifications()
                            .push(
                                "Qaltils",
                                "$username currently has a " + HypixelUtils.winstreak + " winstreak."
                            )
                    } else {
                        EssentialAPI.getNotifications()
                            .push("Qaltils", "There was a problem trying to get $username's winstreak.")
                    }
                }
            } else {
                if (HypixelUtils.getWinstreak()) {
                    EssentialAPI.getNotifications()
                        .push("Qaltils", "You currently have a " + HypixelUtils.winstreak + " winstreak.")
                } else {
                    EssentialAPI.getNotifications()
                        .push("Qaltils", "There was a problem trying to get your winstreak.")
                }
            }
        }
    }

}