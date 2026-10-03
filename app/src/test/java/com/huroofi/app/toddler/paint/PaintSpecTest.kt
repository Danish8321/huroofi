package com.huroofi.app.toddler.paint

import com.huroofi.app.toddler.MAX_TODDLER_CHOICES
import com.huroofi.app.toddler.isRed
import com.huroofi.app.ui.theme.HuroofiDimens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaintSpecTest {
    @Test
    fun controlsMeetToddlerTouchMinimum() {
        for (s in PaintSpec.touchSizes) assertTrue("$s", s >= HuroofiDimens.ToddlerMinTouch)
    }

    @Test
    fun atMostThreeChoices() {
        assertTrue(PaintSpec.CHOICES <= MAX_TODDLER_CHOICES)
    }

    @Test
    fun noRed() {
        for (c in PaintSpec.colors) assertFalse("$c", isRed(c))
    }

    /** Coral is the one red-ish crayon; crayons are paint, not feedback (decision 2). */
    @Test
    fun onlyCoralCrayonLooksRed() {
        assertEquals(listOf(Crayon.CORAL), Crayon.entries.filter { isRed(it.color) })
    }
}
