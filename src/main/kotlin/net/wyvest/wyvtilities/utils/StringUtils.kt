package net.wyvest.wyvtilities.utils

import gg.essential.universal.wrappers.message.UTextComponent
import org.apache.commons.lang3.StringUtils as ApacheStringUtils

/**
 * Adapted from Skytils under AGPLv3
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
fun String.startsWithAny(vararg sequences: CharSequence?) = ApacheStringUtils.startsWithAny(this, *sequences)

/**
 * Adapted from Skytils under AGPLv3
 * https://github.com/Skytils/SkytilsMod/blob/1.x/LICENSE.md
 */
fun String.containsAny(vararg sequences: CharSequence?) = sequences.any { it != null && this.contains(it, true) }


fun String.withoutFormattingCodes(): String = UTextComponent.stripFormatting(this)