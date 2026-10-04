package com.huroofi.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.min

/** Font size, in px, of the glyph drawn offscreen to find its ink. Layout scales linearly from it. */
private const val PROBE_PX = 200f

/**
 * One Naskh letter laid out so its drawn ink, not its line box, fills [share] of [size] and sits
 * centred. Short letters (د ر و) then fill a canvas as well as tall ones (أ ل). Draws offscreen, so
 * call it once per size, not every frame.
 */
fun fitGlyph(measurer: TextMeasurer, letter: String, size: Size, share: Float, density: Density): Pair<TextLayoutResult, Offset> {
    fun measure(fontPx: Float) = measurer.measure(
        letter,
        TextStyle(
            fontFamily = NotoNaskhArabic,
            fontSize = with(density) { fontPx.toSp() },
            textDirection = TextDirection.Rtl,
        ),
    )
    val probe = measure(PROBE_PX)
    val ink = inkBounds(probe, density)
        ?: return probe to Offset((size.width - probe.size.width) / 2f, (size.height - probe.size.height) / 2f)
    val scale = min(size.width * share / ink.width, size.height * share / ink.height)
    return measure(PROBE_PX * scale) to Offset(size.width / 2f - ink.center.x * scale, size.height / 2f - ink.center.y * scale)
}

/** How far to move a laid-out glyph so the centre of its [ink] lands on the centre of its [box]. */
fun inkShift(box: IntSize, ink: Rect): Offset = Offset(box.width / 2f - ink.center.x, box.height / 2f - ink.center.y)

/** The box around the pixels [layout] actually paints, or null when it paints none. */
internal fun inkBounds(layout: TextLayoutResult, density: Density): Rect? {
    val w = layout.size.width
    val h = layout.size.height
    if (w == 0 || h == 0) return null
    val bitmap = ImageBitmap(w, h)
    CanvasDrawScope().draw(density, LayoutDirection.Ltr, Canvas(bitmap), Size(w.toFloat(), h.toFloat())) {
        drawText(layout, Color.Black)
    }
    val pixels = bitmap.toPixelMap()
    var left = w
    var top = h
    var right = -1
    var bottom = -1
    for (y in 0 until h) for (x in 0 until w) {
        if (pixels[x, y].alpha > 0.5f) {
            if (x < left) left = x
            if (x > right) right = x
            if (y < top) top = y
            if (y > bottom) bottom = y
        }
    }
    return if (right < 0) null else Rect(left.toFloat(), top.toFloat(), right + 1f, bottom + 1f)
}
