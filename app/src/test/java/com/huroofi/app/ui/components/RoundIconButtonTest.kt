package com.huroofi.app.ui.components

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class RoundIconButtonTest {
    @Test
    fun toddlerSizeIsRaisedToSixtyFour() {
        assertEquals(64.dp, roundButtonSize(40.dp, toddler = true))
        assertEquals(84.dp, roundButtonSize(84.dp, toddler = true))
    }

    @Test
    fun normalSizeIsRaisedToFortyEight() {
        assertEquals(48.dp, roundButtonSize(32.dp, toddler = false))
        assertEquals(56.dp, roundButtonSize(56.dp, toddler = false))
    }
}
