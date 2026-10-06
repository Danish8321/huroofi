package com.huroofi.app.learn

import androidx.compose.ui.geometry.Offset
import com.huroofi.app.data.content.ContentJson
import com.huroofi.app.data.content.LetterStrokes
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

    private companion object {
        const val BOX = 260f
        const val TOLERANCE = 30f // TraceSpec.Tolerance at density 1
        const val SPACING = 18f // TraceSpec.DotSpacing at density 1
    }
}
