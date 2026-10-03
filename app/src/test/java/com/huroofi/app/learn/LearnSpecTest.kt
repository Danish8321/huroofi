package com.huroofi.app.learn

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.huroofi.app.toddler.isRed
import com.huroofi.app.toddler.paint.Crayon
import com.huroofi.app.ui.theme.HuroofiDimens
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every Preschool / Early reader screen: touch ≥ 64 dp and no red (plan 07 decision 9). New screens are added here by hand. */
class LearnSpecTest {
    private class Screen(val name: String, val touchSizes: List<Dp>, val colors: List<Color>)

    private val screens = listOf(
        Screen("Nav", listOf(NavSpec.Item), NavSpec.colors),
        Screen("Home", HomeSpec.touchSizes, HomeSpec.colors),
        Screen("Lesson", LessonSpec.touchSizes, LessonSpec.colors),
        Screen("Trace", TraceSpec.touchSizes, TraceSpec.colors),
        Screen("Quiz", QuizSpec.touchSizes, QuizSpec.colors),
        Screen("Reward", RewardSpec.touchSizes, RewardSpec.colors),
    )

    @Test
    fun everyControlIsAtLeast64dp() {
        for (s in screens) for (size in s.touchSizes) {
            assertTrue("${s.name}: $size", size >= HuroofiDimens.ToddlerMinTouch)
        }
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
