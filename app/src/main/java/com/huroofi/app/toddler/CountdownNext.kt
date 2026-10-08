package com.huroofi.app.toddler

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors

/**
 * A Next that presses itself (plan 15 decision 4): it waits [CountdownSpec.PAUSE_MS] for the child
 * to enjoy the reward, fills over [CountdownSpec.FILL_MS], then fires. A tap fires at once.
 */
object CountdownSpec {
    const val PAUSE_MS = 1_000
    const val FILL_MS = 4_000
    const val TOTAL_MS = PAUSE_MS + FILL_MS

    /** Phone: a full-width bar (`.fill-next`). */
    val BarHeight = 96.dp
    val BarCorner = 30.dp
    val BarShadow = 7.dp
    val BarIcon = 48.dp
    val BarFill = Color(0xFF22A05C)

    /** Tablet: a ring round a round button (`.ring-next`). */
    val Ring = 120.dp
    val RingStroke = 8.dp
    val RingInset = 14.dp
    val RingShadow = 5.dp
    val RingIcon = 48.dp
    val RingTrack = HuroofiTokens.Navy.copy(alpha = 0.12f)

    const val LABEL = "Next. It starts by itself in a moment"
}

/** 0..1 over the countdown; calls [onDone] when it reaches 1. Restarts when [key] changes. */
@Composable
private fun countdown(key: Any, onDone: () -> Unit): Animatable<Float, *> {
    val done by rememberUpdatedState(onDone)
    val progress = remember(key) { Animatable(0f) }
    LaunchedEffect(progress) {
        progress.animateTo(1f, tween(CountdownSpec.FILL_MS, delayMillis = CountdownSpec.PAUSE_MS, easing = LinearEasing))
        done()
    }
    return progress
}

/** The phone's full-width green Next with a darker bar filling it. */
@Composable
fun FillNextButton(key: Any, onNext: () -> Unit, modifier: Modifier = Modifier, label: String = CountdownSpec.LABEL) {
    val spec = CountdownSpec
    val colors = LocalHuroofiColors.current
    val progress = countdown(key, onNext)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val shape = RoundedCornerShape(spec.BarCorner)
    Box(
        modifier
            .fillMaxWidth()
            .height(spec.BarHeight + spec.BarShadow)
            .semantics { contentDescription = label }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onNext),
    ) {
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(spec.BarHeight).background(colors.successShadow, shape))
        Box(
            Modifier
                .fillMaxWidth()
                .height(spec.BarHeight)
                .offset { IntOffset(0, if (pressed) spec.BarShadow.roundToPx() else 0) }
                .clip(shape)
                .background(colors.success),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.align(Alignment.CenterStart).fillMaxHeight().fillMaxWidth(progress.value).background(spec.BarFill))
            ChevronIcon(colors.card, pointsRight = true, size = spec.BarIcon)
        }
    }
}

/** The tablet's round Next inside a ring that fills clockwise from the top. */
@Composable
fun RingNextButton(
    key: Any,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = CountdownSpec.Ring,
    track: Color = CountdownSpec.RingTrack,
    label: String = CountdownSpec.LABEL,
) {
    val spec = CountdownSpec
    val colors = LocalHuroofiColors.current
    val progress = countdown(key, onNext)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val s = size / spec.Ring
    Box(
        modifier
            .size(size)
            .semantics { contentDescription = label }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onNext),
    ) {
        val success = colors.success
        Canvas(Modifier.fillMaxSize()) {
            val w = spec.RingStroke.toPx() * s
            val arc = Size(this.size.width - w, this.size.height - w)
            val at = Offset(w / 2f, w / 2f)
            drawArc(track, 0f, 360f, useCenter = false, topLeft = at, size = arc, style = Stroke(w))
            drawArc(success, -90f, 360f * progress.value, useCenter = false, topLeft = at, size = arc, style = Stroke(w, cap = StrokeCap.Round))
        }
        val inset = spec.RingInset * s
        Box(Modifier.fillMaxSize().padding(start = inset, end = inset, top = inset + spec.RingShadow * s, bottom = inset - spec.RingShadow * s).background(colors.successShadow, CircleShape))
        Box(
            Modifier
                .fillMaxSize()
                .padding(inset)
                .offset { IntOffset(0, if (pressed) (spec.RingShadow * s).roundToPx() else 0) }
                .background(success, CircleShape),
            contentAlignment = Alignment.Center,
        ) { ChevronIcon(colors.card, pointsRight = true, size = spec.RingIcon * s) }
    }
}
