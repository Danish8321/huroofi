package com.huroofi.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val WIGGLE_MS = 700

/**
 * The "I heard you" answer to a tap: a tilt left, a tilt right and a small grow, 0.7 s, as Look &
 * listen's picture always did (plan 11 decision 2). Call [play] on tap; draw with [Modifier.wiggle].
 */
class Wiggle internal constructor(private val scope: CoroutineScope) {
    internal val rotation = Animatable(0f)
    internal val scale = Animatable(1f)

    fun play() {
        scope.launch {
            rotation.animateTo(0f, keyframes {
                durationMillis = WIGGLE_MS
                -8f at 175
                8f at 525
            })
        }
        scope.launch {
            scale.animateTo(1f, keyframes {
                durationMillis = WIGGLE_MS
                1.08f at 175
                1.08f at 525
            })
        }
    }
}

@Composable
fun rememberWiggle(): Wiggle {
    val scope = rememberCoroutineScope()
    return remember { Wiggle(scope) }
}

fun Modifier.wiggle(wiggle: Wiggle): Modifier = graphicsLayer {
    rotationZ = wiggle.rotation.value
    scaleX = wiggle.scale.value
    scaleY = wiggle.scale.value
}
