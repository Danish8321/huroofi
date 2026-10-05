package com.huroofi.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Test

class GlyphFitTest {
    private val ink = Rect(100f, 50f, 300f, 250f)

    @Test
    fun cornersLandOnTheInkCorners() {
        assertEquals(Offset(100f, 50f), toCanvas(listOf(0f, 0f), ink))
        assertEquals(Offset(300f, 50f), toCanvas(listOf(1f, 0f), ink))
        assertEquals(Offset(100f, 250f), toCanvas(listOf(0f, 1f), ink))
        assertEquals(Offset(300f, 250f), toCanvas(listOf(1f, 1f), ink))
    }

    @Test
    fun centreLandsOnTheInkCentre() {
        assertEquals(ink.center, toCanvas(listOf(0.5f, 0.5f), ink))
    }

    @Test
    fun xGoesRightAndYGoesDown() {
        val p = toCanvas(listOf(0.25f, 0.75f), ink)
        assertEquals(Offset(150f, 200f), p)
    }
}
