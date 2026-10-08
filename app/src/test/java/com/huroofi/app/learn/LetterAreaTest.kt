package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LetterAreaTest {
    /** A bar 100 px long and 30 px tall, as a grid every 6 px, and its centre line. */
    private val bar = (0..16).flatMap { x -> (0..5).map { y -> Offset(100f + x * 6f, 185f + y * 6f) } }
    private val centre = listOf(Offset(100f, 200f), Offset(196f, 200f))

    @Test
    fun distanceToASegment() {
        assertEquals(5f, segmentDistance(Offset(50f, 5f), Offset(0f, 0f), Offset(100f, 0f)), 1e-4f)
        assertEquals(5f, segmentDistance(Offset(-3f, 4f), Offset(0f, 0f), Offset(100f, 0f)), 1e-4f)
        assertEquals(5f, segmentDistance(Offset(3f, 4f), Offset(0f, 0f), Offset(0f, 0f)), 1e-4f)
    }

    @Test
    fun reachIsTheFurthestSpotPlusSlack() {
        assertEquals(15f + 4f, brushReach(bar, listOf(centre), slackPx = 4f, minPx = 13f), 1e-4f)
    }

    @Test
    fun reachNeverGoesUnderTheMinimum() {
        val thin = listOf(Offset(0f, 0f), Offset(6f, 0f))
        assertEquals(13f, brushReach(thin, listOf(thin), slackPx = 4f, minPx = 13f), 1e-4f)
        assertEquals(13f, brushReach(emptyList(), listOf(thin), slackPx = 4f, minPx = 13f), 1e-4f)
    }

    @Test
    fun aDotStrokeCountsAsAPoint() {
        val reach = brushReach(listOf(Offset(10f, 0f)), listOf(listOf(Offset(0f, 0f))), slackPx = 0f, minPx = 0f)
        assertEquals(10f, reach, 1e-4f)
    }

    @Test
    fun tracingDownTheMiddleWithThatBrushPaintsItAll() {
        val area = AreaCheck(bar, brushReach(bar, listOf(centre), slackPx = 4f, minPx = 13f))
        // A finger 3 px off the line, in short moves, as touch events land.
        strokeDots(centre.map { it + Offset(0f, 3f) }, 5f).zipWithNext(area::paint)
        assertTrue(area.done)
    }

    @Test
    fun aThinBrushLeavesGaps() {
        val area = AreaCheck(bar, reachPx = 6f)
        strokeDots(centre, 5f).zipWithNext(area::paint)
        assertFalse(area.done)
        assertTrue(area.painted(bar.indexOf(Offset(100f, 197f))))
        assertFalse(area.painted(bar.indexOf(Offset(100f, 185f))))
    }

    @Test
    fun aTapPaintsAroundIt() {
        val area = AreaCheck(bar, reachPx = 7f)
        area.paint(Offset(100f, 185f), Offset(100f, 185f))
        assertTrue(area.painted(0))
        assertTrue(area.painted(1))
        assertFalse(area.painted(2))
    }

    @Test
    fun clearStartsOver() {
        val area = AreaCheck(bar, reachPx = 100f)
        area.paint(centre[0], centre[1])
        assertTrue(area.done)
        area.clear()
        assertFalse(area.painted(0))
        assertFalse(area.done)
    }
}
