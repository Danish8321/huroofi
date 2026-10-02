package com.huroofi.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class TokensTest {
    @Test
    fun colourTokensMatchHandoffSection5() {
        assertEquals(Color(0xFF13294B), HuroofiTokens.Navy)
        assertEquals(Color(0xFF4A5D7A), HuroofiTokens.Muted)
        assertEquals(Color(0xFFE8F4FF), HuroofiTokens.Sky)
        assertEquals(Color(0xFFFFFFFF), HuroofiTokens.Card)
        assertEquals(Color(0xFF1F6FE0), HuroofiTokens.Primary)
        assertEquals(Color(0xFF1555B0), HuroofiTokens.PrimaryShadow)
        assertEquals(Color(0xFFFFC93C), HuroofiTokens.Sun)
        assertEquals(Color(0xFFE0A400), HuroofiTokens.SunShadow)
        assertEquals(Color(0xFF158048), HuroofiTokens.Success)
        assertEquals(Color(0xFF0F5F35), HuroofiTokens.SuccessShadow)
        assertEquals(Color(0xFF26324A), HuroofiTokens.Outline)
        assertEquals(Color(0xFFF4F7FB), HuroofiTokens.ParentBg)
        assertEquals(Color(0xFF13294B), HuroofiTokens.GateBg)
    }

    @Test
    fun textMeetsWcagAaOnItsBackgrounds() {
        val pairs = mapOf(
            "navy on sky" to (HuroofiTokens.Navy to HuroofiTokens.Sky),
            "navy on card" to (HuroofiTokens.Navy to HuroofiTokens.Card),
            "muted on sky" to (HuroofiTokens.Muted to HuroofiTokens.Sky),
            "muted on parent bg" to (HuroofiTokens.Muted to HuroofiTokens.ParentBg),
            "white on primary" to (HuroofiTokens.Card to HuroofiTokens.Primary),
            "white on success" to (HuroofiTokens.Card to HuroofiTokens.Success),
            "navy on sun" to (HuroofiTokens.Navy to HuroofiTokens.Sun),
            "white on gate" to (HuroofiTokens.Card to HuroofiTokens.GateBg),
        )
        pairs.forEach { (name, p) ->
            val ratio = contrastRatio(p.first, p.second)
            assertTrue("$name contrast $ratio < 4.5", ratio >= 4.5)
        }
    }

    @Test
    fun contrastRatioKnownValues() {
        assertEquals(21.0, contrastRatio(Color.Black, Color.White), 0.01)
        assertEquals(1.0, contrastRatio(Color.White, Color.White), 0.001)
    }

    @Test
    fun stageColoursParseFromHex() {
        val s = StageColors.fromHex("#F59E0B", "#FFEDC4", "#A35400")
        assertEquals(Color(0xFFF59E0B), s.border)
        assertEquals(Color(0xFFFFEDC4), s.pastel)
        assertEquals(Color(0xFFA35400), s.accent)
    }

    @Test
    fun stageColoursRejectBadHex() {
        assertThrows(IllegalArgumentException::class.java) {
            StageColors.fromHex("F59E0B", "#FFEDC4", "#A35400")
        }
        assertThrows(IllegalArgumentException::class.java) {
            StageColors.fromHex("#F59E0", "#FFEDC4", "#A35400")
        }
    }
}
