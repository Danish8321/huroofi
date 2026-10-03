package com.huroofi.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Decorative: the owning button supplies the content description. */
@Composable
fun LockIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 24.dp) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = w * 0.11f
        // shackle
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.26f, h * 0.08f),
            size = Size(w * 0.48f, h * 0.5f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        // body
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.16f, h * 0.42f),
            size = Size(w * 0.68f, h * 0.5f),
            cornerRadius = CornerRadius(w * 0.1f),
        )
    }
}

/** Decorative: the owning button supplies the content description. */
@Composable
fun SoundIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val speaker = Path().apply {
            moveTo(w * 0.12f, h * 0.38f)
            lineTo(w * 0.32f, h * 0.38f)
            lineTo(w * 0.56f, h * 0.18f)
            lineTo(w * 0.56f, h * 0.82f)
            lineTo(w * 0.32f, h * 0.62f)
            lineTo(w * 0.12f, h * 0.62f)
            close()
        }
        drawPath(speaker, color)
        val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        drawArc(
            color, -45f, 90f, false,
            Offset(w * 0.46f, h * 0.3f), Size(w * 0.3f, h * 0.4f), style = stroke,
        )
        drawArc(
            color, -45f, 90f, false,
            Offset(w * 0.4f, h * 0.14f), Size(w * 0.48f, h * 0.72f), style = stroke,
        )
    }
}

/** House outline from the toddler prototypes. Decorative: the owning button supplies the content description. */
@Composable
fun HomeIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 34.dp) {
    Canvas(modifier.size(size)) {
        val u = this.size.width / 24f
        val stroke = Stroke(width = 2.4f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val roof = Path().apply {
            moveTo(3f * u, 11f * u)
            lineTo(12f * u, 4f * u)
            lineTo(21f * u, 11f * u)
        }
        val body = Path().apply {
            moveTo(5f * u, 10f * u)
            lineTo(5f * u, 20f * u)
            lineTo(19f * u, 20f * u)
            lineTo(19f * u, 10f * u)
        }
        drawPath(roof, color, style = stroke)
        drawPath(body, color, style = stroke)
    }
}
