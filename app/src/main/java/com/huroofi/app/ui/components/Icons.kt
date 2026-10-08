package com.huroofi.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
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

/** Chevron pointing left ([pointsRight] false) or right. Decorative. */
@Composable
fun ChevronIcon(color: Color, pointsRight: Boolean, modifier: Modifier = Modifier, size: Dp = 44.dp) {
    Canvas(modifier.size(size)) {
        val u = this.size.width / 24f
        val x = if (pointsRight) listOf(9f, 16f, 9f) else listOf(15f, 8f, 15f)
        val path = Path().apply {
            moveTo(x[0] * u, 5f * u)
            lineTo(x[1] * u, 12f * u)
            lineTo(x[2] * u, 19f * u)
        }
        drawPath(path, color, style = Stroke(width = 3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Five-point star from the toddler prototypes, filled with [fill] and outlined. Decorative. */
@Composable
fun StarIcon(
    fill: Color,
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    outline: Color = Color(0xFF26324A),
    outlineWidth: Float = 1.4f,
) {
    Canvas(modifier.size(size)) {
        val u = this.size.width / 24f
        val points = listOf(
            12f to 2.8f, 14.8f to 8.6f, 21.1f to 9.5f, 16.5f to 13.9f, 17.6f to 20.2f,
            12f to 17.2f, 6.4f to 20.2f, 7.5f to 13.9f, 2.9f to 9.5f, 9.2f to 8.6f,
        )
        val star = Path().apply {
            points.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x * u, y * u) else lineTo(x * u, y * u) }
            close()
        }
        drawPath(star, fill)
        drawPath(star, outline, style = Stroke(width = outlineWidth * u, join = StrokeJoin.Round))
    }
}

/** Eraser over a line, from `ToddlerPaint.html`. Decorative: the owning button supplies the content description. */
@Composable
fun WipeIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Canvas(modifier.size(size)) {
        val u = this.size.width / 24f
        val stroke = Stroke(width = 2.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val line = Path().apply {
            moveTo(7f * u, 21f * u)
            lineTo(17f * u, 21f * u)
        }
        val eraser = Path().apply {
            moveTo(5f * u, 16f * u)
            lineTo(14f * u, 7f * u)
            lineTo(19f * u, 12f * u)
            lineTo(13f * u, 18f * u)
            lineTo(8f * u, 18f * u)
            close()
        }
        val band = Path().apply {
            moveTo(10f * u, 11f * u)
            lineTo(15f * u, 16f * u)
        }
        drawPath(line, color, style = stroke)
        drawPath(eraser, color, style = stroke)
        drawPath(band, color, style = stroke)
    }
}

/** Prototype button icons as SVG path data for [LineIcon]. */
object ButtonIcons {
    val Next = listOf("M9 5l7 7-7 7")
    val Done = listOf("M5 12l5 5 9-10")
    val Again = listOf("M4 12a8 8 0 1 0 3-6.2", "M4 4v5h5")
    val Hand = listOf(
        "M9 11V5.5a1.5 1.5 0 0 1 3 0V11",
        "M12 10.5V4.5a1.5 1.5 0 0 1 3 0v6",
        "M15 10.5V6.5a1.5 1.5 0 0 1 3 0V14a7 7 0 0 1-7 7h-.5a6 6 0 0 1-4.8-2.4L3.4 15a1.6 1.6 0 0 1 2.4-2.1L9 15.5V9.5",
    )
    val Lock = listOf("M7 11h10a2 2 0 0 1 2 2v6a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-6a2 2 0 0 1 2-2z", "M8 11V8a4 4 0 0 1 8 0v3")
    val Home = listOf("M3 11l9-7 9 7", "M5 10v10h14V10", "M10 20v-5h4v5")
    val Exit = listOf("M15 4h4v16h-4", "M10 8l-4 4 4 4", "M6 12h10")
    val Shield = listOf("M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z", "M8.5 12l2.5 2.5 4.5-5")
    val Moon = listOf("M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z")
    val Sticker = listOf("M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9z")
}

/**
 * Outline icon from prototype SVG path data in a 24-unit box, drawn with round caps and joins.
 * Decorative: the owning control supplies the content description.
 */
@Composable
fun LineIcon(paths: List<String>, color: Color, modifier: Modifier = Modifier, size: Dp = 28.dp, strokeWidth: Float = 2.2f) {
    val parsed = remember(paths) { paths.map { PathParser().parsePathString(it).toPath() } }
    Canvas(modifier.size(size)) {
        val u = this.size.width / 24f
        scale(u, u, pivot = Offset.Zero) {
            for (p in parsed) drawPath(p, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}
