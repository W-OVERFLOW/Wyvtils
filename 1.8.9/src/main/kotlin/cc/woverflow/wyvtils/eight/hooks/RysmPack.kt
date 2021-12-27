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

package cc.woverflow.wyvtils.eight.hooks

import net.minecraft.client.resources.FolderResourcePack
import net.minecraft.client.resources.data.IMetadataSection
import net.minecraft.client.resources.data.IMetadataSerializer
import net.minecraft.util.ResourceLocation
import cc.woverflow.wyvtils.eight.Wyvtils
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.lang.reflect.Method
import javax.imageio.ImageIO

private val yes = ResourceLocation("wyvtils", "textures/pack.png")
var image: BufferedImage? = null

@Throws(IOException::class)
fun cacheResourceLocation() {
    image = ImageIO.read(Wyvtils.mc.resourceManager.getResource(yes).inputStream)
}

fun containsFunnyMethod(a: Array<Method>): Boolean {
    a.forEach {
        if (it.name == "func_148312_b") return true
    }
    return false
}

class RysmPack(file: File) : FolderResourcePack(file) {
    override fun getPackImage(): BufferedImage? {
        if (image == null) {
            cacheResourceLocation()
        }
        return image
    }

    override fun <T : IMetadataSection?> getPackMetadata(
        p_getPackMetadata_1_: IMetadataSerializer?,
        p_getPackMetadata_2_: String?
    ): T? {
        return null
    }
}