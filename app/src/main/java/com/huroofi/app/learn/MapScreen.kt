package com.huroofi.app.learn

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.audio.Clips
import com.huroofi.app.data.content.Stage
import com.huroofi.app.data.progress.unlockedStage
import com.huroofi.app.parent.LetterState
import com.huroofi.app.parent.parentProgress
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.StageColors
import kotlin.random.Random

/** Sizes and colours of the Letter Map (`StageMap.html`, plan 07 decision 8). */
object MapSpec {
    val CardWidth = 284.dp
    val CardHeight = 144.dp
    val Gap = 22.dp
    val Inset = 18.dp
    val CardCorner = 26.dp
    val Ring = 4.dp
    val QuietRing = 3.dp
    val Play = 64.dp
    val Badge = 44.dp
    val NumberCircle = 34.dp
    val PathWidth = 8.dp
    val TopFade = 24.dp
    val PathColor = Color(0xFFA9CBF2)
    val QuietBorder = Color(0xFFD5E2F0)
    const val NAME_SP = 20f
    val textSizes = listOf(NAME_SP)

    /** The play button, a finished card (tap = replay a letter), a locked card (tap = boing + wiggle) and the nav items. */
    val touchSizes = listOf(Play, CardHeight, NavSpec.Item)
    val colors = listOf(
        HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, HuroofiTokens.Primary,
        HuroofiTokens.PrimaryShadow, HuroofiTokens.Success, PathColor, QuietBorder, LockedChip,
    ) + NavSpec.colors

    /** The dotted path is decoration; a locked card also shows a lock. Names are 20 sp ExtraBold, so large. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "title"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "subtitle"),
        ContrastPair(HuroofiTokens.Success, HuroofiTokens.Sky, large = true, "finished card ring"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "stage name"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = true, "locked stage name"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Primary, large = true, "play triangle"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Success, large = true, "finished tick"),
        ContrastPair(HuroofiTokens.Muted, LockedChip, large = true, "lock icon"),
        ContrastPair(HuroofiTokens.Muted, LockedChip, large = true, "locked chip letter"),
    ) + HomeSpec.chipPairs + NavSpec.textPairs

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(stage.accent, HuroofiTokens.Sky, large = true, "current card ring"),
        ContrastPair(HuroofiTokens.Navy, stage.pastel, large = false, "stage number"),
    ) + HomeSpec.chipStagePairs(stage)
}

/** One card of the map: the stage, how it shows, and its 4 chips. */
data class MapStage(val stage: Stage, val state: StageState, val chips: List<StageChip>)

private val PlayTriangle = "M8 5v14l11-7z"

/**
 * Seven zig-zag stage cards over a dotted path, opened at the current stage. A finished card replays
 * one of its letters with [onReplay] (plan 11 decision 2), the current card's play button opens Meet,
 * an open card's play button (Unlock all) starts its stage with [onOpen], a locked card says no with
 * [onLocked] and a wiggle.
 */
@Composable
fun MapScreen(
    stages: List<MapStage>,
    onPlay: () -> Unit,
    onReplay: (Stage) -> Unit,
    onOpen: (Stage) -> Unit,
    onLocked: () -> Unit,
    navTabs: List<NavTab>,
    onTab: (NavTab) -> Unit,
) {
    CappedWidth(HuroofiTokens.Sky) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp)) {
                Text("Letter Map", style = HuroofiText.screenTitle, color = HuroofiTokens.Navy)
                Text(
                    subtitle(stages),
                    // Balanced lines, so the wrap never leaves one word alone.
                    style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold, lineBreak = LineBreak.Heading),
                    color = HuroofiTokens.Muted,
                )
            }
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                val cardWidth = min(MapSpec.CardWidth, maxWidth - MapSpec.Inset * 2)
                val starts = listOf(MapSpec.Inset, maxWidth - MapSpec.Inset - cardWidth)
                val scroll = rememberScrollState()
                val step = with(LocalDensity.current) { (MapSpec.CardHeight + MapSpec.Gap).roundToPx() }
                val current = stages.indexOfFirst { it.state == StageState.CURRENT }
                LaunchedEffect(current) { if (current > 0) scroll.scrollTo(current * step) }
                Box(Modifier.fillMaxSize().verticalScroll(scroll).padding(vertical = 24.dp)) {
                    DottedPath(stages.size, starts, cardWidth, Modifier.matchParentSize())
                    Column(verticalArrangement = Arrangement.spacedBy(MapSpec.Gap)) {
                        stages.forEachIndexed { i, s ->
                            MapCard(
                                s,
                                Modifier.padding(start = starts[i % 2]).width(cardWidth).height(MapSpec.CardHeight),
                                onPlay = onPlay,
                                onReplay = { onReplay(s.stage) },
                                onOpen = { onOpen(s.stage) },
                                onLocked = onLocked,
                            )
                        }
                    }
                }
                // Cards scrolled under the header fade out instead of being cut sharply.
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(MapSpec.TopFade)
                        .background(Brush.verticalGradient(listOf(HuroofiTokens.Sky, HuroofiTokens.Sky.copy(alpha = 0f)))),
                )
            }
            LearnNav(NavTab.MAP, navTabs, onTab)
        }
    }
}

/** Counts come from content, so the subtitle never disagrees with letters.json. */
private fun subtitle(stages: List<MapStage>): String {
    val how = if (stages.any { it.state == StageState.OPEN }) "start any stage" else "finish one to unlock the next"
    return "${stages.sumOf { it.chips.size }} letters · ${stages.size} stages · $how"
}

/** Dots from card centre to card centre, drawn behind the cards so they show in the gaps. */
@Composable
private fun DottedPath(count: Int, starts: List<Dp>, cardWidth: Dp, modifier: Modifier) {
    Canvas(modifier) {
        val step = (MapSpec.CardHeight + MapSpec.Gap).toPx()
        val half = MapSpec.CardHeight.toPx() / 2f
        val centres = List(count) { i -> Offset((starts[i % 2] + cardWidth / 2).toPx(), i * step + half) }
        if (centres.size < 2) return@Canvas
        val path = Path().apply {
            moveTo(centres[0].x, centres[0].y)
            for (i in 1 until centres.size) {
                val a = centres[i - 1]
                val b = centres[i]
                cubicTo(a.x, a.y + step / 2f, b.x, b.y - step / 2f, b.x, b.y)
            }
        }
        val width = MapSpec.PathWidth.toPx()
        val dots = PathEffect.dashPathEffect(floatArrayOf(1f, 18.dp.toPx()))
        drawPath(path, MapSpec.PathColor, style = Stroke(width = width, cap = StrokeCap.Round, pathEffect = dots))
    }
}

@Composable
private fun MapCard(s: MapStage, modifier: Modifier, onPlay: () -> Unit, onReplay: () -> Unit, onOpen: () -> Unit, onLocked: () -> Unit) {
    val colors = s.stage.colors()
    val shape = RoundedCornerShape(MapSpec.CardCorner)
    val locked = s.state == StageState.LOCKED
    val (ring, ringWidth, edge) = when (s.state) {
        StageState.CURRENT -> Triple(colors.accent, MapSpec.Ring, colors.pastel)
        StageState.FINISHED -> Triple(HuroofiTokens.Success, MapSpec.Ring, MapSpec.QuietBorder)
        StageState.OPEN -> Triple(colors.pastel, MapSpec.QuietRing, colors.pastel)
        StageState.LOCKED -> Triple(MapSpec.QuietBorder, MapSpec.QuietRing, MapSpec.QuietBorder)
    }
    val wiggle = rememberWiggle()
    val stateLabel = when (s.state) {
        StageState.FINISHED -> "finished"
        StageState.CURRENT -> "playing now"
        StageState.OPEN -> "open"
        StageState.LOCKED -> "locked"
    }
    Column(
        modifier
            .wiggle(wiggle)
            .dropEdge(edge, 6.dp, shape)
            .clip(shape)
            .background(HuroofiTokens.Card)
            .border(ringWidth, ring, shape)
            .then(
                when (s.state) {
                    StageState.LOCKED -> Modifier.clickable(role = Role.Button) {
                        wiggle.play()
                        onLocked()
                    }
                    StageState.FINISHED -> Modifier.clickable(role = Role.Button, onClickLabel = "Play a letter again") {
                        wiggle.play()
                        onReplay()
                    }
                    StageState.CURRENT, StageState.OPEN -> Modifier
                },
            )
            .semantics { contentDescription = "Stage ${s.stage.stage}, ${s.stage.name}, $stateLabel" }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(Modifier.fillMaxWidth().height(MapSpec.Play + 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Number and name are in the card's label; hidden here so TalkBack does not read them twice (plan 08 task 6.2).
            Box(Modifier.size(MapSpec.NumberCircle).background(colors.pastel, CircleShape).clearAndSetSemantics {}, contentAlignment = Alignment.Center) {
                Text("${s.stage.stage}", style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold), color = HuroofiTokens.Navy)
            }
            Text(
                s.stage.name,
                Modifier.weight(1f).clearAndSetSemantics {},
                style = HuroofiText.sectionHeading.copy(fontSize = MapSpec.NAME_SP.sp, lineHeight = 22.sp),
                color = if (locked) HuroofiTokens.Muted else HuroofiTokens.Navy,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            when (s.state) {
                StageState.CURRENT, StageState.OPEN -> RoundIconButton(
                    contentDescription = "Play ${s.stage.name}",
                    onClick = if (s.state == StageState.OPEN) onOpen else onPlay,
                    toddler = true,
                    size = MapSpec.Play,
                    containerColor = HuroofiTokens.Primary,
                    shadowColor = HuroofiTokens.PrimaryShadow,
                    shadowDepth = 4.dp,
                ) { PlayIcon(HuroofiTokens.Card) }
                StageState.FINISHED -> Box(
                    Modifier.size(MapSpec.Badge).background(HuroofiTokens.Success, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { LineIcon(LearnIcons.Check, HuroofiTokens.Card, size = 24.dp, strokeWidth = 3.2f) }
                StageState.LOCKED -> Box(
                    Modifier.size(MapSpec.Badge).background(LockedChip, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { LineIcon(LearnIcons.Lock, HuroofiTokens.Muted, size = 22.dp) }
            }
        }
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (chip in s.chips) StageLetterChip(chip, s.stage, locked = locked)
            }
        }
    }
}

/** Filled play triangle from the prototype. Decorative: the button carries the label. */
@Composable
private fun PlayIcon(color: Color) {
    val path = remember { PathParser().parsePathString(PlayTriangle).toPath() }
    Canvas(Modifier.size(30.dp)) {
        val u = size.width / 24f
        scale(u, u, pivot = Offset.Zero) { drawPath(path, color) }
    }
}

/**
 * Letter Map for the stored progress. Play opens Meet for Today's letter, the learning letter. A
 * finished card opens Meet for one of its letters at random, never the same one twice in a row.
 */
@Composable
fun MapRoute(navTabs: List<NavTab>, onTab: (NavTab) -> Unit, onPlay: (Int) -> Unit, onPractice: (Int) -> Unit) {
    val container = LocalAppContainer.current
    val content = container.content
    val completed by container.progress.completedLetters.collectAsStateWithLifecycle<Set<Int>?>(null)
    val unlockAll by container.progress.unlockAll.collectAsStateWithLifecycle(false)
    val traceOnly by container.progress.traceOnly.collectAsStateWithLifecycle(false)
    val scope = rememberCoroutineScope()
    val prompt = remember { PromptPlayer(container.sound, scope) }
    var lastReplay by rememberSaveable { mutableIntStateOf(0) }
    val done = completed ?: return Loading()
    val stageOf = remember { content.letters.associate { it.index to it.stage } }
    val open = unlockedStage(done, stageOf)
    val states = stageStates(content.stages, done, stageOf, unlockAll)
    val letterStates = parentProgress(content.letters, content.stages, done, open).states
    val learning = learningLetter(content.letters, done, open)
    // Trace practice opens a stage at its first letter (plan 13 decision 4).
    val practiceStage = { stage: Int -> onPractice(content.lettersInStage(stage).minOf { it.index }) }
    MapScreen(
        stages = content.stages.sortedBy { it.stage }.map { stage ->
            MapStage(
                stage,
                states.getValue(stage.stage),
                content.lettersInStage(stage.stage).sortedBy { it.index }.map { StageChip(it, letterStates.getValue(it.index)) },
            )
        },
        onPlay = {
            prompt.stop()
            if (traceOnly) practiceStage(open) else learning?.let { onPlay(it.index) }
        },
        onReplay = { stage ->
            prompt.stop()
            if (traceOnly) {
                practiceStage(stage.stage)
            } else {
                val pick = replayLetter(content.lettersInStage(stage.stage).map { it.index }, lastReplay, Random)
                lastReplay = pick
                onPlay(pick)
            }
        },
        onOpen = { stage ->
            prompt.stop()
            if (traceOnly) practiceStage(stage.stage) else learningLetter(content.letters, done, stage.stage)?.let { onPlay(it.index) }
        },
        onLocked = { prompt.play(Clips.boing) },
        navTabs = navTabs,
        onTab = {
            prompt.stop()
            onTab(it)
        },
    )
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun MapPreview() {
    val letters = LearnPreviewData.letters
    HuroofiTheme {
        MapScreen(
            stages = LearnPreviewData.stages.map { stage ->
                val state = when {
                    stage.stage == 1 -> StageState.FINISHED
                    stage.stage == 2 -> StageState.CURRENT
                    else -> StageState.LOCKED
                }
                val chips = letters.mapIndexed { i, l ->
                    StageChip(
                        l,
                        when {
                            state == StageState.FINISHED || (state == StageState.CURRENT && i == 0) -> LetterState.LEARNED
                            state == StageState.CURRENT && i == 1 -> LetterState.LEARNING
                            else -> LetterState.TO_GO
                        },
                    )
                }
                MapStage(stage, state, chips)
            },
            onPlay = {},
            onReplay = {},
            onOpen = {},
            onLocked = {},
            navTabs = NavTab.entries,
            onTab = {},
        )
    }
}
