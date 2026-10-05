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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.HuroofiTokens
import kotlinx.coroutines.delay

/** Look and timing of the demo ball: a bright circle with a short fading trail, drawn in code. */
object DemoBallSpec {
    val Fill = HuroofiTokens.Sun
    val Edge = HuroofiTokens.Navy
    val Radius = 14.dp
    val EdgeWidth = 3.dp
    const val STROKE_MS = 1_500
    const val GAP_MS = 250L
    const val TRAIL_STEPS = 6
    const val TRAIL_GAP = 0.04f
}

/** One run of the ball: which strokes, in order. A new [id] restarts it even if [strokes] is the same. */
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
 * Overlay that moves the demo ball along [paths] (canvas pixels, one list per stroke) for each
 * stroke of [run], about 1.5 s apiece, then calls [onFinished]. A null [run] draws nothing. It has
 * no touch handling, so the canvas beneath keeps every gesture.
 */
@Composable
fun DemoBall(paths: List<List<Offset>>, run: DemoRun?, onFinished: () -> Unit, modifier: Modifier = Modifier) {
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
                delay(DemoBallSpec.GAP_MS)
            }
            stroke = s
            anim.snapTo(0f)
            anim.animateTo(1f, tween(DemoBallSpec.STROKE_MS, easing = LinearEasing)) { progress = value }
        }
        stroke = -1
        finished()
    }
    Canvas(modifier.fillMaxSize()) {
        val points = paths.getOrNull(stroke) ?: return@Canvas
        val radius = DemoBallSpec.Radius.toPx()
        for (k in DemoBallSpec.TRAIL_STEPS downTo 1) {
            val behind = progress - k * DemoBallSpec.TRAIL_GAP
            if (behind < 0f) continue
            val strength = 1f - k.toFloat() / (DemoBallSpec.TRAIL_STEPS + 1)
            drawCircle(DemoBallSpec.Fill.copy(alpha = 0.5f * strength), radius * (0.4f + 0.5f * strength), pointAlong(points, behind))
        }
        val at = pointAlong(points, progress)
        drawCircle(DemoBallSpec.Fill, radius, at)
        drawCircle(DemoBallSpec.Edge, radius, at, style = Stroke(DemoBallSpec.EdgeWidth.toPx()))
    }
}
