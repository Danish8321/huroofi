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

    private fun states(completed: Set<Int>) = stageStates(TestContent.repo.stages, completed, stageOf)

    @Test
    fun freshMapHasStageOneCurrentAndTheRestLocked() {
        val s = states(emptySet())
        assertEquals(StageState.CURRENT, s[1])
        for (n in 2..7) assertEquals("$n", StageState.LOCKED, s[n])
    }

    @Test
    fun aStageInProgressStaysCurrent() {
        assertEquals(StageState.CURRENT, states(setOf(1, 2, 3))[1])
    }

    @Test
    fun finishingStageOneOpensStageTwo() {
        val s = states(setOf(1, 2, 3, 4))
        assertEquals(StageState.FINISHED, s[1])
        assertEquals(StageState.CURRENT, s[2])
        for (n in 3..7) assertEquals("$n", StageState.LOCKED, s[n])
    }

    @Test
    fun allLearnedFinishesEveryStageWithNoneCurrent() {
        val s = states((1..28).toSet())
        assertEquals(7, s.size)
        assertTrue(s.values.all { it == StageState.FINISHED })
    }

    @Test
    fun unlockAllOpensEveryLaterStageAndKeepsTheCurrentOne() {
        val s = stageStates(TestContent.repo.stages, setOf(1, 2, 3, 4, 9), stageOf, unlockAll = true)
        assertEquals(StageState.FINISHED, s[1])
        assertEquals(StageState.CURRENT, s[2])
        for (n in 3..7) assertEquals("$n", StageState.OPEN, s[n])
    }

    @Test
    fun finishingTheOpenStageUnlocksTheNext() {
        assertEquals(RewardNext.Unlocked(2), rewardNext(1, (1..4).toSet(), stageOf))
    }

    @Test
    fun finishingAStageAheadUnlocksNothing() {
        assertEquals(RewardNext.Nothing, rewardNext(3, (9..12).toSet(), stageOf))
    }

    @Test
    fun finishingTheOpenStageSkipsStagesAlreadyFinishedAhead() {
        assertEquals(RewardNext.Unlocked(4), rewardNext(2, (1..12).toSet(), stageOf))
    }

    @Test
    fun finishingTheLastGapIsAllLearned() {
        assertEquals(RewardNext.AllLearned, rewardNext(1, (1..28).toSet(), stageOf))
    }

    @Test
    fun replayNeverRepeatsTheLastLetter() {
        val stage = listOf(5, 6, 7, 8)
        repeat(50) { seed -> assertTrue(replayLetter(stage, 6, Random(seed)) in listOf(5, 7, 8)) }
    }

    @Test
    fun replayCanReachEveryOtherLetter() {
        val picks = (0 until 200).map { replayLetter(listOf(5, 6, 7, 8), 0, Random(it)) }.toSet()
        assertEquals(setOf(5, 6, 7, 8), picks)
    }

    @Test
    fun replayOfAOneLetterStageGivesThatLetter() {
        assertEquals(9, replayLetter(listOf(9), 9, Random(1)))
    }
}
