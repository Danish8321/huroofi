package com.huroofi.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

/**
 * A foreground a screen draws on a fixed background, for the contrast sweep (plan 08 decision 9).
 * [large] is large text (≥ 24 sp, or ≥ 18.66 sp bold) or a non-text cue such as an icon or ring.
 */
data class ContrastPair(val fg: Color, val bg: Color, val large: Boolean, val what: String) {
    /** WCAG AA: 3:1 for large text and non-text cues, 4.5:1 otherwise. */
    val required: Double get() = if (large) 3.0 else 4.5
}

/** WCAG 2.x contrast ratio between two opaque colours. */
fun contrastRatio(a: Color, b: Color): Double {
    val la = a.luminance().toDouble()
    val lb = b.luminance().toDouble()
    return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
}
