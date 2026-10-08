package com.huroofi.app.toddler.find

import com.huroofi.app.toddler.CountdownSpec
import com.huroofi.app.toddler.MAX_TODDLER_CHOICES
import com.huroofi.app.toddler.isRed
import com.huroofi.app.ui.theme.HuroofiDimens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FindSpecTest {
    @Test
    fun controlsMeetToddlerTouchMinimum() {
        for (s in FindSpec.touchSizes) assertTrue("$s", s >= HuroofiDimens.ToddlerMinTouch)
    }

    @Test
    fun atMostThreeChoices() {
        assertTrue(FindSpec.CHOICES <= MAX_TODDLER_CHOICES)
    }

    @Test
    fun noRed() {
        for (c in FindSpec.colors) assertFalse("$c", isRed(c))
    }

    @Test
    fun nextWaitsASecondThenCountsDownFour() {
        assertEquals(1_000, CountdownSpec.PAUSE_MS)
        assertEquals(4_000, CountdownSpec.FILL_MS)
    }
}
