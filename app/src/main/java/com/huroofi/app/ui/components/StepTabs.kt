package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors

/** Done-step pill fill, from the prototype. */
private val DoneFill = Color(0xFFD6F2C8)

/** For the contrast sweep: Lesson and Trace add these. Labels are 16 sp, so normal text. */
val stepTabsPairs = listOf(
    ContrastPair(HuroofiTokens.Card, HuroofiTokens.Primary, large = false, "current step label"),
    ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = true, "current step pill on the bar"),
    ContrastPair(HuroofiTokens.Navy, DoneFill, large = false, "done step label"),
    ContrastPair(HuroofiTokens.Success, DoneFill, large = true, "done step tick"),
    ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "to-do step label"),
)

/** Tick path from `Trace.html`, 24-unit box. */
private const val TICK = "M5 12l5 5 9-10"

/**
 * Lesson / Trace / Quiz step indicator: one white bar, as in `Lesson.html` and `Trace.html`. The
 * current step is a filled pill with its number, a finished step a green pill with a tick, a later
 * step plain text with its number, so position never rests on colour alone. Not tappable.
 */
@Composable
fun StepTabs(
    steps: List<String>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    require(currentIndex in steps.indices) { "currentIndex out of range" }
    val colors = LocalHuroofiColors.current
    val pill = RoundedCornerShape(999.dp)
    Row(
        modifier = modifier.fillMaxWidth().background(colors.card, pill).padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        steps.forEachIndexed { i, label ->
            val current = i == currentIndex
            val done = i < currentIndex
            val state = when {
                current -> "current"
                done -> "done"
                else -> "to do"
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .background(
                        when {
                            current -> colors.primary
                            done -> DoneFill
                            else -> Color.Transparent
                        },
                        pill,
                    )
                    .semantics(mergeDescendants = true) {
                        selected = current
                        contentDescription = "Step ${i + 1} of ${steps.size}, $label, $state"
                    },
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (done) LineIcon(listOf(TICK), colors.success, size = 16.dp, strokeWidth = 3f)
                Text(
                    text = if (done) label else "${i + 1} $label",
                    style = HuroofiText.caption.copy(fontWeight = if (current || done) FontWeight.ExtraBold else FontWeight.Bold),
                    color = when {
                        current -> Color.White
                        done -> colors.navy
                        else -> colors.muted
                    },
                    maxLines = 1,
                )
            }
        }
    }
}
