package com.huroofi.app.learn

import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.TestContent
import com.huroofi.app.data.progress.AgeMode
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizRoundTest {
    private val letters = TestContent.repo.letters
    private val baa = letters.first { it.index == 2 }

    private fun round(target: Letter = baa, count: Int = 4, seed: Int = 1) =
        quizRound(target, letters, count, Random(seed))

    @Test
    fun optionCountByMode() {
        assertEquals(3, optionCount(AgeMode.PRESCHOOL))
        assertEquals(4, optionCount(AgeMode.READER))
    }

    @Test
    fun roundHasTheAskedCountWithTheTargetOnce() {
        for (count in listOf(3, 4)) for (seed in 1..20) {
            val options = round(count = count, seed = seed)
            assertEquals(count, options.size)
            assertEquals(1, options.count { it.index == baa.index })
            assertEquals(count, options.map { it.index }.toSet().size)
        }
    }

    @Test
    fun noDistractorStartsWithTheTargetLetter() {
        for (target in letters) for (seed in 1..10) {
            val distractors = round(target, seed = seed).filter { it.index != target.index }
            assertTrue(target.letter, distractors.none { it.wordFirst == target.letter || it.wordFirst == target.wordFirst })
        }
    }

    @Test
    fun seedsVaryTheOrder() {
        val orders = (1..20).map { seed -> round(seed = seed).map { it.index } }.toSet()
        assertTrue(orders.size > 1)
        assertTrue("target should not always sit in one place", orders.map { it.indexOf(baa.index) }.toSet().size > 1)
    }

    @Test
    fun theRightPictureMovesEveryRound() {
        for (count in listOf(3, 4)) for (slot in 0 until count) for (seed in 1..30) {
            val options = quizRound(baa, letters, count, Random(seed), avoidSlot = slot)
            assertTrue("count $count slot $slot seed $seed", options[slot].index != baa.index)
            assertEquals(1, options.count { it.index == baa.index })
        }
    }

    @Test
    fun theRightPictureCanReachEveryOtherSlot() {
        val slots = (1..40).map { seed -> quizRound(baa, letters, 3, Random(seed), avoidSlot = 0).indexOfFirst { it.index == baa.index } }.toSet()
        assertEquals(setOf(1, 2), slots)
    }

    @Test
    fun targetSlotFindsTheRightPicture() {
        val game = QuizGame(baa, round())
        assertEquals(baa.index, game.options[game.targetSlot].index)
    }

    @Test
    fun wrongPickDimsButNeverAdvances() {
        val game = QuizGame(baa, round())
        val wrong = game.options.first { it.index != baa.index }
        val after = game.answer(wrong)
        assertEquals(0, after.roundsDone)
        assertFalse(after.solved)
        assertEquals(wrong, after.wrongPick)
        assertEquals(2, after.answer(wrong).wrongTaps)
    }

    @Test
    fun rightPickCountsTheRoundAndClearsTheDim() {
        val game = QuizGame(baa, round())
        val wrong = game.options.first { it.index != baa.index }
        val after = game.answer(wrong).answer(baa)
        assertEquals(1, after.roundsDone)
        assertTrue(after.solved)
        assertNull(after.wrongPick)
    }

    @Test
    fun tapsWhileSolvedAreIgnored() {
        val solved = QuizGame(baa, round()).answer(baa)
        assertSame(solved, solved.answer(baa))
    }

    @Test
    fun threeRightAnswersFinish() {
        var game = QuizGame(baa, round())
        repeat(QUIZ_ROUNDS) { i ->
            assertFalse(game.finished)
            game = game.answer(baa)
            if (i < QUIZ_ROUNDS - 1) game = game.nextRound(round(seed = i + 2))
        }
        assertTrue(game.finished)
        assertEquals(QUIZ_ROUNDS, game.roundsDone)
    }

    @Test
    fun nextRoundNeedsASolvedRound() {
        val game = QuizGame(baa, round())
        assertSame(game, game.nextRound(round(seed = 9)))
        val next = game.answer(baa).nextRound(round(seed = 9))
        assertFalse(next.solved)
        assertEquals(round(seed = 9), next.options)
    }

    @Test
    fun aRightAnswerBeforeTheLastRoundSaysNext() {
        val game = QuizGame(baa, round()).answer(baa)
        assertEquals("Great job! Next", quizNextLabel(game, next = null, allLearned = false))
    }

    @Test
    fun theLastRoundNamesWhereItGoes() {
        val last = QuizGame(baa, round(), roundsDone = QUIZ_ROUNDS - 1).answer(baa)
        assertTrue(last.finished)
        assertEquals("Next letter", quizNextLabel(last, PathNext.Meet(3), allLearned = false))
        assertEquals("Get your sticker", quizNextLabel(last, PathNext.Reward(1), allLearned = false))
        assertEquals("See your sticker", quizNextLabel(last, PathNext.Reward(7), allLearned = true))
        assertEquals("Back home", quizNextLabel(last, PathNext.Home, allLearned = false))
    }
}
