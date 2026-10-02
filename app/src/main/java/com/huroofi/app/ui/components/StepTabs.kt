package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.LocalHuroofiColors

/**
 * Lesson / Trace / Quiz step indicator. Position is shown by number, bold label, an underline
 * bar and a thick border, not by colour alone. Finished steps get a tick.
 */
@Composable
fun StepTabs(
    steps: List<String>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    require(currentIndex in steps.indices) { "currentIndex out of range" }
    val colors = LocalHuroofiColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        steps.forEachIndexed { i, label ->
            val current = i == currentIndex
            val done = i < currentIndex
            val shape = RoundedCornerShape(16.dp)
            val state = when {
                current -> "current"
                done -> "done"
                else -> "to do"
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = HuroofiDimens.MinTouch)
                    .background(if (current) colors.card else colors.sky, shape)
                    .border(if (current) 4.dp else 2.dp, if (current) colors.primary else colors.muted, shape)
                    .semantics {
                        selected = current
                        contentDescription = "Step ${i + 1} of ${steps.size}, $label, $state"
                    }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (if (done) "✓ " else "${i + 1}. ") + label,
                        style = if (current) HuroofiText.sectionHeading else HuroofiText.caption,
                        color = colors.navy,
                    )
                    Box(
                        Modifier
                            .height(4.dp)
                            .fillMaxWidth(if (current) 1f else 0f)
                            .background(colors.primary, RoundedCornerShape(2.dp)),
                    )
                }
            }
        }
    }
}

