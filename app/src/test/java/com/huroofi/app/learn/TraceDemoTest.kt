package com.huroofi.app.learn

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TraceDemoTest {
    private fun run(vararg events: DemoEvent): Pair<DemoState, List<DemoPlay>> {
        var state = DemoState()
        val plays = events.map { e -> reduceDemo(state, e).also { state = it.first }.second }
        return state to plays
    }

    private fun last(vararg events: DemoEvent) = run(*events).second.last()

    @Test fun `entry plays nothing until the sound is done`() =
        assertEquals(listOf(DemoPlay.Keep, DemoPlay.Keep), run(DemoEvent.Enter, DemoEvent.Idle(9_000)).second)

    @Test fun `sound done then plays the full demo`() =
        assertEquals(DemoPlay.All, last(DemoEvent.Enter, DemoEvent.SoundDone))

    @Test fun `first touch stops the demo`() =
        assertEquals(DemoPlay.Stop, last(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Touch))

    @Test fun `touch during the sound means no demo after it`() =
        assertEquals(DemoPlay.Keep, last(DemoEvent.Enter, DemoEvent.Touch, DemoEvent.SoundDone))

    @Test fun `five seconds idle replays the next stroke only`() =
        assertEquals(DemoPlay.Next, last(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Idle(5_000)))

    @Test fun `idle under five seconds does nothing`() =
        assertEquals(DemoPlay.Keep, last(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Idle(4_999)))

    @Test fun `idle adds up across ticks`() {
        val plays = run(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Idle(2_500), DemoEvent.Idle(2_500)).second
        assertEquals(listOf(DemoPlay.Keep, DemoPlay.All, DemoPlay.Keep, DemoPlay.Next), plays)
    }

    @Test fun `it repeats after each further five seconds`() {
        val plays = run(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Idle(5_000), DemoEvent.Idle(4_000), DemoEvent.Idle(1_000)).second
        assertEquals(listOf(DemoPlay.Next, DemoPlay.Keep, DemoPlay.Next), plays.drop(2))
    }

    @Test fun `new ink restarts the wait`() {
        val plays = run(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Idle(4_000), DemoEvent.Ink, DemoEvent.Idle(4_000), DemoEvent.Idle(1_000)).second
        assertEquals(listOf(DemoPlay.Keep, DemoPlay.Keep, DemoPlay.Next), plays.drop(3))
    }

    @Test fun `touch restarts the wait`() {
        val plays = run(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Idle(4_000), DemoEvent.Touch, DemoEvent.Idle(4_000)).second
        assertEquals(listOf(DemoPlay.Stop, DemoPlay.Keep), plays.drop(3))
    }

    @Test fun `helper tap replays the full demo even after touching`() =
        assertEquals(DemoPlay.All, last(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Touch, DemoEvent.HelperTap))

    @Test fun `helper tap restarts the idle wait`() =
        assertEquals(DemoPlay.Keep, last(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Idle(4_000), DemoEvent.HelperTap, DemoEvent.Idle(4_000)))

    @Test fun `again replays the full demo and clears done`() {
        val (state, plays) = run(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Done, DemoEvent.Again)
        assertEquals(DemoPlay.All, plays.last())
        assertFalse(state.done)
    }

    @Test fun `done stops the ball`() =
        assertEquals(DemoPlay.Stop, last(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Done))

    @Test fun `no idle replay once done`() =
        assertEquals(DemoPlay.Keep, last(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Done, DemoEvent.Idle(60_000)))

    @Test fun `idle replay works again after Again`() =
        assertEquals(DemoPlay.Next, last(DemoEvent.Enter, DemoEvent.SoundDone, DemoEvent.Done, DemoEvent.Again, DemoEvent.Idle(5_000)))
}
