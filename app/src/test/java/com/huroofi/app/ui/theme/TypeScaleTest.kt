package com.huroofi.app.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TypeScaleTest {
    @Test
    fun noSizeIsBelowSixteenSp() {
        TypeScale.all.forEach { e ->
            assertTrue("${e.name} min ${e.min} < 16", e.min >= 16f)
        }
    }

    @Test
    fun defaultsLieInsideTheirRange() {
        TypeScale.all.forEach { e ->
            assertTrue("${e.name} range", e.min <= e.default && e.default <= e.max)
        }
    }

    @Test
    fun namesAreUnique() {
        assertEquals(TypeScale.all.size, TypeScale.all.map { it.name }.toSet().size)
    }

    @Test
    fun valuesMatchHandoffSection5() {
        assertEquals(16f, TypeScale.Caption.default)
        assertEquals(18f, TypeScale.Body.default)
        assertEquals(20f, TypeScale.SectionHeading.default)
        assertEquals(20f, TypeScale.ButtonSecondary.default)
        assertEquals(22f, TypeScale.ButtonPrimary.default)
        assertEquals(26f, TypeScale.ScreenTitle.min)
        assertEquals(36f, TypeScale.ScreenTitle.max)
        assertEquals(26f, TypeScale.ArabicChip.min)
        assertEquals(34f, TypeScale.ArabicChip.max)
        assertEquals(170f, TypeScale.LessonLetter.default)
        assertEquals(34f, TypeScale.ToddlerSentence.min)
        assertEquals(42f, TypeScale.ToddlerSentence.max)
        assertEquals(60f, TypeScale.ToddlerSentenceTablet.default)
        assertEquals(50f, TypeScale.ToddlerWord.min)
        assertEquals(88f, TypeScale.ToddlerWord.max)
        assertEquals(110f, TypeScale.ToddlerCardLetter.default)
    }

    @Test
    fun arabicIsOneStepLargerThanEnglishNeighbour() {
        assertEquals(20f, arabicSizeNextTo(18f), 0f)
        assertEquals(24f, arabicSizeNextTo(22f), 0f)
    }
}
