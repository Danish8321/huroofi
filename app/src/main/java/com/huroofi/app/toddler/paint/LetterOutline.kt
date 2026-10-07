package com.huroofi.app.toddler.paint

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.fitGlyph

/**
 * The letter to paint over: pale pink fill, dashed pink edge. Drawn from the font (plan 05 decision 4).
 * Paint draws the fill itself, under its paint, and this edge over it ([fill] false; plan 12 decision 4).
 */
object LetterOutlineSpec {
    val FillColor = Color(0xFFFFF0F5)
    val EdgeColor = Color(0xFFEC4F8C)
    val EdgeWidth = 4.dp
    val Dash = 10.dp

    /** The glyph's ink fills this share of the canvas width or height, whichever binds first. */
    const val INK_SHARE = 0.75f
}

@Composable
fun LetterOutline(letter: String, modifier: Modifier = Modifier, fill: Boolean = true) {
    val measurer = rememberTextMeasurer()
    // Fitting draws offscreen, so it runs once per size, not on every frame.
    Spacer(modifier.drawWithCache {
        val (layout, topLeft) = fitGlyph(measurer, letter, size, LetterOutlineSpec.INK_SHARE, this)
        val dash = LetterOutlineSpec.Dash.toPx()
        val edge = Stroke(width = LetterOutlineSpec.EdgeWidth.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)))
        onDrawBehind {
            if (fill) drawText(layout, LetterOutlineSpec.FillColor, topLeft, drawStyle = Fill)
            drawText(layout, LetterOutlineSpec.EdgeColor, topLeft, drawStyle = edge)
        }
    })
}

@Preview(showBackground = true)
@Composable
private fun LetterOutlinePreview() {
    Row {
        for (l in listOf("أ", "ب", "ي", "ه")) {
            Box(Modifier.width(100.dp).aspectRatio(320f / 380f)) {
                LetterOutline(l, Modifier.matchParentSize())
            }
        }
    }
}
