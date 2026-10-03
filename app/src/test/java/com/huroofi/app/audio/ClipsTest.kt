package com.huroofi.app.audio

import com.huroofi.app.data.content.ContentJson
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipsTest {
    private val letters = ContentJson.decode(File("src/main/assets/letters.json").readText(Charsets.UTF_8)).letters

    @Test
    fun letterAndWordClipsComeFromJson() {
        val alif = letters.first()
        assertEquals(Clip("audio/letters/01_alif.mp3"), Clips.letter(alif))
        assertEquals(Clip("audio/words/01_lion.mp3"), Clips.word(alif))
    }

    @Test
    fun promptAndSfxNamesAreTheAgreedOnes() {
        val baa = letters[1]
        assertEquals("audio/prompts/where_is_duck.mp3", Clips.whereIs(baa).path)
        assertEquals("audio/prompts/colour_baa.mp3", Clips.colour(baa).path)
        assertEquals("audio/prompts/what_shall_we_play.mp3", Clips.whatShallWePlay.path)
        assertEquals("audio/prompts/which_starts_with.mp3", Clips.whichStartsWith.path)
        assertEquals("audio/sfx/boing.mp3", Clips.boing.path)
        assertEquals("audio/sfx/cheer.mp3", Clips.cheer.path)
        assertEquals("audio/sfx/praise_1.mp3", Clips.praise(1).path)
        assertEquals("audio/sfx/praise_3.mp3", Clips.praise(3).path)
        assertThrows(IllegalArgumentException::class.java) { Clips.praise(4) }
    }

    @Test
    fun allClipsAreUniqueMp3sUnderAudio() {
        val all = Clips.all(letters)
        assertEquals(all.size, all.map { it.path }.toSet().size)
        assertTrue(all.all { it.path.startsWith("audio/") && it.path.endsWith(".mp3") })
        // 28 letter + 28 word + 28 where_is + 28 colour + play + rest + which + 3 praise + boing + cheer
        assertEquals(28 * 4 + 3 + 3 + 2, all.size)
    }
}
