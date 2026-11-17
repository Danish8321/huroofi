package com.huroofi.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Plan 16 slice 9: Arabic runs carry the Arabic locale for the speech engine; English stays untagged. */
class ArabicLocaleTest {
    private fun arabicRanges(s: AnnotatedString) =
        s.spanStyles.filter { it.item.localeList == ArabicLocale }.map { s.text.substring(it.start, it.end) }

    @Test
    fun aRunIsTaggedWhole() {
        assertEquals(listOf("أرنب"), arabicRanges(arabicRun("أرنب")))
    }

    @Test
    fun withArabicTagsOnlyTheLetter() {
        assertEquals(listOf("ب"), arabicRanges(withArabic("Finish ", "ب", " first")))
    }

    @Test
    fun highlightedWordIsTaggedWhole() {
        val word = highlightedWord("أ", "رنب", Color.Red)
        assertEquals(listOf("أرنب"), arabicRanges(word))
        assertTrue(word.spanStyles.any { it.item.color == Color.Red && it.start == 0 && it.end == 1 })
    }
}
