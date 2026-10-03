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

    private fun wrong(set: FindSet) = set.round.options.first { it != set.round.target }

    @Test
    fun askPlaysTheWhereIsPrompt() = runTest {
        val (sound, audio) = setup()
        val set = game.start()
        audio.ask(set.round.target)
        runCurrent()
        assertEquals(listOf(Clips.whereIs(set.round.target).path), sound.played)
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
        assertEquals(listOf(Clips.boing.path, Clips.whereIs(after.round.target).path), sound.played)
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
            val (after, result) = game.tap(set, set.round.target)
            audio.onTap(result, after)
            drain(sound)
            praises += sound.played.filter { it.contains("praise") }.last()
            set = game.next(after)
        }
        praises.zipWithNext().forEach { (a, b) -> assertNotEquals(a, b) }
    }

    @Test
    fun thirdStarAlsoCheers() = runTest {
        val (sound, audio) = setup()
        var set = game.start()
        repeat(2) { set = game.next(game.tap(set, set.round.target).first) }
        val (after, result) = game.tap(set, set.round.target)
        audio.onTap(result, after)
        drain(sound)
        assertEquals(2, sound.played.size)
        assertEquals(Clips.cheer.path, sound.played.last())
    }
}
