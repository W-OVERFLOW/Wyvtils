package net.wyvest.wyvtilities.utils

import net.minecraft.util.EnumChatFormatting
import net.wyvest.wyvtilities.Wyvtilities
import xyz.matthewtgm.tgmlib.util.ApiHelper

/**
 * Adapted from Skytils under AGPLv3
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
object APIUtil {

    fun getUUID(username: String): String? {
        val uuidResponse = ApiHelper.getParsedJsonOnline("https://api.mojang.com/users/profiles/minecraft/$username")
        if (uuidResponse.asJsonObject.has("error")) {
            Wyvtilities.sendMessage(EnumChatFormatting.RED.toString() + "Failed with error: ${uuidResponse.asJsonObject["reason"].asString}")
            return null
        }
        return uuidResponse.asJsonObject["uuid"].asString.replace("-", "")
    }
}