package com.huroofi.app.toddler.paint

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.ToddlerActivity
import com.huroofi.app.toddler.ToddlerHeader
import com.huroofi.app.toddler.ToddlerPhrases
import com.huroofi.app.toddler.ToddlerScaffold
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.components.WipeIcon
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.LocalHuroofiColors
import kotlin.random.Random

/** Sizes and colours of Finger paint (`ToddlerPaint.html`, phone layout). */
object PaintSpec {
    val BubbleHeight = 64.dp
    val BubbleCorner = 30.dp
    const val BUBBLE_SP = 34f
    val BubbleIcon = 26.dp
    val CanvasCorner = 40.dp
    val CanvasBorder = 6.dp
    val CanvasShadow = 8.dp
    const val CANVAS_ASPECT = 320f / 380f
    val StrokeWidth = 30.dp
    const val STROKE_ALPHA = 0.9f
    val Star = 86.dp
    val NextButton = 96.dp
    val CrayonSize = 64.dp
    val CrayonRing = 6.dp
    val CrayonShadow = 5.dp
    val Wipe = 64.dp
    val WipeBorder = 3.dp
    val Pink = ToddlerActivity.PAINT.tile.border
    val PinkShadow = ToddlerActivity.PAINT.tile.shadow
    val WipeBorderColor = Color(0xFFA9CBF2)
    val CrayonShadowColor = Color(0xFF13294B).copy(alpha = 0.2f)

    /**
     * Nothing to get right or wrong here. The four crayons are a palette, not answer choices
     * (plan 05 decision 2 keeps all four).
     */
    const val CHOICES = 0
    val touchSizes = listOf(BubbleHeight, NextButton, CrayonSize, Wipe)

    /** Every colour except the crayons, which are an allow-list (plan 05 decision 2). */
    val colors = listOf(Pink, PinkShadow, WipeBorderColor, CrayonShadowColor)
}

/** Finger paint: a random letter to paint over, a star after 400 dp (plan 05 slice 5). */
@Composable
fun PaintScreen(onHome: () -> Unit, onRequestParentZone: () -> Unit) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val session = remember { PaintSession(container.content.letters, Random.Default) }
    val audio = remember { PaintAudio(PromptPlayer(container.sound, scope), Random.Default) }
    var page by remember { mutableStateOf(session.start()) }
    val colors = LocalHuroofiColors.current
    val star = page.painting.starEarned

    LaunchedEffect(page.letter) { audio.ask(page.letter) }
    LaunchedEffect(star) { if (star) audio.star() }
    DisposableEffect(Unit) { onDispose { audio.stop() } }

    ToddlerScaffold {
        ToddlerHeader(onHome = onHome, onRequestParentZone = onRequestParentZone) {
            Row(
                Modifier
                    .height(PaintSpec.BubbleHeight)
                    .clip(RoundedCornerShape(PaintSpec.BubbleCorner))
                    .background(colors.card)
                    .clickable(role = Role.Button) { audio.ask(page.letter) }
                    .semantics { contentDescription = ToddlerPhrases.colourEn(page.letter) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ArabicText(ToddlerPhrases.colourAr(page.letter), size = PaintSpec.BUBBLE_SP.sp, color = colors.navy)
                SoundIcon(color = PaintSpec.Pink, size = PaintSpec.BubbleIcon)
            }
        }
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) {
            Box(Modifier.aspectRatio(PaintSpec.CANVAS_ASPECT)) {
                val shape = RoundedCornerShape(PaintSpec.CanvasCorner)
                Box(
                    Modifier
                        .matchParentSize()
                        .padding(top = PaintSpec.CanvasShadow)
                        .background(PaintSpec.PinkShadow, shape),
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = PaintSpec.CanvasShadow)
                        .clip(shape)
                        .background(colors.card)
                        .border(PaintSpec.CanvasBorder, PaintSpec.Pink, shape),
                ) {
                    LetterOutline(page.letter.letter, Modifier.matchParentSize())
                    PaintLayer(
                        painting = page.painting,
                        onStart = { page = page.copy(painting = page.painting.start(it)) },
                        onMove = { page = page.copy(painting = page.painting.moveTo(it)) },
                        modifier = Modifier
                            .matchParentSize()
                            .semantics { contentDescription = "Paint over the letter ${page.letter.nameLatin} with your finger" },
                    )
                    if (star) {
                        PopIn(page.letter, Modifier.align(Alignment.TopEnd).padding(14.dp)) {
                            StarIcon(fill = colors.sun, size = PaintSpec.Star, outlineWidth = 1.1f)
                        }
                    }
                }
                if (star) {
                    PopIn(page.letter, Modifier.align(Alignment.BottomEnd).offset(x = 8.dp, y = 8.dp)) {
                        RoundIconButton(
                            contentDescription = "Next",
                            onClick = { page = session.next(page) },
                            toddler = true,
                            size = PaintSpec.NextButton,
                            containerColor = colors.success,
                            shadowColor = colors.successShadow,
                        ) { ChevronIcon(colors.card, pointsRight = true, size = 46.dp) }
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (crayon in Crayon.entries) {
                CrayonButton(crayon, selected = crayon == page.painting.crayon) {
                    page = page.copy(painting = page.painting.select(crayon))
                }
            }
            Box(
                Modifier
                    .size(PaintSpec.Wipe)
                    .clip(CircleShape)
                    .background(colors.card)
                    .border(PaintSpec.WipeBorder, PaintSpec.WipeBorderColor, CircleShape)
                    .clickable(role = Role.Button) { page = page.copy(painting = page.painting.wipe()) }
                    .semantics { contentDescription = "Wipe clean" },
                contentAlignment = Alignment.Center,
            ) { WipeIcon(colors.navy) }
        }
    }
}

/** The strokes, and the finger input that makes them. Pointer positions become dp here. */
@Composable
private fun PaintLayer(
    painting: Painting,
    onStart: (PaintPoint) -> Unit,
    onMove: (PaintPoint) -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier.pointerInput(Unit) {
            fun point(p: Offset) = PaintPoint(p.x.toDp().value, p.y.toDp().value)
            awaitEachGesture {
                val down = awaitFirstDown()
                down.consume()
                onStart(point(down.position))
                while (true) {
                    val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) break
                    change.consume()
                    onMove(point(change.position))
                }
            }
        },
    ) {
        val width = PaintSpec.StrokeWidth.toPx()
        val style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
        for (stroke in painting.strokes) {
            val points = stroke.points.map { Offset(it.x.dp.toPx(), it.y.dp.toPx()) }
            if (points.size == 1) {
                drawCircle(stroke.crayon.color, width / 2f, points[0], alpha = PaintSpec.STROKE_ALPHA)
                continue
            }
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (p in points.drop(1)) lineTo(p.x, p.y)
            }
            drawPath(path, stroke.crayon.color, alpha = PaintSpec.STROKE_ALPHA, style = style)
        }
    }
}

@Composable
private fun CrayonButton(crayon: Crayon, selected: Boolean, onClick: () -> Unit) {
    val ring = if (selected) LocalHuroofiColors.current.navy else LocalHuroofiColors.current.card
    Box(
        Modifier
            .size(PaintSpec.CrayonSize + PaintSpec.CrayonShadow)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics {
                contentDescription = crayon.label
                this.selected = selected
            },
    ) {
        Box(
            Modifier
                .padding(top = PaintSpec.CrayonShadow)
                .size(PaintSpec.CrayonSize)
                .background(PaintSpec.CrayonShadowColor, CircleShape),
        )
        Box(
            Modifier
                .size(PaintSpec.CrayonSize)
                .background(crayon.color, CircleShape)
                .border(PaintSpec.CrayonRing, ring, CircleShape),
        )
    }
}

/** Pops its content in, again each time [key] changes. */
@Composable
private fun PopIn(key: Any, modifier: Modifier, content: @Composable () -> Unit) {
    val pop = remember(key) { Animatable(0f) }
    LaunchedEffect(pop) { pop.animateTo(1f, tween(450)) }
    Box(
        modifier.graphicsLayer {
            scaleX = pop.value
            scaleY = pop.value
            rotationZ = -30f * (1f - pop.value)
            alpha = pop.value
        },
    ) { content() }
}
