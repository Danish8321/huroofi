package com.huroofi.app.audio

import android.content.Context
import android.media.MediaPlayer
import java.io.IOException
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Platform MediaPlayer reading from assets. Not unit-testable on the JVM; verify on a device.
 * Any failure to open or play a clip ends it silently (a child must never see an error).
 */
class AndroidAudioBackend(context: Context) : AudioBackend {
    private val assets = context.applicationContext.assets
    private var player: MediaPlayer? = null
    private var pending: CancellableContinuation<Unit>? = null

    override fun exists(path: String): Boolean = try {
        assets.openFd(path).use { true }
    } catch (_: IOException) {
        false
    }

    override suspend fun playToEnd(path: String) = suspendCancellableCoroutine { cont ->
        val mp = MediaPlayer()
        try {
            assets.openFd(path).use { mp.setDataSource(it.fileDescriptor, it.startOffset, it.length) }
            mp.setOnCompletionListener { finish(mp) }
            mp.setOnErrorListener { _, _, _ -> finish(mp); true }
            mp.prepare()
            player = mp
            pending = cont
            mp.start()
        } catch (_: IOException) {
            mp.release()
            cont.resume(Unit)
            return@suspendCancellableCoroutine
        } catch (_: IllegalStateException) {
            mp.release()
            cont.resume(Unit)
            return@suspendCancellableCoroutine
        }
        cont.invokeOnCancellation { release(mp) }
    }

    private fun finish(mp: MediaPlayer) {
        val cont = pending
        release(mp)
        pending = null
        if (cont?.isActive == true) cont.resume(Unit)
    }

    private fun release(mp: MediaPlayer) {
        if (player === mp) player = null
        mp.release()
    }

    override fun stop() {
        player?.let { finish(it) }
    }

    override fun release() = stop()
}
