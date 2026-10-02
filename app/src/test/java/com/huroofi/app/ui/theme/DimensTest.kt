package com.huroofi.app.ui.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DimensTest {
    @Test
    fun constantsMatchHandoff() {
        assertEquals(64.dp, HuroofiDimens.PrimaryButtonHeight)
        assertEquals(22.dp, HuroofiDimens.PrimaryButtonCorner)
        assertEquals(6.dp, HuroofiDimens.ButtonShadow)
        assertEquals(48.dp, HuroofiDimens.MinTouch)
        assertEquals(64.dp, HuroofiDimens.ToddlerMinTouch)
        assertEquals(84.dp, HuroofiDimens.ToddlerMainControlMin)
        assertEquals(104.dp, HuroofiDimens.ToddlerMainControlMax)
        assertEquals(16.dp, HuroofiDimens.EdgeSafe)
    }

    @Test
    fun minTouchTargetDependsOnMode() {
        assertEquals(48.dp, minTouchTarget(toddler = false))
        assertEquals(64.dp, minTouchTarget(toddler = true))
    }

    @Test
    fun toddlerNeverBelowSixtyFour() {
        assertTrue(minTouchTarget(true) >= 64.dp)
        assertTrue(HuroofiDimens.ToddlerMainControlMin >= 64.dp)
    }
}
