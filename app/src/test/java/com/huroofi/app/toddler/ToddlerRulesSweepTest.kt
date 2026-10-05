package com.huroofi.app.toddler

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.huroofi.app.parent.RestSpec
import com.huroofi.app.toddler.cards.CardsSpec
import com.huroofi.app.toddler.find.FindSpec
import com.huroofi.app.toddler.find.FindTabletSpec
import com.huroofi.app.toddler.find.tabletFindScale
import com.huroofi.app.toddler.paint.Crayon
import com.huroofi.app.toddler.paint.LetterOutlineSpec
import com.huroofi.app.toddler.paint.PaintSpec
import com.huroofi.app.ui.components.ParentLockSize
import com.huroofi.app.ui.components.ToddlerHomeButtonSpec
import com.huroofi.app.ui.theme.HuroofiDimens
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every toddler screen (and the Rest screen) against the toddler rules (plan 05). New screens are added here by hand. */
class ToddlerRulesSweepTest {
    private class Screen(val name: String, val touchSizes: List<Dp>, val colors: List<Color>, val choices: Int)

    private val screens = listOf(
        Screen(
            "Home",
            listOf(ToddlerHomeSpec.TileHeight, ToddlerHomeSpec.BubbleHeight),
            ToddlerHomeSpec.colors + ToddlerActivity.entries.flatMap {
                listOf(it.tile.background, it.tile.border, it.tile.shadow, it.tile.text)
            },
            ToddlerActivity.entries.size,
        ),
        Screen("Home button", listOf(ToddlerHomeButtonSpec.Size), listOf(ToddlerHomeButtonSpec.ShadowColor), 0),
        Screen("Look & listen", CardsSpec.touchSizes, CardsSpec.colors, CardsSpec.CHOICES),
        Screen("Find it", FindSpec.touchSizes, FindSpec.colors, FindSpec.CHOICES),
        Screen(
            "Find it (tablet, smallest landscape tablet)",
            FindTabletSpec.touchSizes(tabletFindScale(960f, 600f)),
            FindTabletSpec.colors,
            FindSpec.CHOICES,
        ),
        Screen(
            "Paint",
            PaintSpec.touchSizes,
            PaintSpec.colors + listOf(LetterOutlineSpec.FillColor, LetterOutlineSpec.EdgeColor),
            PaintSpec.CHOICES,
        ),
        Screen("Rest", RestSpec.touchSizes, RestSpec.colors, RestSpec.CHOICES),
    )

    /** Paint, not feedback (decision 2). */
    private val crayonAllowList = Crayon.entries.map { it.color }

    @Test
    fun everyControlIsAtLeast64dp() {
        for (s in screens) for (size in s.touchSizes) {
            assertTrue("${s.name}: $size", size >= HuroofiDimens.ToddlerMinTouch)
        }
    }

    /** The one named exception: the grown-ups' lock is small on purpose, but still ≥ 48 dp. */
    @Test
    fun parentLockIsAtLeast48dp() {
        assertTrue(ParentLockSize.Hit >= 48.dp)
    }

    @Test
    fun noRedExceptCrayons() {
        for (s in screens) for (c in s.colors) {
            assertFalse("${s.name}: $c", isRed(c) && c !in crayonAllowList)
        }
    }

    @Test
    fun atMostThreeChoices() {
        for (s in screens) assertTrue(s.name, s.choices <= MAX_TODDLER_CHOICES)
    }
}
