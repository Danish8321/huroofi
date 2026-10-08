package com.huroofi.app.toddler.find

import com.huroofi.app.audio.Clips
import com.huroofi.app.audio.FakeSoundPlayer
import com.huroofi.app.data.content.TestContent
import com.huroofi.app.toddler.PromptPlayer
import kotlin.random.Random
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FindAudioTest {
    private val letters = TestContent.repo.letters
    private val game = FindGame(letters, Random(3))

    private fun TestScope.setup(): Pair<FakeSoundPlayer, FindAudio> {
        val sound = FakeSoundPlayer()
        return sound to FindAudio(PromptPlayer(sound, backgroundScope), Random(5))
    }

    /** Lets every clip queued so far finish. */
    private fun TestScope.drain(sound: FakeSoundPlayer) {
        repeat(5) {
            runCurrent()
            sound.finish()
        }
        runCurrent()
    }

    private fun wrong(set: FindRound) = set.options.first { it != set.target }

    @Test
    fun askPlaysTheWhereIsPrompt() = runTest {
        val (sound, audio) = setup()
        val set = game.start()
        audio.ask(set.target)
        runCurrent()
        assertEquals(listOf(Clips.whereIs(set.target).path), sound.played)
    }

    @Test
    fun firstWrongTapBoingsThenRepeatsThePromptAfter300ms() = runTest {
        val (sound, audio) = setup()
        val start = game.start()
        val (after, result) = game.tap(start, wrong(start))
        audio.onTap(result, after)
        runCurrent()
        sound.finish()
        runCurrent()
        assertEquals(listOf(Clips.boing.path), sound.played)
        advanceTimeBy(299)
        runCurrent()
        assertEquals(listOf(Clips.boing.path), sound.played)
        advanceTimeBy(2)
        runCurrent()
        assertEquals(listOf(Clips.boing.path, Clips.whereIs(after.target).path), sound.played)
    }

    @Test
    fun laterWrongTapsOnlyBoing() = runTest {
        val (sound, audio) = setup()
        val start = game.start()
        val once = game.tap(start, wrong(start)).first
        val (twice, result) = game.tap(once, wrong(once))
        audio.onTap(result, twice)
        drain(sound)
        assertEquals(listOf(Clips.boing.path), sound.played)
    }

    @Test
    fun rightTapPraisesAndNeverTheSamePraiseTwiceInARow() = runTest {
        val (sound, audio) = setup()
        var set = game.start()
        val praises = mutableListOf<String>()
        repeat(30) {
            val (after, result) = game.tap(set, set.target)
            audio.onTap(result, after)
            drain(sound)
            praises += sound.played.filter { it.contains("praise") }.last()
            set = game.next(after)
        }
        praises.zipWithNext().forEach { (a, b) -> assertNotEquals(a, b) }
    }

    @Test
    fun rightTapsOnlyEverPraise() = runTest {
        val (sound, audio) = setup()
        var set = game.start()
        repeat(6) {
            val (after, result) = game.tap(set, set.target)
            audio.onTap(result, after)
            drain(sound)
            set = game.next(after)
        }
        assertEquals(6, sound.played.size)
        sound.played.forEach { assertTrue(it, it.contains("praise")) }
    }
}
