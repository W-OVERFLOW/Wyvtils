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

package cc.woverflow.wyvtils.eight.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import cc.woverflow.wyvtils.core.WyvtilsInfo
import cc.woverflow.wyvtils.core.config.WyvtilsConfig

/**
 * The command for Wyvtils.
 * This is an easy way for opening the WyvtilsConfig GUI.
 */
object WyvtilsCommand : Command(WyvtilsInfo.ID, true) {
    override val commandAliases = setOf(
        Alias("wyvest"),
        Alias("wyvtilities"),
        Alias("wt")
    )

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for ${WyvtilsInfo.NAME}")
    fun config() {
        EssentialAPI.getGuiUtil().openScreen(WyvtilsConfig.gui())
    }

}
