package com.huroofi.app.ui.components

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

class DemoBallTest {
    private val bend = listOf(Offset(0f, 0f), Offset(100f, 0f), Offset(100f, 100f))

    @Test fun `ends of the path`() {
        assertEquals(Offset(0f, 0f), pointAlong(bend, 0f))
        assertEquals(Offset(100f, 100f), pointAlong(bend, 1f))
    }

    @Test fun `halfway is the bend, by length`() = assertEquals(Offset(100f, 0f), pointAlong(bend, 0.5f))

    @Test fun `a quarter is half the first leg`() = assertEquals(Offset(50f, 0f), pointAlong(bend, 0.25f))

    @Test fun `fraction is clamped`() = assertEquals(Offset(100f, 100f), pointAlong(bend, 2f))

    @Test fun `a dot stays put`() = assertEquals(Offset(5f, 5f), pointAlong(listOf(Offset(5f, 5f)), 0.7f))

    @Test fun `no points gives zero`() = assertEquals(Offset.Zero, pointAlong(emptyList(), 0.5f))
}
