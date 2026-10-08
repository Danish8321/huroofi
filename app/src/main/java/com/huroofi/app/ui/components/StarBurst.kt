package com.huroofi.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.HuroofiTokens
import kotlin.math.roundToInt

/** One star of a [StarBurst]: where it lands from the centre, how big, how far it turns, when it leaves. */
data class BurstStar(val dx: Dp, val dy: Dp, val size: Dp, val rotation: Float, val color: Color, val delayMs: Int)

/** The prototype's celebration (`burst()` in `core.js`): ten stars fly out from one point. Only ever on success. */
object StarBurstSpec {
    /** dx, dy and size in dp at scale 1. */
    private val STARS = listOf(
        Triple(-120, -130, 44), Triple(0, -170, 52), Triple(118, -128, 40), Triple(-140, -20, 36), Triple(140, -30, 46),
        Triple(-110, 80, 40), Triple(110, 86, 34), Triple(-40, -80, 26), Triple(56, -70, 30), Triple(30, 120, 28),
    )
    const val FLY_MS = 900
    const val STAGGER_MS = 50
    val Light = Color(0xFFFFE07A)
    val Fill = HuroofiTokens.Sun
    val Edge = HuroofiTokens.SunShadow
    /** Stars never shrink below this share of their size, so a small burst still reads as stars. */
    const val MIN_SIZE_SCALE = 0.6f

    /** The stars at [scale]: spread scales fully, size no lower than [MIN_SIZE_SCALE]; every third star is light. */
    fun stars(scale: Float): List<BurstStar> = STARS.mapIndexed { i, (dx, dy, s) ->
        BurstStar(
            dx = (dx * scale).roundToInt().dp,
            dy = (dy * scale).roundToInt().dp,
            size = (s * maxOf(scale, MIN_SIZE_SCALE)).roundToInt().dp,
            rotation = (if (i % 2 == 1) 1f else -1f) * (20 + i * 9),
            color = if (i % 3 == 0) Light else Fill,
            delayMs = i * STAGGER_MS,
        )
    }
}

/** The prototype's `cubic-bezier(.2, .9, .3, 1.2)`: quick out, a little overshoot. */
private val BurstEasing = CubicBezierEasing(0.2f, 0.9f, 0.3f, 1.2f)

/**
 * Stars fly from a point [topFraction] down the box, centred across it, to their spots. Decorative;
 * fills its parent and takes no touches. Calm motion snaps them to where they land.
 */
@Composable
fun StarBurst(modifier: Modifier = Modifier, scale: Float = 1f, topFraction: Float = 0.44f) {
    val stars = remember(scale) { StarBurstSpec.stars(scale) }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val cx = maxWidth / 2
        val cy = maxHeight * topFraction
        for (star in stars) {
            val t = remember { Animatable(0f) }
            LaunchedEffect(Unit) { t.animateTo(1f, tween(StarBurstSpec.FLY_MS, delayMillis = star.delayMs, easing = BurstEasing)) }
            Box(
                Modifier
                    .offset(cx - star.size / 2, cy - star.size / 2)
                    .graphicsLayer {
                        val p = t.value
                        translationX = star.dx.toPx() * p
                        translationY = star.dy.toPx() * p
                        val grow = 0.2f + 0.8f * p
                        scaleX = grow
                        scaleY = grow
                        rotationZ = star.rotation * p
                        alpha = (p / 0.25f).coerceIn(0f, 1f)
                    },
            ) { StarIcon(fill = star.color, size = star.size, outline = StarBurstSpec.Edge, outlineWidth = 1.2f) }
        }
    }
}
