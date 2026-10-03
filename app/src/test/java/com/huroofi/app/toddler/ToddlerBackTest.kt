package com.huroofi.app.toddler

import com.huroofi.app.Routes
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
    fun parentZoneAndPlaceholderKeepDefaultBack() {
        assertEquals(BackAction.Default, back(Routes.ParentZone))
        assertEquals(BackAction.Default, back(Routes.Placeholder))
    }

    @Test
    fun outsideToddlerModeBackIsDefault() {
        for (route in listOf(Routes.Placeholder, Routes.Gate, Routes.ParentZone)) {
            assertEquals(route, BackAction.Default, toddlerBack(route, startRoute = Routes.Placeholder))
        }
    }
}
