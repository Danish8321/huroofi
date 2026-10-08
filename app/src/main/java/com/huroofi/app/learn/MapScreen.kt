package com.huroofi.app.learn

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.audio.Clips
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.data.progress.unlockedStage
import com.huroofi.app.parent.LetterState
import com.huroofi.app.parent.parentProgress
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.stagePicture
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.withArabic
import kotlin.random.Random

/** Sizes and colours of the Letter map (`StageMap.html`, plan 15 decision 6). */
object MapSpec {
    val Gap = 14.dp
    val CardCorner = 30.dp
    val CardRing = 4.dp
    val CardEdge = 6.dp
    val Picture = 56.dp
    val PictureImage = 46.dp
    val PictureCorner = 18.dp
    val Badge = 40.dp
    val Play = 52.dp
    val Chip = 72.dp
    val ChipCorner = 18.dp
    val ChipRing = 4.dp
    /** The dotted line runs down through the pictures: card ring + padding + half a picture. */
    val LineX = CardRing + 14.dp + Picture / 2
    val LineWidth = 6.dp
    /** The shortest card: a picture row inside the card's padding and ring. */
    val CardMin = Picture + (12.dp + CardRing) * 2

    val Quiet = Color(0xFFF4F7FB)
    val QuietEdge = Color(0xFFCFE2F7)
    val LineColor = Color(0xFFA9CBF2)
    /** The current card's ring is navy, as in the approved snapshot. */
    val CurrentRing = HuroofiTokens.Navy

    const val NAME_SP = 20f
    const val CHIP_SP = 40f
    const val LETTERS_SP = 20f
    val textSizes = listOf(NAME_SP, CHIP_SP, LETTERS_SP)

    /** Finished, open and locked cards are buttons; the current card's button; the nav items. */
    val touchSizes = listOf(CardMin, HuroofiDimens.PrimaryButtonHeight, NavSpec.Item)
    val colors = listOf(
        HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, HuroofiTokens.Primary,
        HuroofiTokens.PrimaryShadow, HuroofiTokens.Success, HuroofiTokens.SuccessShadow, Quiet, QuietEdge, LineColor,
    ) + NavSpec.colors

    /** The dotted line is decoration. Names are 20 sp ExtraBold and chips 40 sp, so large; the rest is 16–18 sp. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "title"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "subtitle"),
        ContrastPair(HuroofiTokens.Navy, Quiet, large = true, "stage name"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "open stage name"),
        ContrastPair(HuroofiTokens.Muted, Quiet, large = false, "stage number and letters"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "open stage number and letters"),
        ContrastPair(CurrentRing, HuroofiTokens.Sky, large = true, "current card ring"),
        ContrastPair(HuroofiTokens.Success, HuroofiTokens.Sky, large = true, "finished card ring"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Success, large = true, "finished tick"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Primary, large = true, "play triangle"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = true, "lock icon"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Success, large = true, "learned chip"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "learning chip"),
        ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = true, "learning chip ring"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = true, "to-go chip"),
    ) + ButtonKind.Primary.contrastPairs() + NavSpec.textPairs

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(stage.accent, Quiet, large = false, "current stage number"),
        ContrastPair(stage.accent, HuroofiTokens.Card, large = false, "current count"),
    )
}

/** One letter of a stage and where the child is with it. */
data class StageChip(val letter: Letter, val state: LetterState)

/** One card of the map: the stage, how it shows, and its 4 letters. */
data class MapStage(val stage: Stage, val state: StageState, val chips: List<StageChip>)

/** "16 of 28 letters learned · finish one stage to unlock the next". Counts come from content. */
fun mapSubtitle(stages: List<MapStage>): String {
    val learned = stages.sumOf { s -> s.chips.count { it.state == LetterState.LEARNED } }
    val how = if (stages.any { it.state == StageState.OPEN }) "start any stage" else "finish one stage to unlock the next"
    return "$learned of ${stages.sumOf { it.chips.size }} letters learned · $how"
}

private val PlayTriangle = "M8 5v14l11-7z"

/**
 * Seven stage cards down a dotted line, scrolled to the current one. The current card shows its
 * letters and a "Continue with" button ([onPlay]); a finished card replays one of its letters
 * ([onReplay], plan 11 decision 2); an open card (Unlock all) starts its stage ([onOpen]); a locked
 * card says no with [onLocked] and a wiggle. In trace-only mode the button reads "Trace".
 */
@Composable
fun MapScreen(
    stages: List<MapStage>,
    traceOnly: Boolean,
    onPlay: () -> Unit,
    onReplay: (Stage) -> Unit,
    onOpen: (Stage) -> Unit,
    onLocked: () -> Unit,
    navTabs: List<NavTab>,
    onTab: (NavTab) -> Unit,
) {
    CappedWidth(HuroofiTokens.Sky) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            val scroll = rememberScrollState()
            var currentTop by remember { mutableIntStateOf(-1) }
            // Open at the current stage, with the card above it peeking in.
            LaunchedEffect(currentTop) { if (currentTop > 0) scroll.scrollTo(currentTop / 2) }
            Column(
                Modifier.weight(1f).verticalScroll(scroll).padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(MapSpec.Gap),
            ) {
                Column {
                    Text("Letter map", style = HuroofiText.screenTitle, color = HuroofiTokens.Navy)
                    Text(mapSubtitle(stages), style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold), color = HuroofiTokens.Muted)
                }
                Box {
                    DottedLine(Modifier.matchParentSize())
                    Column(verticalArrangement = Arrangement.spacedBy(MapSpec.Gap)) {
                        for (s in stages) {
                            MapCard(
                                s,
                                traceOnly,
                                modifier = if (s.state == StageState.CURRENT) {
                                    Modifier.onGloballyPositioned { currentTop = it.positionInParent().y.toInt() }
                                } else Modifier,
                                onPlay = onPlay,
                                onReplay = { onReplay(s.stage) },
                                onOpen = { onOpen(s.stage) },
                                onLocked = onLocked,
                            )
                        }
                    }
                }
            }
            LearnNav(NavTab.MAP, navTabs, onTab)
        }
    }
}

/** The prototype's `.map-line`: a dotted line from the first card to the last, behind the cards. */
@Composable
private fun DottedLine(modifier: Modifier) {
    Canvas(modifier.padding(top = 40.dp, bottom = 60.dp)) {
        val x = MapSpec.LineX.toPx()
        val w = MapSpec.LineWidth.toPx()
        drawLine(
            MapSpec.LineColor,
            Offset(x, 0f),
            Offset(x, size.height),
            strokeWidth = w,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, w * 2f)),
        )
    }
}

@Composable
private fun MapCard(
    s: MapStage,
    traceOnly: Boolean,
    modifier: Modifier,
    onPlay: () -> Unit,
    onReplay: () -> Unit,
    onOpen: () -> Unit,
    onLocked: () -> Unit,
) {
    val colors = s.stage.colors()
    val shape = RoundedCornerShape(MapSpec.CardCorner)
    val (fill, ring, edge) = when (s.state) {
        StageState.CURRENT -> Triple(MapSpec.Quiet, MapSpec.CurrentRing, colors.accent)
        StageState.FINISHED -> Triple(HuroofiTokens.Card, HuroofiTokens.Success, HuroofiTokens.SuccessShadow)
        StageState.OPEN -> Triple(HuroofiTokens.Card, HuroofiTokens.Card, MapSpec.QuietEdge)
        StageState.LOCKED -> Triple(MapSpec.Quiet, HuroofiTokens.Card, MapSpec.QuietEdge)
    }
    val wiggle = rememberWiggle()
    val name = "Stage ${s.stage.stage}, ${s.stage.name}"
    val tap: Modifier = when (s.state) {
        StageState.LOCKED -> Modifier
            .clickable(role = Role.Button) {
                wiggle.play()
                onLocked()
            }
            .clearAndSetSemantics { contentDescription = "$name, locked" }
        StageState.FINISHED -> Modifier
            .clickable(role = Role.Button) {
                wiggle.play()
                onReplay()
            }
            .clearAndSetSemantics { contentDescription = "$name, finished. Tap to play a letter again" }
        StageState.OPEN -> Modifier
            .clickable(role = Role.Button, onClick = onOpen)
            .clearAndSetSemantics { contentDescription = "$name. Start" }
        StageState.CURRENT -> Modifier.semantics { contentDescription = "$name, playing now" }
    }
    Column(
        modifier
            .fillMaxWidth()
            .wiggle(wiggle)
            .dropEdge(edge, MapSpec.CardEdge, shape)
            .clip(shape)
            .background(fill)
            .border(MapSpec.CardRing, ring, shape)
            .then(tap)
            .padding(horizontal = 14.dp + MapSpec.CardRing, vertical = 12.dp + MapSpec.CardRing),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(MapSpec.Picture).background(HuroofiTokens.Card, RoundedCornerShape(MapSpec.PictureCorner)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    stagePicture(s.stage),
                    contentDescription = null,
                    modifier = Modifier.size(MapSpec.PictureImage).alpha(if (s.state == StageState.LOCKED) 0.6f else 1f),
                )
            }
            // Number, name and letters are in the card's label; hidden so TalkBack reads them once (plan 08 task 6.2).
            Column(Modifier.weight(1f).clearAndSetSemantics {}) {
                Text(
                    "Stage ${s.stage.stage}",
                    style = HuroofiText.caption.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = if (s.state == StageState.CURRENT) colors.accent else HuroofiTokens.Muted,
                )
                Text(
                    s.stage.name,
                    style = HuroofiText.sectionHeading.copy(fontSize = MapSpec.NAME_SP.sp, lineHeight = 22.sp),
                    color = HuroofiTokens.Navy,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (s.state != StageState.CURRENT) {
                    ArabicText(s.chips.joinToString(" ") { it.letter.letter }, size = MapSpec.LETTERS_SP.sp, color = HuroofiTokens.Muted)
                }
            }
            when (s.state) {
                StageState.CURRENT -> Text(
                    "${s.chips.count { it.state == LetterState.LEARNED }}/${s.chips.size}",
                    Modifier
                        .background(HuroofiTokens.Card, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .clearAndSetSemantics {},
                    style = HuroofiText.caption.copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold),
                    color = colors.accent,
                )
                StageState.FINISHED -> Badge(HuroofiTokens.Success) { LineIcon(LearnIcons.Check, HuroofiTokens.Card, size = 20.dp, strokeWidth = 3.2f) }
                StageState.OPEN -> Box(
                    Modifier
                        .size(MapSpec.Play)
                        .dropEdge(HuroofiTokens.PrimaryShadow, 4.dp, CircleShape)
                        .background(HuroofiTokens.Primary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { PlayIcon(HuroofiTokens.Card) }
                StageState.LOCKED -> Badge(HuroofiTokens.Card) { LineIcon(LearnIcons.Lock, HuroofiTokens.Muted, size = 20.dp) }
            }
        }
        if (s.state == StageState.CURRENT) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (chip in s.chips) MapChip(chip, Modifier.weight(1f))
                }
            }
            val learning = s.chips.firstOrNull { it.state == LetterState.LEARNING } ?: s.chips.first { it.state != LetterState.LEARNED }
            val label = if (traceOnly) withArabic("Trace ", s.chips.first().letter.letter) else withArabic("Continue with ", learning.letter.letter)
            PrimaryButton(label, onClick = onPlay)
        }
    }
}

@Composable
private fun Badge(fill: Color, content: @Composable () -> Unit) {
    Box(Modifier.size(MapSpec.Badge).background(fill, CircleShape), contentAlignment = Alignment.Center) { content() }
}

/** A letter of the current stage: learned green, today's ringed in blue, the rest white. */
@Composable
private fun MapChip(chip: StageChip, modifier: Modifier) {
    val shape = RoundedCornerShape(MapSpec.ChipCorner)
    val (fill, text) = when (chip.state) {
        LetterState.LEARNED -> HuroofiTokens.Success to HuroofiTokens.Card
        LetterState.LEARNING -> HuroofiTokens.Card to HuroofiTokens.Navy
        else -> HuroofiTokens.Card to HuroofiTokens.Muted
    }
    Box(
        modifier
            .height(MapSpec.Chip)
            .background(fill, shape)
            .then(if (chip.state == LetterState.LEARNING) Modifier.border(MapSpec.ChipRing, HuroofiTokens.Primary, shape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        CenteredLetter(chip.letter.letter, size = MapSpec.CHIP_SP.sp, color = text, style = HuroofiText.arabicChip)
    }
}

/** Filled play triangle from the prototype. Decorative: the card carries the label. */
@Composable
private fun PlayIcon(color: Color) {
    val path = remember { PathParser().parsePathString(PlayTriangle).toPath() }
    Canvas(Modifier.width(26.dp).height(26.dp)) {
        val u = size.width / 24f
        scale(u, u, pivot = Offset.Zero) { drawPath(path, color) }
    }
}

/**
 * Letter map for the stored progress. "Continue with" opens Meet for the learning letter. A finished
 * card opens Meet for one of its letters at random, never the same one twice in a row.
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
        traceOnly = traceOnly,
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
            traceOnly = false,
            onPlay = {},
            onReplay = {},
            onOpen = {},
            onLocked = {},
            navTabs = NavTab.entries,
            onTab = {},
        )
    }
}
