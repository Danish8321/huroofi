package com.huroofi.app.toddler.find

import com.huroofi.app.audio.Clips
import com.huroofi.app.data.content.Letter
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.pickExcept
import kotlin.random.Random
import kotlinx.coroutines.delay

/** Where's the…? sounds (plan 05 decision 13). Each call cuts whatever is still playing. */
class FindAudio(private val prompt: PromptPlayer, private val random: Random) {
    private var lastPraise: Int? = null

    /** On opening a round and on the sun button. */
    fun ask(target: Letter) {
        prompt.play(Clips.whereIs(target))
    }

    /** After a tap, given the state the tap produced. */
    fun onTap(result: TapResult, after: FindSet) {
        val target = after.round.target
        when (result) {
            TapResult.NUDGE ->
                if (after.round.wrongTaps == 1) {
                    prompt.run {
                        play(Clips.boing)
                        delay(HINT_DELAY_MS)
                        play(Clips.whereIs(target))
                    }
                } else {
                    prompt.play(Clips.boing)
                }
            TapResult.CORRECT -> {
                val n = pickExcept((1..Clips.PRAISE_COUNT).toList(), lastPraise, random)
                lastPraise = n
                val clips = listOf(Clips.praise(n)) + if (after.complete) listOf(Clips.cheer) else emptyList()
                prompt.play(clips)
            }
            TapResult.IGNORED -> Unit
        }
    }

    fun stop() = prompt.stop()

    companion object {
        const val HINT_DELAY_MS = 300L
    }
}
