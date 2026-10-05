package com.huroofi.app.parent

import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.TypeScale
import org.junit.Assert.assertTrue
import org.junit.Test

/** The Parent zone is for adults: touch ≥ 48 dp (plan 06 decision 9). Contrast is in ContrastSweepTest. */
class ZoneSpecTest {
    @Test
    fun touchTargetsAreAtLeast48dp() {
        for (size in ZoneSpec.touchSizes) assertTrue("$size", size >= 48.dp)
    }

    @Test
    fun textIsAtLeast16sp() {
        for (size in ZoneSpec.textSizes) assertTrue("$size", size.value >= TypeScale.FLOOR_SP)
    }
}
