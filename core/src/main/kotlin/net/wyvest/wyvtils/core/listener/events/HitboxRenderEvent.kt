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

package net.wyvest.wyvtils.core.listener.events

/**
 * Called when a hitbox is about to be rendered.
 */
data class HitboxRenderEvent(
    val entity: Entity,
    val distance: Double,
    var boxColor: Int,
    var lineOfSightColor: Int,
    var eyeLineColor: Int,
    override var cancelled: Boolean = false,
    var cancelBox: Boolean,
    var cancelLineOfSight: Boolean,
    var cancelEyeLine: Boolean
) : Event()