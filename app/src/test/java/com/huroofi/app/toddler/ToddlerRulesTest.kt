package com.huroofi.app.toddler

import androidx.compose.ui.graphics.Color
import com.huroofi.app.ui.theme.HuroofiTokens
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToddlerRulesTest {
    @Test
    fun saturatedRedsAreRed() {
        assertTrue(isRed(Color(0xFFFF0000)))
        assertTrue(isRed(Color(0xFFFF7A59))) // coral crayon: allowed only via the crayon allow-list
        assertTrue(isRed(Color(0xFFE0103A)))
    }

    @Test
    fun prototypePinksAmbersGreensAreNotRed() {
        for (hex in listOf(0xFFEC4F8C, 0xFFFF6FA5, 0xFFB83A6B, 0xFFF59E0B, 0xFF3BAA5C, 0xFFFFFFFF, 0xFFFFDDEA)) {
            assertFalse("%08X".format(hex), isRed(Color(hex)))
        }
    }

    @Test
    fun noHandoffTokenIsRed() {
        val tokens = with(HuroofiTokens) {
            listOf(
                Navy, Muted, Sky, Card, Primary, PrimaryShadow, Sun, SunShadow, Success, SuccessShadow,
                Outline, ParentBg, GateBg,
            )
        }
        for (t in tokens) assertFalse(t.toString(), isRed(t))
    }
}
