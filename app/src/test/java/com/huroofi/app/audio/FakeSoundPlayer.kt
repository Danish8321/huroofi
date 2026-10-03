package com.huroofi.app.audio

import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Records calls. [play] suspends until the test calls [finish], [stop] is called, another clip
 * starts, or the caller is cancelled — like [GatedSoundPlayer] over a real backend.
 */
class FakeSoundPlayer : SoundPlayer {
    val events = mutableListOf<String>()
    private var current: CancellableContinuation<Unit>? = null

    /** Paths passed to [play], in order. */
    val played: List<String> get() = events.filter { it.startsWith("play:") }.map { it.removePrefix("play:") }

    override suspend fun play(clip: Clip) {
        finish()
        events += "play:${clip.path}"
        suspendCancellableCoroutine { cont -> current = cont }
    }

    /** The clip that is playing reaches its end. */
    fun finish() {
        val cont = current
        current = null
        if (cont?.isActive == true) cont.resume(Unit)
    }

    override fun stop() {
        events += "stop"
        finish()
    }

    override fun release() = stop()
}
