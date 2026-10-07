package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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

/** Done-step circle fill, from the prototype's done pill. */
private val DoneFill = Color(0xFFD6F2C8)

/** The track between circles, as the Lesson card's edge. */
private val Track = Color(0xFFCFE2F7)

private val Circle = 30.dp

/** For the contrast sweep: Lesson and Trace add these. The track sits on Sky; labels are 16 sp, so normal text. */
val stepTabsPairs = listOf(
    ContrastPair(HuroofiTokens.Card, HuroofiTokens.Primary, large = false, "current step number"),
    ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Sky, large = true, "current step circle"),
    ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = false, "current and done step label"),
    ContrastPair(HuroofiTokens.Success, DoneFill, large = true, "done step tick"),
    ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "to-do step number"),
    ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "to-do step label"),
)

/** Tick path from `Trace.html`, 24-unit box. */
private const val TICK = "M5 12l5 5 9-10"

/**
 * Lesson / Trace / Quiz step indicator: circles on a track with the label under each. The current
 * step is a filled circle with its number, a finished step a green circle with a tick, a later step
 * an outlined circle with its number, so position never rests on colour alone. It is a progress
 * mark, not a control, so nothing looks pressable (plan 11 decision 2; was pills in a white bar).
 */
@Composable
fun StepTabs(
    steps: List<String>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    require(currentIndex in steps.indices) { "currentIndex out of range" }
    val colors = LocalHuroofiColors.current
    Row(modifier.fillMaxWidth()) {
        steps.forEachIndexed { i, label ->
            val current = i == currentIndex
            val done = i < currentIndex
            val state = when {
                current -> "current"
                done -> "done"
                else -> "to do"
            }
            Column(
                Modifier
                    .weight(1f)
                    .semantics(mergeDescendants = true) {
                        selected = current
                        contentDescription = "Step ${i + 1} of ${steps.size}, $label, $state"
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(Circle)
                        .drawBehind {
                            val y = size.height / 2
                            val width = 4.dp.toPx()
                            if (i > 0) drawLine(if (i <= currentIndex) colors.success else Track, Offset(0f, y), Offset(size.width / 2, y), width)
                            if (i < steps.lastIndex) drawLine(if (i < currentIndex) colors.success else Track, Offset(size.width / 2, y), Offset(size.width, y), width)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .size(Circle)
                            .background(
                                when {
                                    current -> colors.primary
                                    done -> DoneFill
                                    else -> colors.card
                                },
                                CircleShape,
                            )
                            .then(if (!current && !done) Modifier.border(2.dp, colors.muted, CircleShape) else Modifier),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (done) {
                            LineIcon(listOf(TICK), colors.success, size = 18.dp, strokeWidth = 3f)
                        } else {
                            Text(
                                "${i + 1}",
                                style = HuroofiText.caption.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (current) Color.White else colors.muted,
                            )
                        }
                    }
                }
                Text(
                    text = label,
                    style = HuroofiText.caption.copy(fontWeight = if (current) FontWeight.ExtraBold else FontWeight.Bold),
                    color = if (current || done) colors.navy else colors.muted,
                    maxLines = 1,
                )
            }
        }
    }
}
