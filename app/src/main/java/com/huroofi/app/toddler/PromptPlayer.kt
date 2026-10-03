package com.huroofi.app.toddler

import com.huroofi.app.audio.Clip
import com.huroofi.app.audio.SoundPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * One spoken sequence at a time for a toddler screen. Starting a new sequence cuts the running one
 * (plan 05 decisions 8 and 12). [speaking] is true while a sequence runs, for talking animations.
 */
class PromptPlayer(private val sound: SoundPlayer, private val scope: CoroutineScope) {
    private var job: Job? = null
    private val _speaking = MutableStateFlow(false)
    val speaking: StateFlow<Boolean> = _speaking.asStateFlow()

    /** Plays [clips] in order, replacing whatever is playing. */
    fun play(clips: List<Clip>): Job = run { for (c in clips) play(c) }

    fun play(clip: Clip): Job = play(listOf(clip))

    /** Runs [sequence] (clips, pauses) against the player, replacing whatever is playing. */
    fun run(sequence: suspend SoundPlayer.() -> Unit): Job {
        cancelCurrent()
        lateinit var self: Job
        self = scope.launch(start = CoroutineStart.LAZY) {
            _speaking.value = true
            try {
                sound.sequence()
            } finally {
                if (job === self) _speaking.value = false
            }
        }
        job = self
        self.start()
        return self
    }

    fun stop() {
        cancelCurrent()
        _speaking.value = false
    }

    private fun cancelCurrent() {
        job?.cancel()
        job = null
        sound.stop()
    }
}
