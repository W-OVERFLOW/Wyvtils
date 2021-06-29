package net.wyvest.wyvtilities.utils

import net.minecraft.client.Minecraft


object Utils {
    @JvmField
    var isOnHypixel = false
    private val mc = Minecraft.getMinecraft()
    /**
     * Adapted from Skytils under AGPLv3
     * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
     */
    fun checkForHypixel() {
        isOnHypixel = run {
            try {
                if (mc.theWorld != null && !mc.isSingleplayer) {
                    if (mc.thePlayer != null && mc.thePlayer.clientBrand != null) {
                        if (mc.thePlayer.clientBrand.lowercase().contains("hypixel")) return@run true
                    }
                    if (mc.currentServerData != null) return@run mc.currentServerData.serverIP.lowercase()
                        .contains("hypixel")
                }
                return@run false
            } catch (e: Exception) {
                e.printStackTrace()
                return@run false
            }
        }
    }
}