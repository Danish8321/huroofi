package com.huroofi.app.ui.components

import androidx.compose.ui.unit.dp
import com.huroofi.app.toddler.isRed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class StarBurstTest {
    @Test
    fun tenStarsAtThePrototypeSpots() {
        val stars = StarBurstSpec.stars(1f)
        assertEquals(10, stars.size)
        assertEquals(BurstStar((-120).dp, (-130).dp, 44.dp, -20f, StarBurstSpec.Light, 0), stars[0])
        assertEquals(BurstStar(0.dp, (-170).dp, 52.dp, 29f, StarBurstSpec.Fill, 50), stars[1])
    }

    @Test
    fun scaleSpreadsFullyButKeepsStarsReadable() {
        val small = StarBurstSpec.stars(0.5f)[1]
        assertEquals((-85).dp, small.dy)
        assertEquals(31.dp, small.size)
    }

    @Test
    fun everyThirdStarIsLightAndNoneIsRed() {
        StarBurstSpec.stars(1f).forEachIndexed { i, s ->
            assertEquals(if (i % 3 == 0) StarBurstSpec.Light else StarBurstSpec.Fill, s.color)
            assertFalse(isRed(s.color))
        }
    }
}
