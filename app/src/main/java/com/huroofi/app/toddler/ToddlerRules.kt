package com.huroofi.app.toddler

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min

/** Toddler screens offer at most this many things to choose between (HANDOFF section 2). */
const val MAX_TODDLER_CHOICES = 3

/**
 * True for a saturated red: hue under 20 or over 340 degrees, saturation >= 0.5, value >= 0.35.
 * Toddler screens never use red for feedback or status (plan 05 decision 2).
 */
fun isRed(c: Color): Boolean {
    val r = c.red
    val g = c.green
    val b = c.blue
    val hi = max(r, max(g, b))
    val lo = min(r, min(g, b))
    val delta = hi - lo
    if (hi < 0.35f || delta == 0f || delta / hi < 0.5f) return false
    val hue = when (hi) {
        r -> 60f * (((g - b) / delta) % 6f)
        g -> 60f * ((b - r) / delta + 2f)
        else -> 60f * ((r - g) / delta + 4f)
    }.let { if (it < 0f) it + 360f else it }
    return hue < 20f || hue > 340f
}
