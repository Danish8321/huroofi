package com.huroofi.app.learn

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas as GraphicsCanvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.huroofi.app.R
import com.huroofi.app.toddler.paint.Crayon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.fitGlyph

/** Sizes and colours of Trace (`Trace.html`, plan 07 decision 3). */
object TraceSpec {
    val Helper = 64.dp
    val CanvasCorner = 34.dp
    val EdgeColor = Color(0xFFCFE2F7)
    val Tolerance = 30.dp
    val SampleStep = 8.dp
    val Ink = 22.dp
    val CrayonSize = 64.dp
    val CrayonRing = 5.dp
    val Again = 64.dp
    val AgainWidth = 120.dp
    val AgainBorder = Color(0xFFA9CBF2)
    val Guide = HuroofiTokens.Navy.copy(alpha = 0.14f)

    /** The glyph's ink fills this share of the canvas width or height, whichever binds first. */
    const val INK_SHARE = 0.7f

    val Pink = Color(0xFFFF6FA5)

    /** The four toddler paints plus pink (prototype). Coral passes only via the crayon allow-list (plan 05 decision 2). */
    val crayons: List<Pair<Color, String>> = Crayon.entries.map { it.color to it.label } + (Pink to "Pink paint")

    val touchSizes = listOf(Helper, CrayonSize, Again, LessonSpec.Back)
    val colors = listOf(HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, EdgeColor, AgainBorder) +
        crayons.map { it.first }
}

/** Points are snapshot state, so the canvas redraws as a stroke grows. */
private class InkStroke(val color: Color, val points: SnapshotStateList<Offset>)

/** Draws the glyph offscreen and keeps grid points where it has ink. */
private fun glyphTargets(layout: TextLayoutResult, topLeft: Offset, size: IntSize, density: Density, direction: LayoutDirection, step: Int): List<Offset> {
    val bitmap = ImageBitmap(size.width, size.height)
    CanvasDrawScope().draw(density, direction, GraphicsCanvas(bitmap), Size(size.width.toFloat(), size.height.toFloat())) {
        drawText(layout, Color.Black, topLeft)
    }
    val pixels = bitmap.toPixelMap()
    return sampleMask(size.width, size.height, step) { x, y -> pixels[x, y].alpha > 0.5f }
}

/**
 * Trace over the faded glyph. Completes once on its own at [TRACE_NEED] coverage, or with "I did it!"
 * as soon as there is any ink (decision 3). Ink outside the letter is never punished.
 */
@Composable
fun TraceScreen(letter: String, onBack: () -> Unit, onDone: () -> Unit) {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val glyph = remember(letter, canvasSize) {
        if (canvasSize == IntSize.Zero) null else fitGlyph(measurer, letter, Size(canvasSize.width.toFloat(), canvasSize.height.toFloat()), TraceSpec.INK_SHARE, density)
    }
    val check = remember(glyph) {
        glyph?.let { (layout, topLeft) ->
            val step = with(density) { TraceSpec.SampleStep.roundToPx() }
            TraceCheck(glyphTargets(layout, topLeft, canvasSize, density, direction, step), with(density) { TraceSpec.Tolerance.toPx() })
        }
    }
    val strokes = remember { mutableStateListOf<InkStroke>() }
    var crayon by remember { mutableStateOf(TraceSpec.crayons.first().first) }
    var finished by remember { mutableStateOf(false) }
    // The gesture loop outlives recompositions; it reads the latest check and crayon through these.
    val currentCheck by rememberUpdatedState(check)
    val currentCrayon by rememberUpdatedState(crayon)

    fun finish() {
        if (!finished) {
            finished = true
            onDone()
        }
    }

    Column(
        Modifier.fillMaxSize().background(HuroofiTokens.Sky).safeDrawingPadding().padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PathHeader(PathStep.TRACE, "Back to lesson", onBack)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(painterResource(R.drawable.pic_lion), contentDescription = null, modifier = Modifier.size(TraceSpec.Helper))
            Text(
                "Trace the letter with your finger!",
                Modifier.weight(1f).background(HuroofiTokens.Card, RoundedCornerShape(20.dp)).padding(horizontal = 14.dp, vertical = 10.dp),
                style = HuroofiText.body.copy(fontWeight = FontWeight.Bold),
                color = HuroofiTokens.Navy,
            )
        }
        val cardShape = RoundedCornerShape(TraceSpec.CanvasCorner)
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .dropEdge(TraceSpec.EdgeColor, 8.dp, cardShape)
                .clip(cardShape)
                .background(HuroofiTokens.Card)
                .onSizeChanged { canvasSize = it }
                .semantics { contentDescription = "Tracing area for the letter $letter" }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        down.consume()
                        val stroke = InkStroke(currentCrayon, mutableStateListOf(down.position))
                        strokes += stroke
                        currentCheck?.addInk(down.position)
                        while (true) {
                            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            change.consume()
                            stroke.points += change.position
                            currentCheck?.addInk(change.position)
                        }
                        if (currentCheck?.done == true) finish()
                    }
                },
        ) {
            Canvas(Modifier.fillMaxSize()) {
                glyph?.let { (layout, topLeft) -> drawText(layout, TraceSpec.Guide, topLeft) }
                val width = TraceSpec.Ink.toPx()
                val style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
                for (s in strokes) {
                    if (s.points.size == 1) {
                        drawCircle(s.color, width / 2f, s.points[0])
                        continue
                    }
                    val path = Path().apply {
                        moveTo(s.points[0].x, s.points[0].y)
                        for (p in s.points.drop(1)) lineTo(p.x, p.y)
                    }
                    drawPath(path, s.color, style = style)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            for ((color, label) in TraceSpec.crayons) {
                val picked = color == crayon
                Box(
                    Modifier
                        .size(TraceSpec.CrayonSize)
                        .clip(CircleShape)
                        .background(color)
                        .border(TraceSpec.CrayonRing, if (picked) HuroofiTokens.Navy else HuroofiTokens.Card, CircleShape)
                        .semantics {
                            contentDescription = label
                            selected = picked
                        }
                        .clickable(role = Role.RadioButton) { crayon = color },
                )
            }
        }
        // Fixed height, so "I did it!" appearing never resizes the canvas.
        Row(
            Modifier.height(HuroofiDimens.PrimaryButtonHeight + HuroofiDimens.ButtonShadow),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                Modifier
                    .width(TraceSpec.AgainWidth)
                    .height(TraceSpec.Again)
                    .clip(RoundedCornerShape(22.dp))
                    .background(HuroofiTokens.Card)
                    .border(3.dp, TraceSpec.AgainBorder, RoundedCornerShape(22.dp))
                    .clickable(role = Role.Button) {
                        strokes.clear()
                        check?.clear()
                    },
                contentAlignment = Alignment.Center,
            ) { Text("Again", style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy) }
            if (strokes.isNotEmpty()) {
                Box(Modifier.weight(1f)) { PrimaryButton("I did it!", onClick = ::finish) }
            }
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun TracePreview() {
    HuroofiTheme { TraceScreen("ب", onBack = {}, onDone = {}) }
}
