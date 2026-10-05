package com.huroofi.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.huroofi.app.data.content.TestContent
import com.huroofi.app.gate.GateSpec
import com.huroofi.app.learn.HomeSpec
import com.huroofi.app.learn.LessonSpec
import com.huroofi.app.learn.MapSpec
import com.huroofi.app.learn.QuizSpec
import com.huroofi.app.learn.RewardSpec
import com.huroofi.app.learn.StickerBookSpec
import com.huroofi.app.learn.TraceSpec
import com.huroofi.app.parent.RestSpec
import com.huroofi.app.parent.ZoneSpec
import com.huroofi.app.toddler.ToddlerHomeSpec
import com.huroofi.app.toddler.cards.CardsSpec
import com.huroofi.app.toddler.find.FindSpec
import com.huroofi.app.toddler.find.FindTabletSpec
import com.huroofi.app.toddler.paint.PaintSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every pair each screen declares, plus its stage pairs for all 7 stages (plan 08 decision 9). New screens are added here by hand. */
class ContrastSweepTest {
    private class Screen(
        val name: String,
        val pairs: List<ContrastPair>,
        val stagePairs: (StageColors) -> List<ContrastPair> = { emptyList() },
    )

    private val screens = listOf(
        Screen("Toddler Home", ToddlerHomeSpec.textPairs),
        Screen("Look & listen", CardsSpec.textPairs, CardsSpec::stagePairs),
        Screen("Find it", FindSpec.textPairs, FindSpec::stagePairs),
        Screen("Find it (tablet)", FindTabletSpec.textPairs, FindTabletSpec::stagePairs),
        Screen("Paint", PaintSpec.textPairs),
        Screen("Home", HomeSpec.textPairs, HomeSpec::stagePairs),
        Screen("Map", MapSpec.textPairs, MapSpec::stagePairs),
        Screen("Lesson", LessonSpec.textPairs, LessonSpec::stagePairs),
        Screen("Trace", TraceSpec.textPairs),
        Screen("Quiz", QuizSpec.textPairs, QuizSpec::stagePairs),
        Screen("Reward", RewardSpec.textPairs, RewardSpec::stagePairs),
        Screen("Sticker book", StickerBookSpec.textPairs, StickerBookSpec::stagePairs),
        Screen("Parent zone", ZoneSpec.textPairs),
        Screen("Rest", RestSpec.textPairs),
        Screen("Parent gate", GateSpec.textPairs),
    )

    private val stages = TestContent.repo.stages

    @Test
    fun everyStageIsChecked() {
        assertEquals(7, stages.size)
    }

    @Test
    fun everyPairMeetsAa() {
        val failures = screens.flatMap { s ->
            val all = s.pairs.map { "" to it } + stages.flatMap { st -> s.stagePairs(st.colors()).map { "stage ${st.stage} " to it } }
            all.mapNotNull { (stage, p) ->
                val ratio = contrastRatio(p.fg, p.bg)
                if (ratio >= p.required) null
                else "${s.name}: $stage${p.what} %.2f < %.1f".format(ratio, p.required)
            }
        }.distinct()
        assertTrue("${failures.size} pairs below AA:\n" + failures.joinToString("\n"), failures.isEmpty())
    }

    @Test
    fun contrastRatioKnownValues() {
        assertEquals(21.0, contrastRatio(Color.Black, Color.White), 0.01)
        assertEquals(1.0, contrastRatio(Color.White, Color.White), 0.001)
    }
}
