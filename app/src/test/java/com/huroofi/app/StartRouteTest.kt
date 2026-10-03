package com.huroofi.app

import com.huroofi.app.data.progress.AgeMode
import org.junit.Assert.assertEquals
import org.junit.Test

class StartRouteTest {
    @Test
    fun toddlerStartsOnToddlerHome() {
        assertEquals(Routes.ToddlerHome, startRoute(AgeMode.TODDLER))
    }

    @Test
    fun preschoolAndReaderStartOnPlaceholderUntilPlan07() {
        assertEquals(Routes.Placeholder, startRoute(AgeMode.PRESCHOOL))
        assertEquals(Routes.Placeholder, startRoute(AgeMode.READER))
    }
}
