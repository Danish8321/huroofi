package com.huroofi.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class ScaleDownToFitTest {
    @Test
    fun contentThatFitsIsNeverGrown() {
        assertEquals(1f, fitScale(100, 100, 300, 300), 0f)
    }

    @Test
    fun tooTallContentShrinksToTheHeight() {
        assertEquals(0.5f, fitScale(100, 200, 300, 100), 0.0001f)
    }

    @Test
    fun theTighterSideWins() {
        assertEquals(0.25f, fitScale(400, 200, 100, 100), 0.0001f)
    }
}
