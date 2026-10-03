package com.huroofi.app.toddler.cards

import com.huroofi.app.audio.Clips
import com.huroofi.app.data.content.Letter
import com.huroofi.app.toddler.PromptPlayer

/** Look & listen sounds (plan 05 decision 8). Every call cuts whatever is still playing. */
class CardAudio(private val prompt: PromptPlayer) {
    val speaking = prompt.speaking

    /** A card arrived (swipe or arrow): letter name, then the word. */
    fun onArrive(letter: Letter) {
        prompt.play(listOf(Clips.letter(letter), Clips.word(letter)))
    }

    /** The picture was tapped: the word only. */
    fun onPictureTap(letter: Letter) {
        prompt.play(Clips.word(letter))
    }

    fun stop() = prompt.stop()
}
