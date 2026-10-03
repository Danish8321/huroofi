package com.huroofi.app.ui.components

import com.huroofi.app.ui.theme.HuroofiDimens
import org.junit.Assert.assertTrue
import org.junit.Test

class ToddlerHomeButtonTest {
    @Test
    fun meetsToddlerTouchMinimum() {
        assertTrue(ToddlerHomeButtonSpec.Size >= HuroofiDimens.ToddlerMinTouch)
    }
}
