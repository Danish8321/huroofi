package com.huroofi.app.toddler

import com.huroofi.app.ui.theme.HuroofiDimens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToddlerActivityTest {
    @Test
    fun homeOffersAtMostThreeChoices() {
        assertTrue(ToddlerActivity.entries.size <= MAX_TODDLER_CHOICES)
    }

    @Test
    fun routesAreDistinct() {
        val routes = ToddlerActivity.entries.map { it.route }
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun noTileOrArtColourIsRed() {
        for (a in ToddlerActivity.entries) {
            with(a.tile) { for (c in listOf(background, border, shadow, text)) assertFalse("$a $c", isRed(c)) }
        }
        for (c in ToddlerHomeSpec.colors) assertFalse(c.toString(), isRed(c))
    }

    @Test
    fun tappableAreasMeetToddlerMinimum() {
        assertTrue(ToddlerHomeSpec.TileHeight >= HuroofiDimens.ToddlerMinTouch)
        assertTrue(ToddlerHomeSpec.BubbleHeight >= HuroofiDimens.ToddlerMinTouch)
    }
}
