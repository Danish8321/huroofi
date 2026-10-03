package com.huroofi.app.toddler.cards

import com.huroofi.app.audio.Clips
import com.huroofi.app.audio.FakeSoundPlayer
import com.huroofi.app.data.content.TestContent
import com.huroofi.app.toddler.PromptPlayer
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CardAudioTest {
    private val alif = TestContent.repo.letter(1)
    private val baa = TestContent.repo.letter(2)

    @Test
    fun arrivalPlaysLetterThenWord() = runTest {
        val sound = FakeSoundPlayer()
        val audio = CardAudio(PromptPlayer(sound, backgroundScope))
        audio.onArrive(alif)
        runCurrent()
        sound.finish()
        runCurrent()
        assertEquals(listOf(Clips.letter(alif).path, Clips.word(alif).path), sound.played)
    }

    @Test
    fun pictureTapPlaysOnlyTheWord() = runTest {
        val sound = FakeSoundPlayer()
        val audio = CardAudio(PromptPlayer(sound, backgroundScope))
        audio.onPictureTap(alif)
        runCurrent()
        sound.finish()
        runCurrent()
        assertEquals(listOf(Clips.word(alif).path), sound.played)
    }

    @Test
    fun newArrivalCutsTheClipStillPlaying() = runTest {
        val sound = FakeSoundPlayer()
        val audio = CardAudio(PromptPlayer(sound, backgroundScope))
        audio.onArrive(alif)
        runCurrent()
        audio.onArrive(baa)
        runCurrent()
        sound.finish()
        runCurrent()
        sound.finish()
        runCurrent()
        assertEquals(
            listOf(Clips.letter(alif).path, Clips.letter(baa).path, Clips.word(baa).path),
            sound.played,
        )
    }
}
