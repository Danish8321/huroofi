package com.huroofi.app.parent

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

/** The Parent zone is for adults: touch ≥ 48 dp and AA text contrast (plan 06 decision 9). */
class ZoneSpecTest {
    private fun channel(c: Float): Double = if (c <= 0.03928f) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)

    private fun luminance(c: Color): Double =
        0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)

    private fun contrast(a: Color, b: Color): Double {
        val (hi, lo) = listOf(luminance(a), luminance(b)).sortedDescending()
        return (hi + 0.05) / (lo + 0.05)
    }

    @Test
    fun contrastHelperMatchesKnownValues() {
        assertTrue(contrast(Color.Black, Color.White) in 20.9..21.1)
        assertTrue(contrast(Color.White, Color.White) in 0.99..1.01)
    }

    @Test
    fun touchTargetsAreAtLeast48dp() {
        for (size in ZoneSpec.touchSizes) assertTrue("$size", size >= 48.dp)
    }

    @Test
    fun textPairsMeetAa() {
        for ((text, background) in ZoneSpec.textPairs) {
            val ratio = contrast(text, background)
            assertTrue("$text on $background is $ratio", ratio >= 4.5)
        }
    }
}
