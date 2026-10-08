package com.huroofi.app.learn

import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.data.content.TraceStroke
import com.huroofi.app.toddler.paint.Crayon
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.DemoHand
import com.huroofi.app.ui.components.DemoHandSpec
import com.huroofi.app.ui.components.DemoRun
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.LocalBuzz
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.StarBurst
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.GlyphMask
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.NotoNaskhArabic
import com.huroofi.app.ui.theme.fitGlyph
import com.huroofi.app.ui.theme.toCanvas
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

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
    val CrayonSize = 64.dp
    val CrayonRing = 5.dp
    val Again = 64.dp
    val AgainBorder = Color(0xFFA9CBF2)
    /** The band: the glyph filled pale blue. Darker than `Trace.html`'s `#EEF3F9`, which toddlers could not see (kid review, 2026-10-07). */
    val Band = Color(0xFFC8DCF4)
    /** The letter's edge, drawn over the ink like a colouring book (plan 12 decision 3). */
    val Outline = HuroofiTokens.Navy
    val OutlineWidth = 3.dp
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

    /** The whole-letter check (plan 15 decision 1): a grid this fine, this far in from the letter's edge. */
    val AreaStep = 6.dp
    val AreaInset = 4.dp

    /** The brush reaches the spot furthest from the strokes' centre lines plus this, and never less than [BrushMin]. */
    val BrushSlack = 4.dp
    val BrushMin = 13.dp

    /** Spots still unpainted once every stroke is done pulse, growing by [GAP_GROW]. */
    val GapRadius = 7.dp
    val GapEdgeWidth = 2.5.dp
    val GapFill = HuroofiTokens.Sun
    val GapEdge = HuroofiTokens.Navy
    const val GAP_GROW = 0.5f

    val DoneEdge = HuroofiTokens.Success
    val DoneBadge = HuroofiTokens.Success
    val DoneBadgeText = Color.White
    val BadgeLion = 44.dp

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
        ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = true, "helper bubble speaker"),
        ContrastPair(Outline, HuroofiTokens.Card, large = true, "letter outline"),
        ContrastPair(Dot, Band, large = true, "trace dot"),
        ContrastPair(DoneDot, Band, large = true, "covered trace dot"),
        ContrastPair(CoinNumber, FirstCoin, large = true, "coin number 1"),
        ContrastPair(CoinNumber, OtherCoin, large = true, "coin number"),
        ContrastPair(FirstCoin, Band, large = true, "coin edge 1"),
        ContrastPair(OtherCoin, Band, large = true, "coin edge"),
        ContrastPair(StarEdge, Band, large = true, "finished star edge"),
        ContrastPair(GapEdge, Band, large = true, "unpainted spot edge"),
        ContrastPair(DoneBadgeText, DoneBadge, large = true, "You traced badge"),
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
private class GuideState(val dots: List<List<Offset>>, tolerancePx: Float, val area: AreaCheck) {
    private val check = StrokeCheck(dots, tolerancePx)
    private val pops = dots.map { FloatArray(it.size) }
    var revision by mutableIntStateOf(0)
        private set

    /** Every stroke followed and the whole letter painted (plan 15 decision 1). */
    val done: Boolean get() = check.done && area.done

    /** Every stroke followed, but part of the letter is still unpainted. */
    val filling: Boolean get() = check.done && !area.done

    val finishedStrokes: Int get() = dots.indices.count(check::finished)

    fun addInk(point: Offset) {
        check.addInk(point)
        area.paint(point, point)
        revision++
    }

    /** Ink between two touch events, so a fast swipe doesn't skip dots. */
    fun addSegment(from: Offset, to: Offset, stepPx: Float) {
        area.paint(from, to)
        val steps = ceil((to - from).getDistance() / stepPx).toInt().coerceAtLeast(1)
        for (i in 1..steps) check.addInk(from + (to - from) * (i.toFloat() / steps))
        revision++
    }

    fun clear() {
        check.clear()
        area.clear()
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

/** Where the child is in Trace, which picks the hint: follow the dots, colour in the spots, or done. */
enum class TracePhase { Trace, Fill, Done }

/**
 * Trace over the pale band with dotted strokes and numbered start coins (plan 09 decision 6).
 * The letter is traced once every dot of every stroke is covered and the whole letter is painted
 * (plan 15 decision 1); then [onTraced] plays and "Play" ("Next letter" in practice) unlocks, and
 * only a tap on it calls [onNext]. Order and direction are never punished; ink off the letter just
 * doesn't show (plan 12 decision 4).
 */
@Composable
fun TraceScreen(
    letter: String,
    strokes: List<TraceStroke>,
    introDone: Boolean,
    onBack: () -> Unit,
    onTraced: () -> Unit,
    onNext: () -> Unit,
    onHearHint: (TracePhase) -> Unit = {},
    practice: Boolean = false,
) {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val glyph = remember(letter, canvasSize) {
        if (canvasSize == IntSize.Zero) null else fitGlyph(measurer, letter, Size(canvasSize.width.toFloat(), canvasSize.height.toFloat()), TraceSpec.INK_SHARE, density)
    }
    val check = remember(glyph, strokes) {
        glyph?.let { g ->
            with(density) {
                val spots = GlyphMask(g, canvasSize, density).grid(TraceSpec.AreaStep.toPx(), TraceSpec.AreaInset.toPx())
                val lines = strokes.map { s -> s.points.map { toCanvas(it, g.ink) } }
                val area = AreaCheck(spots, brushReach(spots, lines, TraceSpec.BrushSlack.toPx(), TraceSpec.BrushMin.toPx()))
                GuideState(guideDots(strokes, g.ink, TraceSpec.DotSpacing.toPx()), TraceSpec.Tolerance.toPx(), area)
            }
        }
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
    val ready by rememberInfiniteTransition(label = "ready").animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "ready pulse",
    )
    val gapPulse by rememberInfiniteTransition(label = "gaps").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "gap pulse",
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
    var filling by remember { mutableStateOf(false) }
    val phase = when {
        finished -> TracePhase.Done
        filling -> TracePhase.Fill
        else -> TracePhase.Trace
    }
    // Demo hand (plan 09 decision 4): the pure schedule decides, the overlay plays.
    val demoPaths = remember(glyph, strokes) { glyph?.let { g -> strokes.map { s -> s.points.map { toCanvas(it, g.ink) } } }.orEmpty() }
    var demoState by remember { mutableStateOf(DemoState()) }
    var demoRun by remember { mutableStateOf<DemoRun?>(null) }
    var demoCount by remember { mutableIntStateOf(0) }
    // The idle loop below outlives recompositions; it must see the check built once the glyph is measured.
    val currentCheck by rememberUpdatedState(check)
    val buzz by rememberUpdatedState(LocalBuzz.current)
    val hearHint by rememberUpdatedState(onHearHint)
    fun send(event: DemoEvent) {
        val (state, play) = reduceDemo(demoState, event)
        demoState = state
        when (play) {
            DemoPlay.Keep -> Unit
            DemoPlay.Stop -> demoRun = null
            DemoPlay.All -> demoRun = DemoRun(strokes.indices.toList(), ++demoCount)
            DemoPlay.Next -> {
                val next = currentCheck?.nextStroke()
                if (next != null) {
                    demoRun = DemoRun(listOf(next), ++demoCount)
                } else if (currentCheck?.filling == true) {
                    hearHint(TracePhase.Fill) // no stroke left to show: say where to colour in instead
                }
            }
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
            filling = false
            buzz.confirm()
            send(DemoEvent.Done)
            onTraced()
        }
    }

    // Checked when the finger lifts, as in the prototype.
    fun lifted(guide: GuideState?) {
        when {
            guide == null -> Unit
            guide.done -> finish()
            guide.filling && !filling -> {
                filling = true
                hearHint(TracePhase.Fill)
            }
        }
    }

    CappedWidth(HuroofiTokens.Sky, minHeight = TraceSpec.MinHeight) {
        Column(
            Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (practice) PathHeader(null, "Back to home", onBack) else PathHeader(PathStep.TRACE, "Back to lesson", onBack)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    Modifier
                        .size(TraceSpec.Helper)
                        .semantics { contentDescription = "Show me how" }
                        .clickable(role = Role.Button) { send(DemoEvent.HelperTap) },
                ) {
                    Image(painterResource(R.drawable.pic_lion), contentDescription = null, modifier = Modifier.fillMaxSize())
                }
                // The hint is spoken too (plan 14 decision 2); a tap on the bubble says it again.
                Row(
                    Modifier
                        .weight(1f)
                        .heightIn(min = TraceSpec.Helper)
                        .clip(RoundedCornerShape(20.dp))
                        .background(HuroofiTokens.Card)
                        .clickable(role = Role.Button, onClickLabel = "Hear the hint") { onHearHint(phase) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        when (phase) {
                            TracePhase.Trace -> "Start at the green 1, then follow the dots!"
                            TracePhase.Fill -> "Now colour in the yellow spots!"
                            TracePhase.Done -> if (practice) "Well done! Tap Next for another letter" else "Well done! Tap Play for the picture game"
                        },
                        Modifier.weight(1f),
                        style = HuroofiText.body.copy(fontWeight = FontWeight.Bold),
                        color = HuroofiTokens.Navy,
                    )
                    SoundIcon(HuroofiTokens.Primary, size = 26.dp)
                }
            }
            val cardShape = RoundedCornerShape(TraceSpec.CanvasCorner)
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .dropEdge(if (finished) TraceSpec.DoneEdge else TraceSpec.EdgeColor, 8.dp, cardShape)
                    .clip(cardShape)
                    .background(HuroofiTokens.Card)
                    .onSizeChanged { canvasSize = it }
                    .semantics { contentDescription = if (finished) "You traced the letter $letter" else "Tracing area for the letter $letter" }
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
                                val before = currentCheck?.finishedStrokes
                                currentCheck?.addSegment(stroke.points.last(), change.position, stepPx)
                                // A light tick for each finished stroke; the last one gets finish()'s buzz instead.
                                currentCheck?.let { if (!it.done && before != null && it.finishedStrokes > before) buzz.tick() }
                                send(DemoEvent.Ink)
                                stroke.points += change.position
                            }
                            lifted(currentCheck)
                        }
                    },
            ) {
                // Ink shows only on the letter (plan 12 decision 4): the band is drawn first, out to
                // the outline's middle, and the ink lands only where the band is.
                Canvas(Modifier.fillMaxSize().graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)) {
                    val g = glyph ?: return@Canvas
                    drawText(g.layout, TraceSpec.Band, g.topLeft, drawStyle = Fill) // the outline below leaves Stroke set on the layout
                    drawText(g.layout, TraceSpec.Band, g.topLeft, drawStyle = Stroke(TraceSpec.OutlineWidth.toPx(), join = StrokeJoin.Round))
                    // As wide as the brush the paint check uses, so what shows painted is what counts.
                    val width = (check?.area?.reachPx ?: TraceSpec.BrushMin.toPx()) * 2f
                    val style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    for (s in inkStrokes) {
                        if (s.points.size == 1) {
                            drawCircle(s.color, width / 2f, s.points[0], blendMode = BlendMode.SrcAtop)
                            continue
                        }
                        val path = Path().apply {
                            moveTo(s.points[0].x, s.points[0].y)
                            for (p in s.points.drop(1)) lineTo(p.x, p.y)
                        }
                        drawPath(path, s.color, style = style, blendMode = BlendMode.SrcAtop)
                    }
                }
                Canvas(Modifier.fillMaxSize()) {
                    val guide = check
                    if (guide != null && guide.revision < 0) return@Canvas // reading it redraws the canvas as the guide changes
                    glyph?.let { drawText(it.layout, TraceSpec.Outline, it.topLeft, drawStyle = Stroke(TraceSpec.OutlineWidth.toPx(), join = StrokeJoin.Round)) }
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
                    if (!guide.filling) return@Canvas
                    val gap = TraceSpec.GapRadius.toPx() * (1f + TraceSpec.GAP_GROW * gapPulse)
                    val edge = Stroke(TraceSpec.GapEdgeWidth.toPx())
                    for ((k, spot) in guide.area.points.withIndex()) {
                        if (guide.area.painted(k)) continue
                        drawCircle(TraceSpec.GapFill, gap, spot)
                        drawCircle(TraceSpec.GapEdge, gap, spot, style = edge)
                    }
                }
                DemoHand(demoPaths, demoRun, onFinished = { demoRun = null })
                if (finished) {
                    StarBurst()
                    DoneBadge(letter, Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp))
                }
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
            // Again beside the Next button, which stays locked until the letter is traced (plan 15 decision 1).
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val againShape = RoundedCornerShape(22.dp)
                Box(
                    Modifier
                        .size(TraceSpec.Again)
                        .clip(againShape)
                        .background(HuroofiTokens.Card)
                        .border(3.dp, TraceSpec.AgainBorder, againShape)
                        .semantics { contentDescription = "Again: clear and show me how" }
                        .clickable(role = Role.Button) {
                            inkStrokes.clear()
                            check?.clear()
                            finished = false
                            filling = false
                            send(DemoEvent.Again)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    LineIcon(ButtonIcons.Again, HuroofiTokens.Navy, size = 30.dp, strokeWidth = 2.4f)
                }
                if (finished) {
                    PrimaryButton(
                        if (practice) "Next letter" else "Play",
                        onClick = onNext,
                        modifier = Modifier.weight(1f).graphicsLayer {
                            scaleX = ready
                            scaleY = ready
                        },
                        icon = ButtonIcons.Next,
                    )
                } else {
                    PrimaryButton(
                        withArabic("Finish ", letter, " first"),
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        kind = ButtonKind.Locked,
                        leadingIcon = ButtonIcons.Lock,
                    )
                }
            }
        }
    }
}

/** [before], the Arabic [letter] in Naskh a little larger, then [after]: English labels that name a letter. */
private fun withArabic(before: String, letter: String, after: String): AnnotatedString = buildAnnotatedString {
    append(before)
    withStyle(SpanStyle(fontFamily = NotoNaskhArabic, fontSize = 30.sp)) { append(letter) }
    append(after)
}

/** "You traced ب!" on a green pill with the lion, over the bottom of the finished canvas. */
@Composable
private fun DoneBadge(letter: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(CircleShape)
            .background(TraceSpec.DoneBadge)
            .padding(start = 6.dp, top = 6.dp, end = 18.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painterResource(R.drawable.pic_lion),
            contentDescription = null,
            modifier = Modifier.size(TraceSpec.BadgeLion).clip(CircleShape).background(Color.White),
        )
        Text(
            withArabic("You traced ", letter, "!"),
            style = HuroofiText.body.copy(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold),
            color = TraceSpec.DoneBadgeText,
        )
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun TracePreview() {
    HuroofiTheme { TraceScreen("ب", strokes = emptyList(), introDone = false, onBack = {}, onTraced = {}, onNext = {}) }
}
