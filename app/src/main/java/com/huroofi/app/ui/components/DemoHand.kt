package com.huroofi.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.HuroofiTokens
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Look and timing of the demo hand: a white-gloved pointing hand drawn in code, its fingertip on
 * the stroke, with a short fading trail (plan 12 decision 1; was a ball).
 */
object DemoHandSpec {
    val Fill = HuroofiTokens.Card
    val Edge = HuroofiTokens.Navy
    val Trail = HuroofiTokens.Sun
    /** Fingertip to wrist. */
    val Size = 64.dp
    val EdgeWidth = 3.dp
    val TrailRadius = 12.dp
    /** The hand leans this far, so it points up and to the left like a reaching hand. */
    const val TILT_DEGREES = -20f
    const val STROKE_MS = 1_500
    const val GAP_MS = 250L
    const val TRAIL_STEPS = 6
    const val TRAIL_GAP = 0.04f
}

/** One run of the hand: which strokes, in order. A new [id] restarts it even if [strokes] is the same. */
data class DemoRun(val strokes: List<Int>, val id: Int)

/** The point [fraction] (0..1) of the way along [points], by length. Empty gives [Offset.Zero]. */
fun pointAlong(points: List<Offset>, fraction: Float): Offset {
    if (points.isEmpty()) return Offset.Zero
    if (points.size == 1) return points[0]
    val lengths = points.zipWithNext { a, b -> (b - a).getDistance() }
    val total = lengths.sum()
    if (total == 0f) return points[0]
    var left = fraction.coerceIn(0f, 1f) * total
    for (i in lengths.indices) {
        if (left <= lengths[i] || i == lengths.lastIndex) {
            val t = if (lengths[i] == 0f) 0f else (left / lengths[i]).coerceIn(0f, 1f)
            return points[i] + (points[i + 1] - points[i]) * t
        }
        left -= lengths[i]
    }
    return points.last()
}

/**
 * Overlay that moves the demo hand along [paths] (canvas pixels, one list per stroke) for each
 * stroke of [run], about 1.5 s apiece, then calls [onFinished]. A null [run] draws nothing. It has
 * no touch handling, so the canvas beneath keeps every gesture.
 */
@Composable
fun DemoHand(paths: List<List<Offset>>, run: DemoRun?, onFinished: () -> Unit, modifier: Modifier = Modifier) {
    var stroke by remember { mutableIntStateOf(-1) }
    var progress by remember { mutableFloatStateOf(0f) }
    val finished by rememberUpdatedState(onFinished)
    LaunchedEffect(run, paths) {
        stroke = -1
        if (run == null || paths.isEmpty()) return@LaunchedEffect
        val anim = Animatable(0f)
        for ((n, s) in run.strokes.withIndex()) {
            if (s !in paths.indices) continue
            if (n > 0) {
                stroke = -1
                delay(DemoHandSpec.GAP_MS)
            }
            stroke = s
            anim.snapTo(0f)
            // The hand teaches the stroke, so it moves even when the phone removes animations (plan 14 decision 3).
            withContext(TeachingMotion) {
                anim.animateTo(1f, tween(DemoHandSpec.STROKE_MS, easing = LinearEasing)) { progress = value }
            }
        }
        stroke = -1
        finished()
    }
    Canvas(modifier.fillMaxSize()) {
        val points = paths.getOrNull(stroke) ?: return@Canvas
        val radius = DemoHandSpec.TrailRadius.toPx()
        for (k in DemoHandSpec.TRAIL_STEPS downTo 1) {
            val behind = progress - k * DemoHandSpec.TRAIL_GAP
            if (behind < 0f) continue
            val strength = 1f - k.toFloat() / (DemoHandSpec.TRAIL_STEPS + 1)
            drawCircle(DemoHandSpec.Trail.copy(alpha = 0.6f * strength), radius * (0.4f + 0.6f * strength), pointAlong(points, behind))
        }
        drawHand(pointAlong(points, progress))
    }
}

/**
 * A pointing hand whose fingertip's round end is centred on [tip]: index finger up, the other
 * fingers curled into a fist below and to the right. Built in units of [DemoHandSpec.Size].
 */
private fun DrawScope.drawHand(tip: Offset) {
    val u = DemoHandSpec.Size.toPx()
    val finger = 0.24f * u
    val origin = Offset(tip.x - finger / 2f, tip.y - finger / 2f)
    fun box(left: Float, top: Float, right: Float, bottom: Float, corner: Float) = Path().apply {
        addRoundRect(
            RoundRect(
                Rect(origin.x + left * u, origin.y + top * u, origin.x + right * u, origin.y + bottom * u),
                CornerRadius(corner * u),
            ),
        )
    }
    var hand = box(0f, 0f, 0.24f, 0.62f, 0.12f)
    for (part in listOf(
        box(-0.04f, 0.42f, 0.66f, 1f, 0.2f), // fist
        box(0.2f, 0.36f, 0.44f, 0.6f, 0.12f), // curled middle finger
        box(0.4f, 0.4f, 0.64f, 0.64f, 0.12f), // curled ring finger
        box(-0.16f, 0.5f, 0.12f, 0.72f, 0.11f), // thumb
    )) hand = Path().apply { op(hand, part, PathOperation.Union) }
    rotate(DemoHandSpec.TILT_DEGREES, pivot = tip) {
        drawPath(hand, DemoHandSpec.Fill)
        drawPath(hand, DemoHandSpec.Edge, style = Stroke(DemoHandSpec.EdgeWidth.toPx(), join = StrokeJoin.Round))
    }
}
