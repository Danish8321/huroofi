package com.huroofi.app.toddler.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.NotoNaskhArabic

/** The letter to paint over: pale pink fill, dashed pink edge. Drawn from the font (plan 05 decision 4). */
object LetterOutlineSpec {
    val FillColor = Color(0xFFFFF0F5)
    val EdgeColor = Color(0xFFF5A9C6)
    val EdgeWidth = 4.dp
    val Dash = 10.dp

    /** Font size as a share of the canvas height (prototype: 300 in a 380-high view box). */
    const val SIZE_SHARE = 300f / 380f
}

@Composable
fun LetterOutline(letter: String, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    Canvas(modifier) {
        val layout = measurer.measure(
            letter,
            TextStyle(
                fontFamily = NotoNaskhArabic,
                fontSize = (size.height * LetterOutlineSpec.SIZE_SHARE).toSp(),
                textDirection = TextDirection.Rtl,
            ),
        )
        val topLeft = Offset(
            (size.width - layout.size.width) / 2f,
            (size.height - layout.size.height) / 2f,
        )
        drawText(layout, LetterOutlineSpec.FillColor, topLeft, drawStyle = Fill)
        val dash = LetterOutlineSpec.Dash.toPx()
        drawText(
            layout,
            LetterOutlineSpec.EdgeColor,
            topLeft,
            drawStyle = Stroke(
                width = LetterOutlineSpec.EdgeWidth.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)),
            ),
        )
    }
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
