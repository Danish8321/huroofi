package com.huroofi.app.toddler.paint

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.R
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.TraceStroke
import com.huroofi.app.toddler.CountdownSpec
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.RingNextButton
import com.huroofi.app.toddler.ToddlerActivity
import com.huroofi.app.toddler.ToddlerHeader
import com.huroofi.app.toddler.ToddlerPhrases
import com.huroofi.app.toddler.ToddlerScaffold
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.DemoHand
import com.huroofi.app.ui.components.DemoRun
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.LocalBuzz
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.components.ToddlerHomeButtonSpec
import com.huroofi.app.ui.components.WipeIcon
import com.huroofi.app.ui.components.parentLockPairs
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.FittedGlyph
import com.huroofi.app.ui.theme.GlyphMask
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.fitGlyph
import com.huroofi.app.ui.theme.toCanvas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
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
    /** On a tall phone the canvas grows down to this width : height instead of leaving a band (plan 14 decision 1). */
    const val CANVAS_TALLEST = 320f / 460f
    val StrokeWidth = 30.dp
    /** A finger this close to the letter still counts as painting on it. */
    val OnLetterSlop = 8.dp
    const val STROKE_ALPHA = 0.9f
    val Star = 86.dp
    /** Under the canvas: the crayons, or once done "Paint it again" and the ring (`.paint-done-row`). */
    val ControlsHeight = 156.dp
    val DoneGap = 24.dp
    val Again = 72.dp
    val AgainIcon = 30.dp
    val AgainColor = Color(0xFFB0185A)
    val RingTrack = Color(0xFFF2B5CC)
    val Leo = 104.dp
    val LeoInset = 12.dp
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
    val touchSizes = listOf(BubbleHeight, CrayonSize, Wipe, Again, CountdownSpec.Ring)

    /** Every colour except the crayons, which are an allow-list (plan 05 decision 2). */
    val colors = listOf(Pink, PinkShadow, WipeBorderColor, CrayonShadowColor, AgainColor, RingTrack)

    /**
     * The star and cheering Leo are a reward, not a cue. A crayon's colour is the content itself, like a
     * picture, so only the picked ring is checked.
     */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "prompt Arabic"),
        ContrastPair(Pink, HuroofiTokens.Card, large = true, "prompt speaker icon"),
        ContrastPair(LetterOutlineSpec.EdgeColor, HuroofiTokens.Card, large = true, "dashed edge of the letter to paint"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Success, large = true, "next chevron"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "wipe icon"),
        ContrastPair(AgainColor, HuroofiTokens.Card, large = true, "paint it again icon"),
        ContrastPair(HuroofiTokens.Success, HuroofiTokens.Sky, large = true, "done canvas frame"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "picked crayon ring"),
    ) + ToddlerHomeButtonSpec.textPairs + parentLockPairs
}

/**
 * Finger paint: a random letter to paint over (plan 05 slice 5). After 400 dp on the letter it
 * celebrates (green frame, star, cheering Leo), then a ring counts down to a new letter; "Paint it
 * again" keeps the letter (plan 15 decision 5).
 */
@Composable
fun PaintScreen(strokesOf: (Int) -> List<TraceStroke>, onHome: () -> Unit, onRequestParentZone: () -> Unit) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val session = remember { PaintSession(container.content.letters, Random.Default) }
    val audio = remember { PaintAudio(PromptPlayer(container.sound, scope), Random.Default) }
    var page by remember { mutableStateOf(session.start()) }
    val colors = LocalHuroofiColors.current
    val done = page.painting.done
    // Demo hand: once per page over the outline, never repeated, stopped by the first touch.
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val strokes = remember(page.letter) { strokesOf(page.letter.index) }
    val glyph = remember(page.letter, canvasSize) {
        if (canvasSize == IntSize.Zero) null else fitGlyph(measurer, page.letter.letter, Size(canvasSize.width.toFloat(), canvasSize.height.toFloat()), LetterOutlineSpec.INK_SHARE, density)
    }
    // Only paint on the letter shows and counts towards the star (plan 12 decision 4).
    val onLetter = remember(glyph) { glyph?.let { GlyphMask(it, canvasSize, density) } }
    val demoPaths = remember(glyph, strokes) { glyph?.let { g -> strokes.map { s -> s.points.map { toCanvas(it, g.ink) } } }.orEmpty() }
    var demoRun by remember(page.letter) { mutableStateOf<DemoRun?>(DemoRun(demoStrokes(strokes), 0)) }

    LaunchedEffect(page.letter) { audio.ask(page.letter) }
    val buzz = LocalBuzz.current
    LaunchedEffect(done) {
        if (done) {
            buzz.confirm()
            audio.celebrate()
        }
    }
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
        // Canvas and controls stay together, centred in the space left, so no gap opens between
        // them and the done row never covers the canvas (plan 11 decision 5).
        Column(
            Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.weight(1f, fill = false).canvasSize(PaintSpec.CANVAS_ASPECT, PaintSpec.CANVAS_TALLEST)) {
                val shape = RoundedCornerShape(PaintSpec.CanvasCorner)
                Box(
                    Modifier
                        .matchParentSize()
                        .padding(top = PaintSpec.CanvasShadow)
                        .background(if (done) colors.successShadow else PaintSpec.PinkShadow, shape),
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = PaintSpec.CanvasShadow)
                        .clip(shape)
                        .background(colors.card)
                        .border(PaintSpec.CanvasBorder, if (done) colors.success else PaintSpec.Pink, shape)
                        .onSizeChanged { canvasSize = it },
                ) {
                    PaintLayer(
                        painting = page.painting,
                        glyph = glyph,
                        onStart = {
                            demoRun = null
                            if (!page.painting.done) page = page.copy(painting = page.painting.start(it))
                        },
                        onMove = move@{
                            if (page.painting.done) return@move
                            val at = with(density) { Offset(it.x.dp.toPx(), it.y.dp.toPx()) }
                            val counts = onLetter?.contains(at, with(density) { PaintSpec.OnLetterSlop.toPx() }) ?: true
                            page = page.copy(painting = page.painting.moveTo(it, counts))
                        },
                        modifier = Modifier
                            .matchParentSize()
                            .semantics {
                                contentDescription = if (done) {
                                    "You painted ${page.letter.nameLatin}!"
                                } else {
                                    "Paint over the letter ${page.letter.nameLatin} with your finger"
                                }
                            },
                    )
                    LetterOutline(page.letter.letter, Modifier.matchParentSize(), fill = false)
                    DemoHand(demoPaths, demoRun, onFinished = { demoRun = null })
                    if (done) {
                        PopIn(page.letter, Modifier.align(Alignment.TopEnd).padding(14.dp)) {
                            StarIcon(fill = colors.sun, size = PaintSpec.Star, outlineWidth = 1.1f)
                        }
                        CheeringLeo(Modifier.align(Alignment.BottomEnd).padding(PaintSpec.LeoInset))
                    }
                }
            }
            Box(Modifier.fillMaxWidth().height(PaintSpec.ControlsHeight), contentAlignment = Alignment.Center) {
                if (done) {
                    DoneRow(
                        letter = page.letter,
                        onAgain = { page = page.copy(painting = page.painting.wipe()) },
                        onNext = { if (page.painting.done) page = session.next(page) },
                    )
                } else {
                    Crayons(
                        crayon = page.painting.crayon,
                        onPick = { page = page.copy(painting = page.painting.select(it)) },
                        onWipe = { page = page.copy(painting = page.painting.wipe()) },
                    )
                }
            }
        }
    }
}

/** The four crayons and the wipe button. */
@Composable
private fun Crayons(crayon: Crayon, onPick: (Crayon) -> Unit, onWipe: () -> Unit) {
    val colors = LocalHuroofiColors.current
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (c in Crayon.entries) CrayonButton(c, selected = c == crayon) { onPick(c) }
        Box(
            Modifier
                .size(PaintSpec.Wipe)
                .clip(CircleShape)
                .background(colors.card)
                .border(PaintSpec.WipeBorder, PaintSpec.WipeBorderColor, CircleShape)
                .clickable(role = Role.Button, onClick = onWipe)
                .semantics { contentDescription = "Wipe clean" },
            contentAlignment = Alignment.Center,
        ) { WipeIcon(colors.navy) }
    }
}

/** Once done: "Paint it again" beside the ring that counts down to a new letter. */
@Composable
private fun DoneRow(letter: Letter, onAgain: () -> Unit, onNext: () -> Unit) {
    val colors = LocalHuroofiColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(PaintSpec.DoneGap), verticalAlignment = Alignment.CenterVertically) {
        RoundIconButton(
            contentDescription = "Paint it again",
            onClick = onAgain,
            toddler = true,
            size = PaintSpec.Again,
            containerColor = colors.card,
            shadowColor = PaintSpec.RingTrack,
        ) { LineIcon(ButtonIcons.Again, PaintSpec.AgainColor, size = PaintSpec.AgainIcon, strokeWidth = 2.6f) }
        RingNextButton(letter, onNext, track = PaintSpec.RingTrack, label = "Next letter. It starts by itself in a moment")
    }
}

/** Leo hops up into the corner, then sways and bobs while the ring counts down. Decorative. */
@Composable
private fun CheeringLeo(modifier: Modifier) {
    val hop = remember { Animatable(0f) }
    val sway = rememberInfiniteTransition(label = "cheer")
    val t by sway.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1_200, easing = LinearEasing), initialStartOffset = StartOffset(1_000)),
        label = "cheer",
    )
    LaunchedEffect(Unit) { hop.animateTo(1f, tween(600, delayMillis = 400, easing = FastOutSlowInEasing)) }
    Image(
        painterResource(R.drawable.pic_lion),
        contentDescription = null,
        modifier = modifier.size(PaintSpec.Leo).graphicsLayer {
            val wave = sin(t * 2f * PI.toFloat())
            translationY = (1f - hop.value) * 60.dp.toPx() - wave.coerceAtLeast(0f) * 8.dp.toPx()
            rotationZ = -4f * cos(t * 2f * PI.toFloat())
            alpha = hop.value
        },
    )
}

/**
 * The letter's fill with the strokes over it, and the finger input that makes them. Paint lands
 * only on the fill, out to the edge's middle (plan 12 decision 4). Pointer positions become dp here.
 */
@Composable
private fun PaintLayer(
    painting: Painting,
    glyph: FittedGlyph?,
    onStart: (PaintPoint) -> Unit,
    onMove: (PaintPoint) -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen).pointerInput(Unit) {
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
        val g = glyph ?: return@Canvas
        drawText(g.layout, LetterOutlineSpec.FillColor, g.topLeft, drawStyle = Fill)
        drawText(g.layout, LetterOutlineSpec.FillColor, g.topLeft, drawStyle = Stroke(LetterOutlineSpec.EdgeWidth.toPx()))
        val width = PaintSpec.StrokeWidth.toPx()
        val style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
        for (stroke in painting.strokes) {
            val points = stroke.points.map { Offset(it.x.dp.toPx(), it.y.dp.toPx()) }
            if (points.size == 1) {
                drawCircle(stroke.crayon.color, width / 2f, points[0], alpha = PaintSpec.STROKE_ALPHA, blendMode = BlendMode.SrcAtop)
                continue
            }
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (p in points.drop(1)) lineTo(p.x, p.y)
            }
            drawPath(path, stroke.crayon.color, alpha = PaintSpec.STROKE_ALPHA, style = style, blendMode = BlendMode.SrcAtop)
        }
    }
}

@Composable
private fun CrayonButton(crayon: Crayon, selected: Boolean, onClick: () -> Unit) {
    val ring = if (selected) LocalHuroofiColors.current.navy else LocalHuroofiColors.current.card
    Box(
        Modifier
            .size(PaintSpec.CrayonSize + PaintSpec.CrayonShadow)
            // The ring is the feedback; a square ripple would spill past the round crayon.
            .clickable(interactionSource = null, indication = null, role = Role.RadioButton, onClick = onClick)
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
private fun PopIn(key: Any, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
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

/**
 * Full width at [aspect] (width : height), then taller into any spare height down to [tallest].
 * Short of room it keeps [aspect] and narrows, like `aspectRatio`.
 */
private fun Modifier.canvasSize(aspect: Float, tallest: Float) = layout { measurable, constraints ->
    val width = constraints.maxWidth
    val shortest = (width / aspect).roundToInt()
    val (w, h) = when {
        !constraints.hasBoundedHeight -> width to shortest
        constraints.maxHeight >= shortest -> width to minOf(constraints.maxHeight, (width / tallest).roundToInt())
        else -> (constraints.maxHeight * aspect).roundToInt() to constraints.maxHeight
    }
    val placeable = measurable.measure(Constraints.fixed(w, h))
    layout(w, h) { placeable.place(0, 0) }
}
