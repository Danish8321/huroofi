package com.huroofi.app.audio

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.resume

private class FakeBackend(private val existing: Set<String>) : AudioBackend {
    val started = mutableListOf<String>()
    val events = mutableListOf<String>()
    var stops = 0
    var released = false
    private var current: CancellableContinuation<Unit>? = null

    override fun exists(path: String) = path in existing

    override suspend fun playToEnd(path: String) {
        started += path
        events += "start:$path"
        suspendCancellableCoroutine { cont -> current = cont }
    }

    /** Simulates the clip reaching its end. */
    fun finishCurrent() {
        current?.resume(Unit)
        current = null
    }

    override fun stop() {
        stops++
        events += "stop"
        finishCurrent()
    }

    override fun release() {
        released = true
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SoundPlayerTest {
    private val clipA = Clip("audio/letters/01_alif.mp3")
    private val clipB = Clip("audio/words/01_lion.mp3")

    @Test
    fun playsAnExistingClipUntilItEnds() = runTest {
        val backend = FakeBackend(setOf(clipA.path))
        val player = GatedSoundPlayer(backend) { true }
        var done = false
        val job = launch(start = CoroutineStart.UNDISPATCHED) { player.play(clipA); done = true }
        assertEquals(listOf(clipA.path), backend.started)
        assertTrue(!done)
        backend.finishCurrent()
        job.join()
        assertTrue(done)
    }

    @Test
    fun missingFileIsASilentNoOp() = runTest {
        val backend = FakeBackend(emptySet())
        val player = GatedSoundPlayer(backend) { true }
        player.play(clipA)
        assertEquals(emptyList<String>(), backend.started)
    }

    @Test
    fun voiceOffPlaysNothingAndDoesNotInterruptACurrentClip() = runTest {
        val backend = FakeBackend(setOf(clipA.path, clipB.path))
        var voice = true
        val player = GatedSoundPlayer(backend) { voice }
        val job = launch(start = CoroutineStart.UNDISPATCHED) { player.play(clipA) }
        voice = false
        player.play(clipB)
        assertEquals(listOf(clipA.path), backend.started)
        assertEquals(1, backend.stops) // only the stop before clipA; voice-off call did not stop it
        backend.finishCurrent()
        job.join()
    }

    @Test
    fun onlyOneClipAtATime() = runTest {
        val backend = FakeBackend(setOf(clipA.path, clipB.path))
        val player = GatedSoundPlayer(backend) { true }
        val first = launch(start = CoroutineStart.UNDISPATCHED) { player.play(clipA) }
        val second = launch(start = CoroutineStart.UNDISPATCHED) { player.play(clipB) }
        assertEquals(listOf(clipA.path, clipB.path), backend.started)
        assertEquals(listOf("stop", "start:${clipA.path}", "stop", "start:${clipB.path}"), backend.events)
        first.join()
        backend.finishCurrent()
        second.join()
    }

    @Test
    fun stopAndReleaseReachTheBackend() {
        val backend = FakeBackend(emptySet())
        val player = GatedSoundPlayer(backend) { true }
        player.stop()
        player.release()
        assertEquals(1, backend.stops)
        assertTrue(backend.released)
    }
}
