package com.huroofi.app.parent

import com.huroofi.app.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayTimeTest {
    private val grownUpRoutes = listOf(Routes.Gate, Routes.ParentZone, Routes.Rest)

    @Test
    fun childRoutesGoToRestAtTheLimit() {
        for (route in ChildRoutes) {
            assertEquals(route, RestMove.ToRest, restMove(route, limitReached = true))
            assertEquals(route, RestMove.None, restMove(route, limitReached = false))
        }
    }

    @Test
    fun gateZoneAndRestAreNotChildRoutes() {
        for (route in grownUpRoutes) assertFalse(route, route in ChildRoutes)
        assertEquals(RestMove.None, restMove(Routes.Gate, limitReached = true))
        assertEquals(RestMove.None, restMove(Routes.ParentZone, limitReached = true))
        assertEquals(RestMove.None, restMove(null, limitReached = true))
    }

    @Test
    fun restLeavesOnlyWhenUnderTheLimit() {
        assertEquals(RestMove.None, restMove(Routes.Rest, limitReached = true))
        assertEquals(RestMove.LeaveRest, restMove(Routes.Rest, limitReached = false))
    }

    @Test
    fun unsavedSecondsCountTowardsTheLimit() {
        assertFalse(limitReached(storedSeconds = 290, pendingSeconds = 9, limitMinutes = 5))
        assertTrue(limitReached(storedSeconds = 290, pendingSeconds = 10, limitMinutes = 5))
    }

    @Test
    fun meterSavesEveryTenSeconds() {
        val meter = UsageMeter()
        repeat(9) { assertNull(meter.tick()) }
        assertEquals(10, meter.tick())
        assertEquals(0, meter.pending)
    }

    @Test
    fun drainReturnsTheRestOnce() {
        val meter = UsageMeter()
        repeat(13) { meter.tick() }
        assertEquals(3, meter.drain())
        assertEquals(0, meter.drain())
    }
}
