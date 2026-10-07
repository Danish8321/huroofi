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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.huroofi.app.R
import com.huroofi.app.data.content.TraceStroke
import com.huroofi.app.toddler.paint.Crayon
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.DemoHand
import com.huroofi.app.ui.components.DemoHandSpec
import com.huroofi.app.ui.components.DemoRun
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.fitGlyph
import com.huroofi.app.ui.theme.toCanvas
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

private const val IDLE_TICK_MS = 100L

/** Sizes and colours of Trace (`Trace.html`, plan 07 decision 3). */
object TraceSpec {
    val Helper = 64.dp
    val CanvasCorner = 34.dp
    val EdgeColor = Color(0xFFCFE2F7)
    val Tolerance = 30.dp
    val DotSpacing = 18.dp
    val DotRadius = 5.dp
    val DotStrokeRadius = 9.dp
    val CoinRadius = 19.dp
    val ArrowLength = 30.dp
    val Ink = 22.dp
    val CrayonSize = 64.dp
    val CrayonRing = 5.dp
    val Again = 64.dp
    val AgainWidth = 120.dp
    val AgainBorder = Color(0xFFA9CBF2)
    /** The band: the glyph filled pale blue. Darker than `Trace.html`'s `#EEF3F9`, which toddlers could not see (kid review, 2026-10-07). */
    val Band = Color(0xFFC8DCF4)
    val Dot = HuroofiTokens.Muted
    val FirstCoin = HuroofiTokens.Success
    val OtherCoin = HuroofiTokens.Primary
    val CoinNumber = Color.White
    val StarFill = HuroofiTokens.Sun
    val StarEdge = HuroofiTokens.Navy
    /** A covered dot turns green and grows by [DONE_GROW] over this long (plan 12 decision 2). */
    const val POP_MS = 180f
    const val DONE_GROW = 0.3f
    val DoneDot = HuroofiTokens.Success

    /** Open dots of every stroke but the next one are this faint, so the next path stands out. */
    const val LATER_ALPHA = 0.35f

    /** The glyph's ink fills this share of the canvas width or height, whichever binds first. */
    const val INK_SHARE = 0.8f

    /** Keeps the canvas about 320 dp tall; a shorter window scrolls (tablet landscape). */
    val MinHeight = 680.dp

    val Pink = Color(0xFFFF6FA5)

    /** The four toddler paints plus pink (prototype). Coral passes only via the crayon allow-list (plan 05 decision 2). */
    val crayons: List<Pair<Color, String>> = Crayon.entries.map { it.color to it.label } + (Pink to "Pink paint")

    val touchSizes = listOf(Helper, CrayonSize, Again, LessonSpec.Back)
    val colors = listOf(HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, EdgeColor, AgainBorder, Band, FirstCoin, OtherCoin, StarFill) +
        crayons.map { it.first }

    /** The guide is the shape to trace, so it counts as a cue. Crayon colours are content, like Paint's. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "helper bubble"),
        ContrastPair(Dot, Band, large = true, "trace dot"),
        ContrastPair(DoneDot, Band, large = true, "covered trace dot"),
        ContrastPair(CoinNumber, FirstCoin, large = true, "coin number 1"),
        ContrastPair(CoinNumber, OtherCoin, large = true, "coin number"),
        ContrastPair(FirstCoin, Band, large = true, "coin edge 1"),
        ContrastPair(OtherCoin, Band, large = true, "coin edge"),
        ContrastPair(StarEdge, Band, large = true, "finished star edge"),
        ContrastPair(DemoHandSpec.Edge, Band, large = true, "demo hand edge"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "picked crayon ring"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "Again icon"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "Again label"),
    ) + LessonSpec.headerPairs
}

/** Points are snapshot state, so the canvas redraws as a stroke grows. */
private class InkStroke(val color: Color, val points: SnapshotStateList<Offset>)

/**
 * The dots of every stroke and how far each covered dot has popped. The canvas reads [revision], so
 * it redraws when ink lands or a dot moves a frame.
 */
private class GuideState(val dots: List<List<Offset>>, tolerancePx: Float) {
    private val check = StrokeCheck(dots, tolerancePx)
    private val pops = dots.map { FloatArray(it.size) }
    var revision by mutableIntStateOf(0)
        private set

    val done: Boolean get() = check.done

    fun addInk(point: Offset) {
        check.addInk(point)
        revision++
    }

    /** Ink between two touch events, so a fast swipe doesn't skip dots. */
    fun addSegment(from: Offset, to: Offset, stepPx: Float) {
        val steps = ceil((to - from).getDistance() / stepPx).toInt().coerceAtLeast(1)
        for (i in 1..steps) addInk(from + (to - from) * (i.toFloat() / steps))
    }

    fun clear() {
        check.clear()
        pops.forEach { it.fill(0f) }
        revision++
    }

    fun covered(stroke: Int, dot: Int): Boolean = check.covered(stroke, dot)

    /** 0 for an open dot, rising to 1 as a covered dot finishes its pop. */
    fun pop(stroke: Int, dot: Int): Float = pops[stroke][dot]

    fun finished(stroke: Int): Boolean = check.finished(stroke)

    fun nextStroke(): Int? = check.nextStroke()

    /** Moves every dot one frame towards its target size; true while any still moves. */
    fun advance(fraction: Float): Boolean {
        var moving = false
        for (s in dots.indices) for (d in dots[s].indices) {
            val target = if (check.covered(s, d)) 1f else 0f
            val now = pops[s][d]
            if (now == target) continue
            pops[s][d] = if (target > now) minOf(target, now + fraction) else maxOf(target, now - fraction)
            moving = true
        }
        if (moving) revision++
        return moving
    }
}

/** The glyph's strokes in canvas pixels, one dot list per stroke. */
private fun guideDots(strokes: List<TraceStroke>, ink: Rect, spacingPx: Float): List<List<Offset>> =
    strokes.map { stroke -> strokeDots(stroke.points.map { toCanvas(it, ink) }, spacingPx) }

private fun DrawScope.drawStar(center: Offset, radius: Float) {
    val path = Path()
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) radius else radius * 0.45f
        val angle = -PI / 2 + i * PI / 5
        val p = Offset(center.x + (r * cos(angle)).toFloat(), center.y + (r * sin(angle)).toFloat())
        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
    }
    path.close()
    drawPath(path, TraceSpec.StarFill)
    drawPath(path, TraceSpec.StarEdge, style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round))
}

/** A short arrow beside the coin, along the stroke's first leg and on the side nearer [towards]; none for a dot stroke. */
private fun DrawScope.drawArrow(dots: List<Offset>, color: Color, towards: Offset) {
    if (dots.size < 2) return
    val reach = dots[minOf(2, dots.lastIndex)] - dots[0]
    val length = reach.getDistance()
    if (length == 0f) return
    val dir = reach / length
    var perp = Offset(-dir.y, dir.x)
    val side = TraceSpec.CoinRadius.toPx() + 14.dp.toPx()
    if ((dots[0] + perp * side - towards).getDistance() > (dots[0] - perp * side - towards).getDistance()) perp = -perp
    val from = dots[0] + perp * side
    val to = from + dir * TraceSpec.ArrowLength.toPx()
    val width = 4.dp.toPx()
    val head = 9.dp.toPx()
    drawLine(color, from, to, width, StrokeCap.Round)
    for (sign in listOf(-1f, 1f)) {
        val a = 0.6f * sign
        val back = Offset(-dir.x * cos(a) + dir.y * sin(a), -dir.x * sin(a) - dir.y * cos(a))
        drawLine(color, to, to + back * head, width, StrokeCap.Round)
    }
}

/**
 * Trace over the pale band with dotted strokes and numbered start coins (plan 09 decision 6).
 * Completes on its own once every stroke is at [STROKE_NEED] coverage; there is no button to skip it
 * (plan 09 decision 3, 2026-10-06). Order, direction and ink outside the letter are never punished.
 */
@Composable
fun TraceScreen(letter: String, strokes: List<TraceStroke>, introDone: Boolean, onBack: () -> Unit, onDone: () -> Unit) {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val glyph = remember(letter, canvasSize) {
        if (canvasSize == IntSize.Zero) null else fitGlyph(measurer, letter, Size(canvasSize.width.toFloat(), canvasSize.height.toFloat()), TraceSpec.INK_SHARE, density)
    }
    val check = remember(glyph, strokes) {
        glyph?.let { with(density) { GuideState(guideDots(strokes, it.ink, TraceSpec.DotSpacing.toPx()), TraceSpec.Tolerance.toPx()) } }
    }
    val coins = remember(glyph, check) {
        if (glyph == null || check == null) emptyList()
        else with(density) {
            val canvas = Rect(Offset.Zero, Size(canvasSize.width.toFloat(), canvasSize.height.toFloat()))
            coinSpots(check.dots, glyph.ink.center, TraceSpec.CoinRadius.toPx(), TraceSpec.DotStrokeRadius.toPx(), canvas)
        }
    }
    val inkStrokes = remember { mutableStateListOf<InkStroke>() }
    val coinNumbers = remember(strokes.size) {
        List(strokes.size) { measurer.measure((it + 1).toString(), HuroofiText.body.copy(color = TraceSpec.CoinNumber, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)) }
    }
    val pulse by rememberInfiniteTransition(label = "next coin").animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "pulse",
    )
    // Covered dots pop over a few frames; the loop ends when nothing moves.
    LaunchedEffect(check, check?.revision) {
        val guide = check ?: return@LaunchedEffect
        var last = withFrameNanos { it }
        do {
            val now = withFrameNanos { it }
            val moving = guide.advance((now - last) / 1_000_000f / TraceSpec.POP_MS)
            last = now
        } while (moving)
    }
    var crayon by remember { mutableStateOf(TraceSpec.crayons.first().first) }
    var finished by remember { mutableStateOf(false) }
    // Demo hand (plan 09 decision 4): the pure schedule decides, the overlay plays.
    val demoPaths = remember(glyph, strokes) { glyph?.let { g -> strokes.map { s -> s.points.map { toCanvas(it, g.ink) } } }.orEmpty() }
    var demoState by remember { mutableStateOf(DemoState()) }
    var demoRun by remember { mutableStateOf<DemoRun?>(null) }
    var demoCount by remember { mutableIntStateOf(0) }
    // The idle loop below outlives recompositions; it must see the check built once the glyph is measured.
    val currentCheck by rememberUpdatedState(check)
    fun send(event: DemoEvent) {
        val (state, play) = reduceDemo(demoState, event)
        demoState = state
        when (play) {
            DemoPlay.Keep -> Unit
            DemoPlay.Stop -> demoRun = null
            DemoPlay.All -> demoRun = DemoRun(strokes.indices.toList(), ++demoCount)
            DemoPlay.Next -> demoRun = currentCheck?.nextStroke()?.let { DemoRun(listOf(it), ++demoCount) }
        }
    }
    LaunchedEffect(Unit) { send(DemoEvent.Enter) }
    LaunchedEffect(introDone) { if (introDone) send(DemoEvent.SoundDone) }
    // The wait only counts while the hand is still.
    LaunchedEffect(Unit) {
        while (true) {
            delay(IDLE_TICK_MS)
            if (demoRun == null) send(DemoEvent.Idle(IDLE_TICK_MS))
        }
    }
    // The gesture loop outlives recompositions; it reads the latest check and crayon through these.
    val currentCrayon by rememberUpdatedState(crayon)

    fun finish() {
        if (!finished) {
            finished = true
            send(DemoEvent.Done)
            onDone()
        }
    }

    CappedWidth(HuroofiTokens.Sky, minHeight = TraceSpec.MinHeight) {
        Column(
            Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PathHeader(PathStep.TRACE, "Back to lesson", onBack)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    Modifier
                        .size(TraceSpec.Helper)
                        .semantics { contentDescription = "Show me how" }
                        .clickable(role = Role.Button) { send(DemoEvent.HelperTap) },
                ) {
                    Image(painterResource(R.drawable.pic_lion), contentDescription = null, modifier = Modifier.fillMaxSize())
                }
                Text(
                    "Start at the green 1, then follow the dots!",
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
                        val stepPx = TraceSpec.Tolerance.toPx() / 2f
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            down.consume()
                            send(DemoEvent.Touch)
                            val stroke = InkStroke(currentCrayon, mutableStateListOf(down.position))
                            inkStrokes += stroke
                            currentCheck?.addInk(down.position)
                            send(DemoEvent.Ink)
                            while (true) {
                                val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) break
                                change.consume()
                                currentCheck?.addSegment(stroke.points.last(), change.position, stepPx)
                                send(DemoEvent.Ink)
                                stroke.points += change.position
                            }
                            if (currentCheck?.done == true) finish()
                        }
                    },
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val guide = check
                    if (guide != null && guide.revision < 0) return@Canvas // reading it redraws the canvas as the guide changes
                    glyph?.let { drawText(it.layout, TraceSpec.Band, it.topLeft) }
                    val width = TraceSpec.Ink.toPx()
                    val style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    for (s in inkStrokes) {
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
                    if (guide == null || glyph == null) return@Canvas
                    val next = guide.nextStroke()
                    for ((i, dots) in guide.dots.withIndex()) {
                        val base = (if (dots.size == 1) TraceSpec.DotStrokeRadius else TraceSpec.DotRadius).toPx()
                        for ((d, dot) in dots.withIndex()) {
                            if (guide.covered(i, d)) {
                                drawCircle(TraceSpec.DoneDot, base * (1f + TraceSpec.DONE_GROW * guide.pop(i, d)), dot)
                            } else {
                                drawCircle(TraceSpec.Dot, base, dot, alpha = if (next == null || i == next) 1f else TraceSpec.LATER_ALPHA)
                            }
                        }
                    }
                    for ((i, dots) in guide.dots.withIndex()) {
                        val color = if (i == 0) TraceSpec.FirstCoin else TraceSpec.OtherCoin
                        val radius = TraceSpec.CoinRadius.toPx() * (if (i == next) pulse else 1f)
                        if (guide.finished(i)) {
                            drawStar(coins[i], radius * 1.15f)
                            continue
                        }
                        drawArrow(dots, color, glyph.ink.center)
                        drawCircle(color, radius, coins[i])
                        val number = coinNumbers[i]
                        drawText(number, topLeft = Offset(coins[i].x - number.size.width / 2f, coins[i].y - number.size.height / 2f))
                    }
                }
                DemoHand(demoPaths, demoRun, onFinished = { demoRun = null })
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
            Box(
                Modifier
                    .width(TraceSpec.AgainWidth)
                    .height(TraceSpec.Again)
                    .clip(RoundedCornerShape(22.dp))
                    .background(HuroofiTokens.Card)
                    .border(3.dp, TraceSpec.AgainBorder, RoundedCornerShape(22.dp))
                    .clickable(role = Role.Button) {
                        inkStrokes.clear()
                        check?.clear()
                        send(DemoEvent.Again)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LineIcon(ButtonIcons.Again, HuroofiTokens.Navy, size = 22.dp, strokeWidth = 2.4f)
                    Text("Again", style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy)
                }
            }
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun TracePreview() {
    HuroofiTheme { TraceScreen("ب", strokes = emptyList(), introDone = false, onBack = {}, onDone = {}) }
}
