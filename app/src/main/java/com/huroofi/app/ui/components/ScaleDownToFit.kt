package com.huroofi.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

/** How much [content] of size [w] × [h] shrinks to fit [maxW] × [maxH]; grows no larger than [most]. */
fun fitScale(w: Int, h: Int, maxW: Int, maxH: Int, most: Float = 1f): Float =
    minOf(most, maxW.toFloat() / w.coerceAtLeast(1), maxH.toFloat() / h.coerceAtLeast(1))

/**
 * Lays [content] out at its natural size and draws it scaled down, centred, when the space is
 * smaller (a short tablet landscape window, a narrow phone). At full size it is untouched. Text is
 * measured unwrapped, so a line shrinks instead of breaking. With [maxScale] above 1 it fills the
 * space it is given and grows the content up to that much (a tall wide-tablet tile).
 */
@Composable
fun ScaleDownToFit(modifier: Modifier = Modifier, maxScale: Float = 1f, content: @Composable () -> Unit) {
    Layout(content, modifier) { measurables, constraints ->
        val placeable = measurables.single().measure(Constraints())
        val grow = maxScale > 1f && constraints.hasBoundedWidth && constraints.hasBoundedHeight
        val width = if (grow) constraints.maxWidth else constraints.constrainWidth(placeable.width)
        val height = if (grow) constraints.maxHeight else constraints.constrainHeight(placeable.height)
        val scale = fitScale(placeable.width, placeable.height, width, height, maxScale)
        layout(width, height) {
            placeable.placeWithLayer((width - placeable.width) / 2, (height - placeable.height) / 2) {
                scaleX = scale
                scaleY = scale
            }
        }
    }
}
