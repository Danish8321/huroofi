package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TraceCheckTest {
    /** Ten points on a horizontal line, 20 px apart. */
    private val line = (0 until 10).map { Offset(100f + it * 20f, 200f) }

    @Test
    fun noInkCoversNothing() {
        val check = TraceCheck(line, tolerancePx = 30f)
        assertEquals(0f, check.coverage)
        assertFalse(check.done)
    }

    @Test
    fun inkOnEveryTargetIsDone() {
        val check = TraceCheck(line, tolerancePx = 30f)
        line.forEach(check::addInk)
        assertEquals(1f, check.coverage)
        assertTrue(check.done)
    }

    @Test
    fun inkJustOutsideToleranceDoesNotCount() {
        val check = TraceCheck(listOf(Offset(100f, 100f)), tolerancePx = 30f)
        check.addInk(Offset(131f, 100f))
        assertEquals(0f, check.coverage)
        check.addInk(Offset(130f, 100f))
        assertEquals(1f, check.coverage)
    }

    @Test
    fun seventyPercentIsTheLine() {
        val check = TraceCheck(line, tolerancePx = 5f)
        line.take(6).forEach(check::addInk)
        assertFalse(check.done)
        check.addInk(line[6])
        assertTrue(check.done)
    }

    @Test
    fun inkFarAwayChangesNothing() {
        val check = TraceCheck(line, tolerancePx = 30f)
        repeat(50) { check.addInk(Offset(900f + it, 900f)) }
        assertEquals(0f, check.coverage)
    }

    @Test
    fun negativeCoordinatesAreSafe() {
        val check = TraceCheck(listOf(Offset(5f, 5f)), tolerancePx = 30f)
        check.addInk(Offset(-10f, -10f))
        assertEquals(1f, check.coverage)
    }

    @Test
    fun clearStartsOver() {
        val check = TraceCheck(line, tolerancePx = 30f)
        line.forEach(check::addInk)
        check.clear()
        assertEquals(0f, check.coverage)
    }

    @Test
    fun emptyGlyphIsNeverDone() {
        val check = TraceCheck(emptyList(), tolerancePx = 30f)
        check.addInk(Offset(1f, 1f))
        assertFalse(check.done)
    }

    @Test
    fun sampleMaskKeepsInkedGridPoints() {
        val points = sampleMask(100, 100, step = 10) { x, _ -> x < 50 }
        assertEquals(50, points.size)
        assertTrue(points.all { it.x < 50f })
        assertEquals(Offset(5f, 5f), points.first())
    }
}
