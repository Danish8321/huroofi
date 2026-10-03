package com.huroofi.app.toddler

import com.huroofi.app.audio.Clip
import com.huroofi.app.audio.FakeSoundPlayer
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptPlayerTest {
    private val a = Clip("a.mp3")
    private val b = Clip("b.mp3")
    private val c = Clip("c.mp3")

    private fun TestScope.setup(): Pair<FakeSoundPlayer, PromptPlayer> {
        val sound = FakeSoundPlayer()
        return sound to PromptPlayer(sound, backgroundScope)
    }

    @Test
    fun playsClipsInOrder() = runTest {
        val (sound, prompt) = setup()
        prompt.play(listOf(a, b))
        runCurrent()
        assertEquals(listOf("a.mp3"), sound.played)
        assertTrue(prompt.speaking.value)
        sound.finish()
        runCurrent()
        assertEquals(listOf("a.mp3", "b.mp3"), sound.played)
        sound.finish()
        runCurrent()
        assertFalse(prompt.speaking.value)
    }

    @Test
    fun newSequenceCutsTheRunningOne() = runTest {
        val (sound, prompt) = setup()
        prompt.play(listOf(a, b))
        runCurrent()
        prompt.play(c)
        runCurrent()
        sound.finish()
        runCurrent()
        assertEquals(listOf("a.mp3", "c.mp3"), sound.played)
        assertFalse(prompt.speaking.value)
    }

    @Test
    fun stopSilencesAndNothingFollows() = runTest {
        val (sound, prompt) = setup()
        prompt.play(listOf(a, b))
        runCurrent()
        prompt.stop()
        runCurrent()
        assertEquals(listOf("a.mp3"), sound.played)
        assertTrue(sound.events.last() == "stop")
        assertFalse(prompt.speaking.value)
    }
}
