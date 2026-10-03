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

    private fun solve(set: FindSet) = game.tap(set, set.round.target).first

    private fun wrong(set: FindSet) = set.round.options.first { it != set.round.target }

    @Test
    fun everyRoundHasTwoDifferentOptionsIncludingTheTarget() {
        var set = game.start()
        repeat(500) {
            val r = set.round
            assertEquals(2, r.options.size)
            assertTrue(r.target in r.options)
            assertNotEquals(r.options[0], r.options[1])
            set = game.next(solve(set))
        }
    }

    @Test
    fun targetNeverRepeatsTheLastOne() {
        var set = game.start()
        repeat(500) {
            val previous = set.round.target
            set = game.next(solve(set))
            assertNotEquals(previous, set.round.target)
        }
    }

    @Test
    fun wrongTapNudgesAndKeepsStars() {
        val solvedOnce = game.next(solve(game.start()))
        val (after, result) = game.tap(solvedOnce, wrong(solvedOnce))
        assertEquals(TapResult.NUDGE, result)
        assertEquals(1, after.stars)
        assertEquals(1, after.round.wrongTaps)
        assertFalse(after.round.solved)
    }

    @Test
    fun tapsAfterSolvingAreIgnored() {
        val solved = solve(game.start())
        assertEquals(TapResult.IGNORED, game.tap(solved, wrong(solved)).second)
        assertEquals(TapResult.IGNORED, game.tap(solved, solved.round.target).second)
    }

    @Test
    fun threeSolvesCompleteTheSetAndNextStartsAFreshOne() {
        var set = solve(game.start())
        assertEquals(1, set.stars)
        set = solve(game.next(set))
        set = solve(game.next(set))
        assertEquals(3, set.stars)
        assertTrue(set.complete)
        assertEquals(0, game.next(set).stars)
    }
}
