package com.huroofi.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import java.io.IOException
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Platform MediaPlayer reading from assets. Not unit-testable on the JVM; verify on a device.
 * Any failure to open or play a clip ends it silently (a child must never see an error).
 * Each clip holds transient audio focus: other apps duck under it, and a call or another app taking
 * focus ends the clip; while another app holds focus (a call) the clip is skipped.
 */
class AndroidAudioBackend(context: Context) : AudioBackend {
    private val assets = context.applicationContext.assets
    private var player: MediaPlayer? = null
    private var pending: CancellableContinuation<Unit>? = null
    private val audio = context.applicationContext.getSystemService(AudioManager::class.java)
    private val speech = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()
    private val focus = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        .setAudioAttributes(speech)
        .setOnAudioFocusChangeListener { change ->
            if (change == AudioManager.AUDIOFOCUS_LOSS || change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) stop()
        }
        .build()

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
            mp.setAudioAttributes(speech)
            mp.prepare()
            if (audio.requestAudioFocus(focus) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mp.release()
                cont.resume(Unit)
                return@suspendCancellableCoroutine
            }
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
        if (player === mp) {
            player = null
            audio.abandonAudioFocusRequest(focus)
        }
        mp.release()
    }

    override fun stop() {
        player?.let { finish(it) }
    }

    override fun release() = stop()

    override fun isMuted() = audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0

    override fun showVolume() = audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
}
