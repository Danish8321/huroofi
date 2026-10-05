package com.huroofi.app.ui

import com.huroofi.app.Routes
import com.huroofi.app.learn.LearnRoutes
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SystemBarsTest {
    @Test
    fun onlyTheParentZoneShowsTheBars() {
        assertFalse(barsHidden(Routes.ParentZone))
    }

    @Test
    fun everyChildReachableScreenHidesTheBars() {
        val routes = listOf(
            Routes.Gate, Routes.ToddlerHome, Routes.ToddlerCards, Routes.ToddlerFind, Routes.ToddlerPaint, Routes.Rest,
            LearnRoutes.Home, LearnRoutes.Map, LearnRoutes.Stickers, LearnRoutes.Lesson, LearnRoutes.Trace,
            LearnRoutes.Quiz, LearnRoutes.Reward, null,
        )
        for (route in routes) assertTrue("$route", barsHidden(route))
    }
}
