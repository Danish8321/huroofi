package com.huroofi.app.audio

/** Plays one clip at a time. A missing file is a silent no-op, so the app runs before recordings exist. */
interface SoundPlayer {
    /** Suspends until the clip has finished, was replaced by another, or [stop] was called. */
    suspend fun play(clip: Clip)

    fun stop()

    fun release()
}

/** Platform side of playback. Faked in tests; the MediaPlayer implementation is device-only. */
interface AudioBackend {
    fun exists(path: String): Boolean

    /** Returns when the clip ends, or when [stop] is called. Must honour coroutine cancellation. */
    suspend fun playToEnd(path: String)

    fun stop()

    fun release()
}

/**
 * Rules shared by every backend: voice toggle, one clip at a time, missing file is silent.
 * [voiceEnabled] reads the Parent-zone voice setting.
 */
class GatedSoundPlayer(
    private val backend: AudioBackend,
    private val voiceEnabled: suspend () -> Boolean,
) : SoundPlayer {
    override suspend fun play(clip: Clip) {
        if (!voiceEnabled()) return
        backend.stop()
        if (!backend.exists(clip.path)) return
        backend.playToEnd(clip.path)
    }

    override fun stop() = backend.stop()

    override fun release() = backend.release()
}
