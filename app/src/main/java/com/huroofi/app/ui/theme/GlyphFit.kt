package com.huroofi.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Font size, in px, of the glyph drawn offscreen to find its ink. Layout scales linearly from it. */
private const val PROBE_PX = 200f

/** A fitted glyph: its [layout], where to draw it ([topLeft]) and the box its ink covers on the canvas, in pixels. */
data class FittedGlyph(val layout: TextLayoutResult, val topLeft: Offset, val ink: Rect)

/**
 * One Naskh letter laid out so its drawn ink, not its line box, fills [share] of [size] and sits
 * centred. Short letters (د ر و) then fill a canvas as well as tall ones (أ ل). Draws offscreen, so
 * call it once per size, not every frame. Destructures as (layout, topLeft, ink).
 */
fun fitGlyph(measurer: TextMeasurer, letter: String, size: Size, share: Float, density: Density): FittedGlyph {
    fun measure(fontPx: Float) = measurer.measure(
        letter,
        TextStyle(
            fontFamily = NotoNaskhArabic,
            fontSize = with(density) { fontPx.toSp() },
            textDirection = TextDirection.Rtl,
        ),
    )
    val probe = measure(PROBE_PX)
    val ink = inkBounds(probe, density) ?: run {
        val topLeft = Offset((size.width - probe.size.width) / 2f, (size.height - probe.size.height) / 2f)
        return FittedGlyph(probe, topLeft, Rect(topLeft, Size(probe.size.width.toFloat(), probe.size.height.toFloat())))
    }
    val scale = min(size.width * share / ink.width, size.height * share / ink.height)
    val topLeft = Offset(size.width / 2f - ink.center.x * scale, size.height / 2f - ink.center.y * scale)
    val inkOnCanvas = Rect(topLeft.x + ink.left * scale, topLeft.y + ink.top * scale, topLeft.x + ink.right * scale, topLeft.y + ink.bottom * scale)
    return FittedGlyph(measure(PROBE_PX * scale), topLeft, inkOnCanvas)
}

/** A stroke point, as fractions (0..1) of the glyph's ink box, mapped into [ink] on the canvas (plan 09 decision 1). */
fun toCanvas(point: List<Float>, ink: Rect): Offset = Offset(ink.left + point[0] * ink.width, ink.top + point[1] * ink.height)

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

/**
 * Which canvas pixels a fitted [glyph] covers on a canvas of [size], to ask "is this point on the
 * letter?" (plan 12 decision 4). Draws offscreen, so build it once per glyph and size.
 */
class GlyphMask(glyph: FittedGlyph, size: IntSize, density: Density) {
    private val width = size.width
    private val height = size.height
    private val pixels = ImageBitmap(width.coerceAtLeast(1), height.coerceAtLeast(1)).also { bitmap ->
        CanvasDrawScope().draw(density, LayoutDirection.Ltr, Canvas(bitmap), Size(width.toFloat(), height.toFloat())) {
            drawText(glyph.layout, Color.Black, glyph.topLeft, drawStyle = Fill)
        }
    }.toPixelMap()

    /** True when [point], or any point [slopPx] away from it in eight directions, is on the letter. */
    fun contains(point: Offset, slopPx: Float): Boolean =
        (listOf(Offset.Zero) + List(8) { i -> Offset(cos(i * PI / 4).toFloat(), sin(i * PI / 4).toFloat()) * slopPx })
            .any { inked(point + it) }

    private fun inked(p: Offset): Boolean {
        val x = p.x.toInt()
        val y = p.y.toInt()
        return x in 0 until width && y in 0 until height && pixels[x, y].alpha > 0.5f
    }
}
