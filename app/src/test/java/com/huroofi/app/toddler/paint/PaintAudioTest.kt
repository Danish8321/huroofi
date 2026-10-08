package com.huroofi.app.toddler.paint

import com.huroofi.app.audio.Clips
import com.huroofi.app.audio.FakeSoundPlayer
import com.huroofi.app.data.content.TestContent
import com.huroofi.app.toddler.PromptPlayer
import kotlin.random.Random
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaintAudioTest {
    private val repo = TestContent.repo

    private fun TestScope.setup(): Pair<FakeSoundPlayer, PaintAudio> {
        val sound = FakeSoundPlayer()
        return sound to PaintAudio(PromptPlayer(sound, backgroundScope), Random(5))
    }

    @Test
    fun askPlaysTheColourPrompt() = runTest {
        val (sound, audio) = setup()
        audio.ask(repo.letter(2))
        runCurrent()
        assertEquals(listOf(Clips.colour(repo.letter(2)).path), sound.played)
    }

    @Test
    fun newLetterCutsTheOldPrompt() = runTest {
        val (sound, audio) = setup()
        audio.ask(repo.letter(2))
        runCurrent()
        audio.ask(repo.letter(3))
        runCurrent()
        assertEquals(listOf(Clips.colour(repo.letter(2)).path, Clips.colour(repo.letter(3)).path), sound.played)
    }

    @Test
    fun starPraisesAndNeverTheSamePraiseTwiceInARow() = runTest {
        val (sound, audio) = setup()
        repeat(30) {
            audio.celebrate()
            runCurrent()
            sound.finish()
            runCurrent()
        }
        assertEquals(30, sound.played.size)
        sound.played.forEach { assertTrue(it, it.contains("praise")) }
        sound.played.zipWithNext().forEach { (a, b) -> assertNotEquals(a, b) }
    }
}
