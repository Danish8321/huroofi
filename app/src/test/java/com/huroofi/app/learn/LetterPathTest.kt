package com.huroofi.app.learn

import com.huroofi.app.data.content.TestContent
import com.huroofi.app.data.progress.unlockedStage
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LetterPathTest {
    private val letters = TestContent.repo.letters
    private val stageOf = letters.associate { it.index to it.stage }

    private fun today(completed: Set<Int>, seed: Int = 1) =
        todaysLetter(letters, completed, unlockedStage(completed, stageOf), Random(seed))

    @Test
    fun freshStartIsAlif() {
        assertEquals(1, today(emptySet()).index)
    }

    @Test
    fun aGapInTheOpenStageComesFirst() {
        assertEquals(2, today(setOf(1, 3)).index)
    }

    @Test
    fun finishingAStageMovesToTheNextOne() {
        assertEquals(5, today(setOf(1, 2, 3, 4)).index)
    }

    @Test
    fun allLearnedGivesALearnedLetterForReview() {
        val all = (1..28).toSet()
        val picks = (1..20).map { today(all, seed = it).index }.toSet()
        assertTrue(picks.all { it in all })
        assertTrue("review should vary", picks.size > 1)
    }

    @Test
    fun thirdLetterGoesToTheNextLetter() {
        assertEquals(PathNext.Meet(4), nextStep(letters, setOf(1, 2, 3), justLearned = 3, wasNew = true))
    }

    @Test
    fun fourthLetterGoesToReward() {
        assertEquals(PathNext.Reward(1), nextStep(letters, setOf(1, 2, 3, 4), justLearned = 4, wasNew = true))
    }

    @Test
    fun aGapStillOpenGoesBackToIt() {
        assertEquals(PathNext.Meet(2), nextStep(letters, setOf(1, 3, 4), justLearned = 4, wasNew = true))
    }

    @Test
    fun lastLetterGoesToTheLastReward() {
        assertEquals(PathNext.Reward(7), nextStep(letters, (1..28).toSet(), justLearned = 28, wasNew = true))
    }

    @Test
    fun reviewGoesHome() {
        assertEquals(PathNext.Home, nextStep(letters, (1..28).toSet(), justLearned = 4, wasNew = false))
    }
}
