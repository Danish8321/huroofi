package com.huroofi.app.learn

import com.huroofi.app.data.content.TestContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LetterShapesTest {
    @Test
    fun joiningLetterHasFourShapes() {
        assertEquals(
            listOf(
                ShapeKind.ALONE to "ب",
                ShapeKind.START to "بـ",
                ShapeKind.MIDDLE to "ـبـ",
                ShapeKind.END to "ـب",
            ),
            letterShapes("ب"),
        )
    }

    @Test
    fun nonJoiningLettersHaveAloneAndEndOnly() {
        for (l in NON_JOINING) {
            assertEquals(l, listOf(ShapeKind.ALONE to l, ShapeKind.END to "ـ$l"), letterShapes(l))
        }
    }

    @Test
    fun nonJoiningSetIsTheSixContentLetters() {
        val content = TestContent.repo.letters.map { it.letter }.toSet()
        assertEquals(6, NON_JOINING.size)
        assertTrue(NON_JOINING.toString(), content.containsAll(NON_JOINING))
    }
}
