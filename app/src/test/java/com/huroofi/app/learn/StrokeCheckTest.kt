package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StrokeCheckTest {
    /** A horizontal stroke of ten dots, 20 px apart, and a one-dot stroke below it. */
    private val body = (0 until 10).map { Offset(100f + it * 20f, 200f) }
    private val dot = listOf(Offset(150f, 400f))
    private val letter = listOf(body, dot)

    private fun check() = StrokeCheck(letter, tolerancePx = 5f)

    @Test
    fun dotsOfOnePointGiveThatPoint() {
        assertEquals(listOf(Offset(3f, 4f)), strokeDots(listOf(Offset(3f, 4f)), 10f))
    }

    @Test
    fun dotsAreEvenAndKeepBothEnds() {
        val dots = strokeDots(listOf(Offset(0f, 0f), Offset(100f, 0f)), 25f)
        assertEquals((0..4).map { Offset(it * 25f, 0f) }, dots)
    }

    @Test
    fun dotsFollowTheBend() {
        val dots = strokeDots(listOf(Offset(0f, 0f), Offset(100f, 0f), Offset(100f, 100f)), 50f)
        assertEquals(listOf(Offset(0f, 0f), Offset(50f, 0f), Offset(100f, 0f), Offset(100f, 50f), Offset(100f, 100f)), dots)
    }

    @Test
    fun noInkIsNotDone() {
        val c = check()
        assertFalse(c.done)
        assertEquals(0f, c.coverage(0))
        assertEquals(0, c.nextStroke())
    }

    @Test
    fun strokesCompleteInAnyOrder() {
        val c = check()
        dot.forEach(c::addInk)
        assertEquals(0, c.nextStroke())
        body.forEach(c::addInk)
        assertTrue(c.done)
        assertNull(c.nextStroke())
    }

    @Test
    fun skippedDotStrokeKeepsItNotDone() {
        val c = check()
        body.forEach(c::addInk)
        assertFalse(c.done)
        assertEquals(1, c.nextStroke())
    }

    @Test
    fun directionIsIgnored() {
        val c = check()
        body.reversed().forEach(c::addInk)
        assertEquals(1f, c.coverage(0))
        assertEquals(1, c.nextStroke())
    }

    @Test
    fun nextStrokeMovesOnAfterEachStroke() {
        val c = check()
        assertEquals(0, c.nextStroke())
        body.forEach(c::addInk)
        assertEquals(1, c.nextStroke())
        dot.forEach(c::addInk)
        assertNull(c.nextStroke())
    }

    @Test
    fun everyDotIsNeededToFinishAStroke() {
        val c = check()
        body.dropLast(1).forEach(c::addInk)
        assertEquals(0, c.nextStroke())
        c.addInk(body.last())
        assertEquals(1, c.nextStroke())
    }

    @Test
    fun inkJustOutsideToleranceDoesNotCount() {
        val c = StrokeCheck(listOf(listOf(Offset(100f, 100f))), tolerancePx = 30f)
        c.addInk(Offset(131f, 100f))
        assertFalse(c.covered(0, 0))
        c.addInk(Offset(130f, 100f))
        assertTrue(c.covered(0, 0))
    }

    @Test
    fun farInkChangesNothing() {
        val c = check()
        repeat(50) { c.addInk(Offset(900f + it, 900f)) }
        assertEquals(0f, c.coverage(0))
    }

    @Test
    fun clearStartsOver() {
        val c = check()
        letter.flatten().forEach(c::addInk)
        assertTrue(c.done)
        c.clear()
        assertFalse(c.done)
        assertFalse(c.covered(1, 0))
        assertEquals(0, c.nextStroke())
    }

    @Test
    fun noStrokesIsNeverDone() {
        val c = StrokeCheck(emptyList(), tolerancePx = 30f)
        c.addInk(Offset(1f, 1f))
        assertFalse(c.done)
        assertNull(c.nextStroke())
    }
}
