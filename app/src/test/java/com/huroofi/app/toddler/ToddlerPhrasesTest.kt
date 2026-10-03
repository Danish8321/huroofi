package com.huroofi.app.toddler

import com.huroofi.app.data.content.TestContent
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test

class ToddlerPhrasesTest {
    private val repo = TestContent.repo

    @Test
    fun matchesThePrototypes() {
        assertEquals("أين البطة؟", ToddlerPhrases.whereIsAr(repo.letter(2)))
        assertEquals("أين الأسد؟", ToddlerPhrases.whereIsAr(repo.letter(1)))
        assertEquals("أين التفاح؟", ToddlerPhrases.whereIsAr(repo.letter(3)))
        assertEquals("Where's the duck?", ToddlerPhrases.whereIsEn(repo.letter(2)))
        assertEquals("لوّن الباء", ToddlerPhrases.colourAr(repo.letter(2)))
    }

    /** The review list must show exactly what the app shows. Update the file if this fails. */
    @Test
    fun reviewListIsUpToDate() {
        val expected = buildString {
            appendLine("| # | Letter | Where is…? | Colour… |")
            appendLine("|---|---|---|---|")
            for (l in repo.letters) {
                appendLine("| ${l.index} | ${l.letter} | ${ToddlerPhrases.whereIsAr(l)} | ${ToddlerPhrases.colourAr(l)} |")
            }
        }
        val file = File("../docs/review/toddler-phrases.md").readText(Charsets.UTF_8).replace("\r\n", "\n")
        assertEquals(expected, file.substring(file.indexOf("| # |")))
    }
}
