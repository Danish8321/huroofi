package com.huroofi.app.toddler.paint

import com.huroofi.app.data.content.TestContent
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaintModelTest {
    private fun line(length: Float) = Painting().start(PaintPoint(0f, 0f)).moveTo(PaintPoint(length, 0f))

    @Test
    fun starNeedsFourHundredDp() {
        assertFalse(line(399f).starEarned)
        assertTrue(line(400f).starEarned)
    }

    @Test
    fun distanceNotEventCount() {
        var still = Painting().start(PaintPoint(50f, 50f))
        repeat(1_000) { still = still.moveTo(PaintPoint(50f, 50f)) }
        assertEquals(0f, still.totalDp)
        assertFalse(still.starEarned)
        assertTrue(line(400f).starEarned)
    }

    @Test
    fun distanceAddsUpAcrossStrokes() {
        val p = line(300f).start(PaintPoint(0f, 100f)).moveTo(PaintPoint(60f, 180f))
        assertEquals(400f, p.totalDp, 0.001f)
        assertTrue(p.starEarned)
        assertEquals(2, p.strokes.size)
    }

    @Test
    fun strokesKeepTheCrayonTheyWerePaintedWith() {
        val p = line(10f).select(Crayon.PURPLE).start(PaintPoint(1f, 1f))
        assertEquals(listOf(Crayon.CORAL, Crayon.PURPLE), p.strokes.map { it.crayon })
    }

    @Test
    fun wipeClearsEverythingButTheCrayon() {
        val wiped = line(500f).select(Crayon.GREEN).wipe()
        assertEquals(Painting(crayon = Crayon.GREEN), wiped)
        assertFalse(wiped.starEarned)
    }

    @Test
    fun defaultCrayonIsCoral() {
        assertEquals(Crayon.CORAL, Painting().crayon)
    }

    @Test
    fun nextNeverRepeatsTheLetterAndClearsTheCanvas() {
        val session = PaintSession(TestContent.repo.letters, Random(7))
        var page = session.start()
        repeat(200) {
            val painted = page.copy(painting = page.painting.start(PaintPoint(0f, 0f)).moveTo(PaintPoint(500f, 0f)))
            val next = session.next(painted)
            assertNotEquals(page.letter, next.letter)
            assertTrue(next.painting.strokes.isEmpty())
            assertEquals(0f, next.painting.totalDp)
            page = next
        }
    }

    @Test
    fun allTwentyEightLettersCanComeUp() {
        val session = PaintSession(TestContent.repo.letters, Random(1))
        var page = session.start()
        val seen = mutableSetOf(page.letter)
        repeat(2_000) {
            page = session.next(page)
            seen += page.letter
        }
        assertEquals(28, seen.size)
    }
}
