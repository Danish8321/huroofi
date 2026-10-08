package com.huroofi.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTokens

/** One step of the path: its label and its prototype icon (24-unit box); [filled] icons are solid shapes. */
data class StepTab(val label: String, val icon: List<String>, val filled: Boolean = false)

/** Sizes and colours of the step bar (`Lesson.html` / `Trace.html` `.steps`, plan 15 decision 6). */
object StepTabsSpec {
    val Height = 40.dp
    val NowIcon = 28.dp
    val Icon = 20.dp
    val Line = 4.dp
    val Track = Color(0xFFD5E2F0)
    const val LABEL_SP = 16f
}

/** For the contrast sweep: Lesson and Trace add these. The 16 sp ExtraBold label is normal text. */
val stepTabsPairs = listOf(
    ContrastPair(HuroofiTokens.Card, HuroofiTokens.Primary, large = false, "current step label"),
    ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = true, "current step icon"),
    ContrastPair(HuroofiTokens.Card, HuroofiTokens.Success, large = true, "done step tick"),
    ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = true, "to-do step icon"),
)

private val Tick = listOf("M5 12.5l4.5 4.5L19 7.5")

/**
 * Meet / Trace / Play progress: a green tick for a step done, a blue pill with icon and name for the
 * step now, a white circle with its icon for a step to come, joined by a line that turns green behind
 * you. Shape and label, not colour alone, tell the steps apart. A progress mark, not a control.
 */
@Composable
fun StepTabs(steps: List<StepTab>, currentIndex: Int, modifier: Modifier = Modifier) {
    require(currentIndex in steps.indices) { "currentIndex out of range" }
    val now = steps[currentIndex]
    Row(
        modifier.clearAndSetSemantics { contentDescription = "Step ${currentIndex + 1} of ${steps.size}, ${now.label}" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        steps.forEachIndexed { i, step ->
            when {
                i < currentIndex -> Box(
                    Modifier.size(StepTabsSpec.Height).background(HuroofiTokens.Success, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { LineIcon(Tick, HuroofiTokens.Card, size = StepTabsSpec.Icon, strokeWidth = 3f) }
                i == currentIndex -> Row(
                    Modifier
                        .height(StepTabsSpec.Height)
                        .background(HuroofiTokens.Primary, CircleShape)
                        .padding(start = 6.dp, end = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(Modifier.size(StepTabsSpec.NowIcon).background(HuroofiTokens.Card, CircleShape), contentAlignment = Alignment.Center) {
                        StepIcon(step, HuroofiTokens.Primary, 18.dp)
                    }
                    Text(
                        step.label,
                        style = HuroofiText.caption.copy(fontSize = StepTabsSpec.LABEL_SP.sp, fontWeight = FontWeight.ExtraBold),
                        color = HuroofiTokens.Card,
                        maxLines = 1,
                    )
                }
                else -> Box(
                    Modifier.size(StepTabsSpec.Height).background(HuroofiTokens.Card, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { StepIcon(step, HuroofiTokens.Muted, StepTabsSpec.Icon) }
            }
            if (i < steps.lastIndex) {
                Box(
                    Modifier
                        .weight(1f)
                        .sizeIn(minWidth = 8.dp)
                        .height(StepTabsSpec.Line)
                        .background(if (i < currentIndex) HuroofiTokens.Success else StepTabsSpec.Track, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun StepIcon(step: StepTab, color: Color, size: Dp) {
    if (!step.filled) {
        LineIcon(step.icon, color, size = size, strokeWidth = 2.2f)
        return
    }
    val parsed = remember(step.icon) { step.icon.map { PathParser().parsePathString(it).toPath() } }
    Canvas(Modifier.size(size)) {
        val u = this.size.width / 24f
        scale(u, u, pivot = Offset.Zero) { for (p in parsed) drawPath(p, color) }
    }
}
