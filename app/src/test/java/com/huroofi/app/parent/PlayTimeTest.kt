package com.huroofi.app.parent

import com.huroofi.app.Routes
import com.huroofi.app.data.progress.DAILY_LIMIT_OFF
import com.huroofi.app.learn.LearnRoutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class PlayTimeTest {
    private val grownUpRoutes = listOf(Routes.Gate, Routes.ParentZone, Routes.Rest)
    private val atLimit = 20 * 60
    private val under = atLimit - 1

    @Test
    fun childRoutesGoToRestAtTheLimit() {
        for (route in ChildRoutes) {
            assertEquals(route, RestMove.ToRest, restMove(route, atLimit, 20))
            assertEquals(route, RestMove.None, restMove(route, under, 20))
        }
    }

    @Test
    fun tracePracticeCountsAsPlay() {
        assertEquals(RestMove.ToRest, restMove(LearnRoutes.Practice, atLimit, 20))
    }

    @Test
    fun gateZoneAndRestAreNotChildRoutes() {
        for (route in grownUpRoutes) assertFalse(route, route in ChildRoutes)
        assertEquals(RestMove.None, restMove(Routes.Gate, atLimit, 20))
        assertEquals(RestMove.None, restMove(Routes.ParentZone, atLimit, 20))
        assertEquals(RestMove.None, restMove(null, atLimit, 20))
    }

    @Test
    fun restLeavesOnlyWhenUnderTheLimit() {
        assertEquals(RestMove.None, restMove(Routes.Rest, atLimit, 20))
        assertEquals(RestMove.LeaveRest, restMove(Routes.Rest, under, 20))
    }

    @Test
    fun theGameOpenWhenTimeRanOutMayFinishForThreeMinutes() {
        val find = Routes.ToddlerFind
        assertEquals(RestMove.None, restMove(find, atLimit, 20, graceRoute = find))
        assertEquals(RestMove.None, restMove(find, atLimit + GRACE_SECONDS - 1, 20, graceRoute = find))
        assertEquals(RestMove.ToRest, restMove(find, atLimit + GRACE_SECONDS, 20, graceRoute = find))
    }

    @Test
    fun movingToAnotherScreenAfterTimeRanOutGoesToRest() {
        assertEquals(RestMove.ToRest, restMove(Routes.ToddlerHome, atLimit, 20, graceRoute = Routes.ToddlerFind))
    }

    @Test
    fun noLimitNeverRests() {
        assertEquals(RestMove.None, restMove(Routes.ToddlerFind, 24 * 3600, DAILY_LIMIT_OFF))
        assertEquals(RestMove.LeaveRest, restMove(Routes.Rest, 24 * 3600, DAILY_LIMIT_OFF))
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
