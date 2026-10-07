package com.huroofi.app.ui.components

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalmMotionTest {
    @Test
    fun onlyAZeroAnimatorScaleIsCalm() {
        assertTrue(isCalmMotion(0f))
        assertFalse(isCalmMotion(0.5f))
        assertFalse(isCalmMotion(1f))
    }

    @Test
    fun teachingMotionRunsAtRealSpeed() {
        assertTrue(TeachingMotion.scaleFactor == 1f)
    }
}
