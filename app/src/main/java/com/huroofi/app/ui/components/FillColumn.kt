package com.huroofi.app.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The middle of a screen whose header and main button stay put: child [fill] takes the height the
 * others leave, never less than [fillMin], and only when even that does not fit does the middle
 * scroll. So a short window (tablet landscape, a large Display size) shrinks the big card before it
 * ever hides the button (plan 16). [edge] of the gap below is drawn into, so the scroll does not clip
 * the last child's drop edge; keep it no larger than that gap.
 */
@Composable
fun FillColumn(
    fill: Int,
    fillMin: Dp,
    modifier: Modifier = Modifier,
    gap: Dp = 16.dp,
    edge: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier) {
        val bounded = constraints.hasBoundedHeight
        val slot = if (bounded) constraints.maxHeight else 0
        val reach = Modifier.layout { m, c ->
            val e = if (bounded) edge.roundToPx() else 0
            val p = m.measure(c.copy(maxHeight = c.maxHeight + e))
            layout(p.width, maxOf(p.height - e, 0)) { p.place(0, 0) }
        }
        Layout(content, Modifier.fillMaxWidth().then(reach).verticalScroll(rememberScrollState())) { items, c ->
            val gapPx = gap.roundToPx()
            val loose = c.copy(minWidth = 0, minHeight = 0, maxHeight = Constraints.Infinity)
            val placed = arrayOfNulls<Placeable>(items.size)
            items.forEachIndexed { i, item -> if (i != fill) placed[i] = item.measure(loose) }
            val used = placed.sumOf { it?.height ?: 0 } + gapPx * (items.size - 1) + edge.roundToPx()
            val height = maxOf(fillMin.roundToPx(), slot + edge.roundToPx() - used)
            placed[fill] = items[fill].measure(c.copy(minWidth = 0, minHeight = height, maxHeight = height))
            layout(c.maxWidth, used + height) {
                var y = 0
                for (p in placed) {
                    p!!.place(0, y)
                    y += p.height + gapPx
                }
            }
        }
    }
}
