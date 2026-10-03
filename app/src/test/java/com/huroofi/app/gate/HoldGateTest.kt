package com.huroofi.app.gate

import org.junit.Assert.assertEquals
import org.junit.Test

class HoldGateTest {
    @Test
    fun holdingForThreeSecondsUnlocks() {
        val gate = HoldGate()
        gate.press(nowMillis = 0)
        gate.tick(nowMillis = 3_000)
        assertEquals(HoldState.Unlocked, gate.state)
    }

    @Test
    fun releasingEarlyResetsAndLaterTimeDoesNotUnlock() {
        val gate = HoldGate()
        gate.press(nowMillis = 0)
        gate.tick(nowMillis = 1_500)
        gate.release()
        assertEquals(HoldState.Idle, gate.state)
        gate.tick(nowMillis = 4_000)
        assertEquals(HoldState.Idle, gate.state)
    }

    @Test
    fun partwayThroughTheHoldReportsProgressForTheRing() {
        val gate = HoldGate()
        gate.press(nowMillis = 10_000)
        gate.tick(nowMillis = 11_500)
        assertEquals(HoldState.Holding(progress = 0.5f), gate.state)
    }

    @Test
    fun onceUnlockedItStaysUnlockedThroughReleaseAndNewPress() {
        val gate = HoldGate()
        gate.press(nowMillis = 0)
        gate.tick(nowMillis = 3_000)
        gate.release()
        assertEquals(HoldState.Unlocked, gate.state)
        gate.press(nowMillis = 5_000)
        gate.tick(nowMillis = 5_100)
        assertEquals(HoldState.Unlocked, gate.state)
    }
}
