package com.huroofi.app.data.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressRulesTest {
    // 3 stages of 4 letters: stage 1 = 1..4, stage 2 = 5..8, stage 3 = 9..12
    private val stageOf = (1..12).associateWith { (it - 1) / 4 + 1 }

    @Test
    fun stageIsCompleteOnlyWhenAllItsLettersAre() {
        assertFalse(isStageComplete(1, setOf(1, 2, 3), stageOf))
        assertTrue(isStageComplete(1, setOf(1, 2, 3, 4), stageOf))
        assertFalse(isStageComplete(9, emptySet(), stageOf))
    }

    @Test
    fun stageWithNoLettersIsNeverComplete() {
        assertFalse(isStageComplete(99, setOf(1, 2, 3, 4), stageOf))
    }

    @Test
    fun stageOneIsAlwaysUnlocked() {
        assertEquals(1, unlockedStage(emptySet(), stageOf))
    }

    @Test
    fun nextStageOpensWhenPreviousIsComplete() {
        assertEquals(2, unlockedStage(setOf(1, 2, 3, 4), stageOf))
        assertEquals(2, unlockedStage(setOf(1, 2, 3, 4, 5), stageOf))
        assertEquals(3, unlockedStage((1..8).toSet(), stageOf))
    }

    @Test
    fun skippingAheadDoesNotUnlock() {
        assertEquals(1, unlockedStage(setOf(5, 6, 7, 8), stageOf))
    }

    @Test
    fun unlockIsCappedAtLastStage() {
        assertEquals(3, unlockedStage((1..12).toSet(), stageOf))
    }

    @Test
    fun limitReachedAtExactlyTheLimit() {
        assertFalse(limitReached(usageSeconds = 20 * 60 - 1, limitMinutes = 20))
        assertTrue(limitReached(usageSeconds = 20 * 60, limitMinutes = 20))
        assertTrue(limitReached(usageSeconds = 20 * 60 + 5, limitMinutes = 20))
    }

    @Test
    fun usageRollsOverOnANewDay() {
        assertEquals(300, usageForToday("2026-10-01", 300, today = "2026-10-01"))
        assertEquals(0, usageForToday("2026-10-01", 300, today = "2026-10-02"))
    }

    @Test
    fun limitIsClampedAndSnappedToStepsOfFive() {
        assertEquals(5, clampLimitMinutes(0))
        assertEquals(5, clampLimitMinutes(-10))
        assertEquals(60, clampLimitMinutes(999))
        assertEquals(20, clampLimitMinutes(20))
        assertEquals(20, clampLimitMinutes(21))
        assertEquals(25, clampLimitMinutes(23))
        assertEquals(60, clampLimitMinutes(58))
    }

    @Test
    fun modeParsingFallsBackToToddler() {
        assertEquals(AgeMode.PRESCHOOL, AgeMode.parse("PRESCHOOL"))
        assertEquals(AgeMode.TODDLER, AgeMode.parse("garbage"))
        assertEquals(AgeMode.TODDLER, AgeMode.parse(null))
    }
}
