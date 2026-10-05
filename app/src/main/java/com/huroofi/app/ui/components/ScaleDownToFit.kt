package com.huroofi.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

/** How much [content] of size [w] × [h] shrinks to fit [maxW] × [maxH]; never grows. */
fun fitScale(w: Int, h: Int, maxW: Int, maxH: Int): Float =
    minOf(1f, maxW.toFloat() / w.coerceAtLeast(1), maxH.toFloat() / h.coerceAtLeast(1))

/**
 * Lays [content] out at its natural size and draws it scaled down, centred, when the space is
 * smaller (a short tablet landscape window). At full size it is untouched.
 */
@Composable
fun ScaleDownToFit(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(content, modifier) { measurables, constraints ->
        val placeable = measurables.single().measure(Constraints(maxWidth = constraints.maxWidth))
        val width = constraints.constrainWidth(placeable.width)
        val height = constraints.constrainHeight(placeable.height)
        val scale = fitScale(placeable.width, placeable.height, width, height)
        layout(width, height) {
            placeable.placeWithLayer((width - placeable.width) / 2, (height - placeable.height) / 2) {
                scaleX = scale
                scaleY = scale
            }
        }
    }
}
