package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.huroofi.app.data.content.ContentJson
import com.huroofi.app.data.content.LetterStrokes
import com.huroofi.app.ui.theme.refit
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Every letter in data/strokes.json can be finished by tracing it, and wrong lines cannot finish it
 * (plan 09 decision 3). The ink box is a [BOX] px square at density 1, near the phone's Trace box;
 * tolerance and dot spacing are the app's.
 */
class AllLettersTraceTest {
    private val letters: List<LetterStrokes> by lazy {
        ContentJson.decodeStrokes(File("../data/strokes.json").readText(Charsets.UTF_8)).letters
    }

    private fun toBox(p: List<Float>) = Offset(p[0] * BOX, p[1] * BOX)

    private fun check(l: LetterStrokes) =
        StrokeCheck(l.strokes.sortedBy { it.order }.map { s -> strokeDots(s.points.map(::toBox), SPACING) }, TOLERANCE)

    /** A finger moving along [path], one ink point every 3 px, as touch events would land. */
    private fun ink(check: StrokeCheck, path: List<Offset>) = strokeDots(path, 3f).forEach(check::addInk)

    @Test
    fun all28LettersAreHere() {
        assertEquals((1..28).toList(), letters.map { it.index })
    }

    @Test
    fun tracingEveryStrokeFinishesEveryLetter() {
        letters.forEach { l ->
            val c = check(l)
            l.strokes.forEach { s -> ink(c, s.points.map(::toBox)) }
            assertTrue("letter ${l.index}", c.done)
        }
    }

    @Test
    fun aCrossOverTheBoxFinishesNoLetter() {
        letters.forEach { l ->
            val c = check(l)
            ink(c, listOf(Offset(0f, 0f), Offset(BOX, BOX)))
            ink(c, listOf(Offset(BOX, 0f), Offset(0f, BOX)))
            assertFalse("letter ${l.index}", c.done)
        }
    }

    @Test
    fun tracingAlifFinishesNoOtherLetter() {
        val alif = letters.first { it.index == 1 }.strokes.filter { !it.dot }
        letters.filter { it.index != 1 }.forEach { l ->
            val c = check(l)
            alif.forEach { s -> ink(c, s.points.map(::toBox)) }
            assertFalse("letter ${l.index}", c.done)
        }
    }

    @Test
    fun skippingTheDotsLeavesALetterUnfinished() {
        letters.filter { l -> l.strokes.any { it.dot } }.forEach { l ->
            val c = check(l)
            l.strokes.filter { !it.dot }.forEach { s -> ink(c, s.points.map(::toBox)) }
            assertFalse("letter ${l.index}", c.done)
        }
    }

    /**
     * Plan 16 slice 3: ink carried to a refitted letter (rotation, Display size) loses no finished
     * stroke. On a smaller letter the dp tolerance reaches further, so it can finish one more, as
     * fresh tracing at that size would (ث's dots, at 160 px).
     */
    @Test
    fun inkRefittedToAnotherSizeKeepsItsFinishedStrokes() {
        val from = Rect(0f, 0f, BOX, BOX)
        listOf(Rect(300f, 40f, 300f + 400f, 440f), Rect(20f, 500f, 180f, 660f)).forEach { to ->
            letters.forEach { l ->
                val before = check(l)
                val traced = l.strokes.sortedBy { it.order }.let { it.take((it.size + 1) / 2) }.map { s -> s.points.map(::toBox) }
                traced.forEach { ink(before, it) }
                val scale = to.width / BOX
                val after = StrokeCheck(
                    l.strokes.sortedBy { it.order }.map { s -> strokeDots(s.points.map { refit(toBox(it), from, to) }, SPACING * scale) },
                    TOLERANCE,
                )
                traced.forEach { path -> ink(after, path.map { refit(it, from, to) }) }
                val finished = { c: StrokeCheck -> l.strokes.indices.count(c::finished) }
                assertTrue("letter ${l.index} at ${to.width}", finished(after) >= finished(before))
                if (to.width >= BOX) assertEquals("letter ${l.index} at ${to.width}", finished(before), finished(after))
            }
        }
    }

    private companion object {
        const val BOX = 260f
        const val TOLERANCE = 30f // TraceSpec.Tolerance at density 1
        const val SPACING = 18f // TraceSpec.DotSpacing at density 1
    }
}
