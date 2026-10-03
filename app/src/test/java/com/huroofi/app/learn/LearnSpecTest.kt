package com.huroofi.app.learn

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.huroofi.app.toddler.isRed
import com.huroofi.app.toddler.paint.Crayon
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.TypeScale
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every Preschool / Early reader screen: touch ≥ 64 dp, text ≥ the type floor and no red (plan 07 decision 9). New screens are added here by hand. */
class LearnSpecTest {
    private class Screen(val name: String, val touchSizes: List<Dp>, val colors: List<Color>, val textSizes: List<Float> = emptyList())

    private val screens = listOf(
        Screen("Nav", listOf(NavSpec.Item), NavSpec.colors),
        Screen("Home", HomeSpec.touchSizes, HomeSpec.colors, HomeSpec.textSizes),
        Screen("Lesson", LessonSpec.touchSizes, LessonSpec.colors, LessonSpec.textSizes),
        Screen("Trace", TraceSpec.touchSizes, TraceSpec.colors),
        Screen("Quiz", QuizSpec.touchSizes, QuizSpec.colors, QuizSpec.textSizes),
        Screen("Reward", RewardSpec.touchSizes, RewardSpec.colors, RewardSpec.textSizes),
        Screen("StickerBook", StickerBookSpec.touchSizes, StickerBookSpec.colors, StickerBookSpec.textSizes),
        Screen("Map", MapSpec.touchSizes, MapSpec.colors, MapSpec.textSizes),
    )

    @Test
    fun everyControlIsAtLeast64dp() {
        for (s in screens) for (size in s.touchSizes) {
            assertTrue("${s.name}: $size", size >= HuroofiDimens.ToddlerMinTouch)
        }
    }

    @Test
    fun textIsAtLeastTheFloor() {
        for (s in screens) for (sp in s.textSizes) assertTrue("${s.name}: $sp sp", sp >= TypeScale.FLOOR_SP)
    }

    /** Crayons are the child's paint, not feedback: the toddler crayon allow-list (plan 05 decision 2). */
    private val crayonAllowList = Crayon.entries.map { it.color }

    @Test
    fun noRed() {
        for (s in screens) for (c in s.colors) assertFalse("${s.name}: $c", isRed(c) && c !in crayonAllowList)
    }

    @Test
    fun pinkCrayonIsNotRed() {
        assertFalse(isRed(TraceSpec.Pink))
    }
}
