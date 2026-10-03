package com.huroofi.app

import com.huroofi.app.data.progress.AgeMode
import com.huroofi.app.learn.LearnRoutes
import org.junit.Assert.assertEquals
import org.junit.Test

class StartRouteTest {
    @Test
    fun toddlerStartsOnToddlerHome() {
        assertEquals(Routes.ToddlerHome, startRoute(AgeMode.TODDLER))
    }

    @Test
    fun preschoolAndReaderStartOnHome() {
        assertEquals(LearnRoutes.Home, startRoute(AgeMode.PRESCHOOL))
        assertEquals(LearnRoutes.Home, startRoute(AgeMode.READER))
    }
}
