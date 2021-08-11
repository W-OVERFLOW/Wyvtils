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

package net.wyvest.wyvtilities.mixin;

import gg.essential.loader.stage0.EssentialSetupTweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import xyz.matthewtgm.requisite.launchwrapper.RequisiteLaunchwrapper;

public class RequisiteEssentialTweaker extends EssentialSetupTweaker {

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        super.injectIntoClassLoader(classLoader);
        System.out.println("Essential has been loaded.");
        RequisiteLaunchwrapper.inject(classLoader);
        System.out.println("Requisite has been loaded.");
    }

}
