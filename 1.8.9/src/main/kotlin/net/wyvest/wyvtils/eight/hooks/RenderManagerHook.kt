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

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.IProjectile
import net.minecraft.entity.item.*
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityFireball
import net.minecraft.entity.projectile.EntityWitherSkull
import net.minecraft.util.AxisAlignedBB
import cc.woverflow.wyvtils.core.WyvtilsCore.eventBus
import cc.woverflow.wyvtils.core.config.WyvtilsConfig
import cc.woverflow.wyvtils.core.config.WyvtilsConfig.accurateHitbox
import cc.woverflow.wyvtils.core.listener.events.HitboxRenderEvent
import cc.woverflow.wyvtils.core.utils.ColorUtils.getAlpha
import cc.woverflow.wyvtils.core.utils.ColorUtils.getBlue
import cc.woverflow.wyvtils.core.utils.ColorUtils.getGreen
import cc.woverflow.wyvtils.core.utils.ColorUtils.getRed
import org.lwjgl.opengl.GL11
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.awt.Color


private var hitboxRenderEvent: HitboxRenderEvent? = null

fun setup(
    entityIn: Entity,
    ci: CallbackInfo
) {
    hitboxRenderEvent = HitboxRenderEvent(
        when (entityIn) {
            is EntityPlayer -> if (entityIn is EntityPlayerSP && (entityIn as EntityPlayer).gameProfile.id == Minecraft.getMinecraft().thePlayer.gameProfile.id) cc.woverflow.wyvtils.core.listener.events.Entity.SELF else cc.woverflow.wyvtils.core.listener.events.Entity.PLAYER
            is EntityLiving -> if (entityIn is IMob) cc.woverflow.wyvtils.core.listener.events.Entity.MONSTER else cc.woverflow.wyvtils.core.listener.events.Entity.LIVING
            is EntityArmorStand -> cc.woverflow.wyvtils.core.listener.events.Entity.ARMORSTAND
            is EntityFireball -> if (entityIn is EntityWitherSkull) cc.woverflow.wyvtils.core.listener.events.Entity.WITHERSKULL else cc.woverflow.wyvtils.core.listener.events.Entity.FIREBALL
            is EntityMinecart -> cc.woverflow.wyvtils.core.listener.events.Entity.MINECART
            is EntityItem -> cc.woverflow.wyvtils.core.listener.events.Entity.ITEM
            is EntityItemFrame -> cc.woverflow.wyvtils.core.listener.events.Entity.ITEMFRAME
            is EntityFireworkRocket -> cc.woverflow.wyvtils.core.listener.events.Entity.FIREWORK
            is EntityXPOrb -> cc.woverflow.wyvtils.core.listener.events.Entity.XP
            is IProjectile -> cc.woverflow.wyvtils.core.listener.events.Entity.PROJECTILE
            else -> cc.woverflow.wyvtils.core.listener.events.Entity.UNDEFINED
        }, getReachDistanceFromEntity(entityIn), Color.WHITE.rgb, Color.WHITE.rgb, Color.WHITE.rgb,
        cancelled = false,
        cancelBox = false,
        cancelLineOfSight = false,
        cancelEyeLine = false
    )
    eventBus.post(hitboxRenderEvent!!)
    if (hitboxRenderEvent!!.cancelled) {
        ci.cancel()
    } else {
        GL11.glLineWidth(WyvtilsConfig.hitboxWidth.toFloat())
    }
}

fun cancelLineOfSightAndBox(
    boundingBox: AxisAlignedBB,
    green: Int,
    entityIn: Entity
) {
    if (green == 255) {
        if (!hitboxRenderEvent!!.cancelBox) {
            RenderGlobal.drawOutlinedBoundingBox(
                if (accurateHitbox) boundingBox.expand(
                    entityIn.collisionBorderSize.toDouble(),
                    entityIn.collisionBorderSize.toDouble(),
                    entityIn.collisionBorderSize.toDouble()
                ) else boundingBox,
                getRed(hitboxRenderEvent!!.boxColor),
                getGreen(hitboxRenderEvent!!.boxColor),
                getBlue(hitboxRenderEvent!!.boxColor),
                getAlpha(hitboxRenderEvent!!.boxColor)
            )
        }
    } else {
        if (!hitboxRenderEvent!!.cancelLineOfSight) {
            RenderGlobal.drawOutlinedBoundingBox(
                boundingBox,
                getRed(hitboxRenderEvent!!.lineOfSightColor),
                getGreen(hitboxRenderEvent!!.lineOfSightColor),
                getBlue(hitboxRenderEvent!!.lineOfSightColor),
                getAlpha(hitboxRenderEvent!!.lineOfSightColor)
            )
        }
    }
}

fun cancelEyeLine(
    entityIn: Entity,
    x: Double,
    y: Double,
    z: Double,
    partialTicks: Float,
    ci: CallbackInfo
) {
    if (!hitboxRenderEvent!!.cancelEyeLine) {
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        val vec3 = entityIn.getLook(partialTicks)
        worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR)
        worldrenderer.pos(x, y + entityIn.eyeHeight.toDouble(), z).color(
            getRed(hitboxRenderEvent!!.eyeLineColor),
            getGreen(hitboxRenderEvent!!.eyeLineColor),
            getBlue(hitboxRenderEvent!!.eyeLineColor),
            getAlpha(hitboxRenderEvent!!.eyeLineColor)
        ).endVertex()
        worldrenderer.pos(
            x + vec3.xCoord * 2.0,
            y + entityIn.eyeHeight.toDouble() + vec3.yCoord * 2.0,
            z + vec3.zCoord * 2.0
        ).color(
            getRed(hitboxRenderEvent!!.eyeLineColor),
            getGreen(hitboxRenderEvent!!.eyeLineColor),
            getBlue(hitboxRenderEvent!!.eyeLineColor),
            getAlpha(hitboxRenderEvent!!.eyeLineColor)
        ).endVertex()
        tessellator.draw()
    }
    GlStateManager.enableTexture2D()
    GlStateManager.enableLighting()
    GlStateManager.enableCull()
    GlStateManager.disableBlend()
    GlStateManager.depthMask(true)
    ci.cancel()
}

/**
 * Adapted from EvergreenHUD under GPLv3
 * https://github.com/isXander/EvergreenHUD/blob/main/LICENSE
 *
 *
 * Modified to be more compact.
 */
private fun getReachDistanceFromEntity(entity: Entity?): Double {
    if (entity == null) return -1.0
    Minecraft.getMinecraft().mcProfiler.startSection("Calculating Reach Dist")
    val maxSize = 6.0
    val otherBB = entity.entityBoundingBox
    val collisionBorderSize = entity.collisionBorderSize
    val otherHitbox =
        otherBB.expand(collisionBorderSize.toDouble(), collisionBorderSize.toDouble(), collisionBorderSize.toDouble())
    val eyePos = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0f)
    val lookPos = Minecraft.getMinecraft().thePlayer.getLook(1.0f)
    val adjustedPos = eyePos.addVector(lookPos.xCoord * maxSize, lookPos.yCoord * maxSize, lookPos.zCoord * maxSize)
    val movingObjectPosition = otherHitbox.calculateIntercept(eyePos, adjustedPos) ?: return -1.0
    // finally calculate distance between both vectors
    val dist = eyePos.distanceTo(movingObjectPosition.hitVec)
    Minecraft.getMinecraft().mcProfiler.endSection()
    return dist
}