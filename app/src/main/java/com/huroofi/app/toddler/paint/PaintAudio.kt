package com.huroofi.app.toddler.paint

import com.huroofi.app.audio.Clips
import com.huroofi.app.data.content.Letter
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.pickExcept
import kotlin.random.Random

/** Finger paint sounds (plan 05 slice 5). Each call cuts whatever is still playing. */
class PaintAudio(private val prompt: PromptPlayer, private val random: Random) {
    private var lastPraise: Int? = null

    /** On opening a letter and on the bubble. */
    fun ask(letter: Letter) {
        prompt.play(Clips.colour(letter))
    }

    /** When the letter is done. Never the same praise twice in a row. */
    fun celebrate() {
        val n = pickExcept((1..Clips.PRAISE_COUNT).toList(), lastPraise, random)
        lastPraise = n
        prompt.play(Clips.praise(n))
    }

    fun stop() = prompt.stop()
}
