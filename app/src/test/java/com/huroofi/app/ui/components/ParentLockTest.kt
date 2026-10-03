package com.huroofi.app.ui.components

import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.HuroofiDimens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ParentLockTest {
    @Test
    fun visibleCircleIs44WithA48HitArea() {
        assertEquals(44.dp, ParentLockSize.Visible)
        assertEquals(48.dp, ParentLockSize.Hit)
    }

    @Test
    fun hitAreaMeetsGeneralMinimumButStaysUnderToddlerMinimum() {
        // Named exception to the 64 dp toddler rule (plan 05, decision 1): a toddler should not hit it.
        assertTrue(ParentLockSize.Hit >= HuroofiDimens.MinTouch)
        assertTrue(ParentLockSize.Hit < HuroofiDimens.ToddlerMinTouch)
    }
}
