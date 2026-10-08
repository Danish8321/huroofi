package com.huroofi.app.toddler.find

import com.huroofi.app.data.content.TestContent
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FindGameTest {
    private val letters = TestContent.repo.letters
    private val game = FindGame(letters, Random(42))

    private fun solve(set: FindRound) = game.tap(set, set.target).first

    private fun wrong(set: FindRound) = set.options.first { it != set.target }

    @Test
    fun everyRoundHasTwoDifferentOptionsIncludingTheTarget() {
        var set = game.start()
        repeat(500) {
            val r = set
            assertEquals(2, r.options.size)
            assertTrue(r.target in r.options)
            assertNotEquals(r.options[0], r.options[1])
            set = game.next(solve(set))
        }
    }

    @Test
    fun onlyToddlerWordsAreAskedOrOffered() {
        var set = game.start()
        val seen = mutableSetOf<String>()
        repeat(500) {
            set.options.forEach { assertTrue(it.meaningEn, it.toddlerWord); seen += it.meaningEn }
            set = game.next(solve(set))
        }
        assertEquals(letters.count { it.toddlerWord }, seen.size)
    }

    @Test
    fun targetNeverRepeatsTheLastOne() {
        var set = game.start()
        repeat(500) {
            val previous = set.target
            set = game.next(solve(set))
            assertNotEquals(previous, set.target)
        }
    }

    @Test
    fun wrongTapNudgesAndLeavesTheRoundOpen() {
        val start = game.start()
        val (after, result) = game.tap(start, wrong(start))
        assertEquals(TapResult.NUDGE, result)
        assertEquals(1, after.wrongTaps)
        assertEquals(wrong(start), after.lastWrong)
        assertFalse(after.solved)
    }

    @Test
    fun tapsAfterSolvingAreIgnored() {
        val solved = solve(game.start())
        assertEquals(TapResult.IGNORED, game.tap(solved, wrong(solved)).second)
        assertEquals(TapResult.IGNORED, game.tap(solved, solved.target).second)
    }

    @Test(expected = IllegalArgumentException::class)
    fun nextNeedsTheRoundSolved() {
        game.next(game.start())
    }

    @Test
    fun nextIsAFreshUnsolvedRound() {
        val start = game.start()
        val nudged = game.tap(start, wrong(start)).first
        val next = game.next(solve(nudged))
        assertFalse(next.solved)
        assertEquals(0, next.wrongTaps)
        assertEquals(null, next.lastWrong)
    }
}
