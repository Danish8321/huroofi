package com.huroofi.app.toddler.find

import com.huroofi.app.ui.components.useWideLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FindLayoutTest {
    @Test
    fun bigTabletLandscapeUsesTheTabletLayout() {
        assertTrue(useWideLayout(1280f, 800f))
        assertEquals(0.976f, tabletFindScale(1280f, 800f), 0.001f)
    }

    @Test
    fun smallTabletLandscapeScalesDownButKeeps64dpTargets() {
        assertTrue(useWideLayout(960f, 600f))
        assertEquals(0.732f, tabletFindScale(960f, 600f), 0.001f)
        assertEquals(64f, minTouch(84f, tabletFindScale(960f, 600f)), 0f)
        // The 20 sp English line would be 14.6 sp here.
        assertEquals(16f, minText(FindTabletSpec.ENGLISH_SP, tabletFindScale(960f, 600f)), 0f)
        assertEquals(20f, minText(FindTabletSpec.ENGLISH_SP, 1f), 0f)
    }

    @Test
    fun portraitAndSquareWindowsKeepThePhoneLayout() {
        assertFalse(useWideLayout(800f, 1280f))
        assertFalse(useWideLayout(840f, 840f))
    }

    @Test
    fun aWindowLargerThanTheDesignIsNotScaledUp() {
        assertEquals(1f, tabletFindScale(2000f, 1200f), 0f)
    }
}
