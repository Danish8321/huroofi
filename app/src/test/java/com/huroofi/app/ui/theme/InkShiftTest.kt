package com.huroofi.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import org.junit.Assert.assertEquals
import org.junit.Test

class InkShiftTest {
    @Test
    fun centredInkStaysPut() {
        assertEquals(Offset.Zero, inkShift(IntSize(100, 150), Rect(30f, 50f, 70f, 100f)))
    }

    @Test
    fun lowInkMovesUp() {
        // A tail letter: ink sits in the lower part of the line box.
        assertEquals(Offset(0f, -35f), inkShift(IntSize(100, 150), Rect(30f, 90f, 70f, 130f)))
    }

    @Test
    fun offCentreInkMovesBothWays() {
        assertEquals(Offset(15f, 20f), inkShift(IntSize(100, 100), Rect(0f, 0f, 70f, 60f)))
    }
}
