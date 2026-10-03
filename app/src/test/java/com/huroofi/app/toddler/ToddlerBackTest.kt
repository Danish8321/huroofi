package com.huroofi.app.toddler

import com.huroofi.app.Routes
import com.huroofi.app.learn.LearnRoutes
import org.junit.Assert.assertEquals
import org.junit.Test

class ToddlerBackTest {
    private fun back(route: String) = toddlerBack(route, startRoute = Routes.ToddlerHome)

    @Test
    fun homeSwallowsBack() {
        assertEquals(BackAction.Swallow, back(Routes.ToddlerHome))
    }

    @Test
    fun activitiesAndGateGoBackToHome() {
        for (route in listOf(Routes.ToddlerCards, Routes.ToddlerFind, Routes.ToddlerPaint, Routes.Gate)) {
            assertEquals(route, BackAction.PopToToddlerHome, back(route))
        }
    }

    @Test
    fun everyActivityRouteIsCovered() {
        for (activity in ToddlerActivity.entries) {
            assertEquals(activity.route, BackAction.PopToToddlerHome, back(activity.route))
        }
    }

    @Test
    fun parentZoneAndLearnHomeKeepDefaultBack() {
        assertEquals(BackAction.Default, back(Routes.ParentZone))
        assertEquals(BackAction.Default, back(LearnRoutes.Home))
    }

    @Test
    fun outsideToddlerModeBackIsDefault() {
        for (route in listOf(LearnRoutes.Home, Routes.Gate, Routes.ParentZone)) {
            assertEquals(route, BackAction.Default, toddlerBack(route, startRoute = LearnRoutes.Home))
        }
    }

    @Test
    fun restSwallowsBackInEveryMode() {
        assertEquals(BackAction.Swallow, back(Routes.Rest))
        assertEquals(BackAction.Swallow, toddlerBack(Routes.Rest, startRoute = LearnRoutes.Home))
    }
}
