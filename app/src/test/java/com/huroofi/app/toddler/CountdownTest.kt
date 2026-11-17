package com.huroofi.app.toddler

import org.junit.Assert.assertEquals
import org.junit.Test

/** Plan 16 slice 4: the countdown is wall time on the frame clock, so it never depends on animation scale. */
class CountdownTest {
    @Test
    fun staysEmptyThroughThePause() {
        assertEquals(0f, countdownProgress(0), 0f)
        assertEquals(0f, countdownProgress(CountdownSpec.PAUSE_MS.toLong()), 0f)
    }

    @Test
    fun fillsLinearlyThenStaysFull() {
        assertEquals(0.5f, countdownProgress(CountdownSpec.PAUSE_MS + CountdownSpec.FILL_MS / 2L), 1e-6f)
        assertEquals(1f, countdownProgress(CountdownSpec.TOTAL_MS.toLong()), 0f)
        assertEquals(1f, countdownProgress(CountdownSpec.TOTAL_MS + 10_000L), 0f)
    }

    @Test
    fun isNotFullBeforeFiveSeconds() {
        assertEquals(5_000, CountdownSpec.TOTAL_MS)
        assert(countdownProgress(CountdownSpec.TOTAL_MS - 1L) < 1f)
    }

    @Test
    fun aLongerTimeToTakeActionStretchesTheFill() {
        assertEquals(0.5f, countdownProgress(CountdownSpec.PAUSE_MS + 4_500L, totalMs = 10_000L), 1e-6f)
        assert(countdownProgress(9_999L, totalMs = 10_000L) < 1f)
    }
}
