package com.huroofi.app.toddler.cards

import com.huroofi.app.toddler.MAX_TODDLER_CHOICES
import com.huroofi.app.toddler.isRed
import com.huroofi.app.ui.theme.HuroofiDimens
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardsSpecTest {
    @Test
    fun controlsMeetToddlerTouchMinimum() {
        for (s in CardsSpec.touchSizes) assertTrue("$s", s >= HuroofiDimens.ToddlerMinTouch)
    }

    @Test
    fun atMostThreeChoices() {
        assertTrue(CardsSpec.CHOICES <= MAX_TODDLER_CHOICES)
    }

    @Test
    fun noRed() {
        for (c in CardsSpec.colors) assertFalse("$c", isRed(c))
    }
}
