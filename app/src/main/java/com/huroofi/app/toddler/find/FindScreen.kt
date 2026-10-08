package com.huroofi.app.toddler.find

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.data.content.Letter
import com.huroofi.app.toddler.CountdownSpec
import com.huroofi.app.toddler.FillNextButton
import com.huroofi.app.toddler.PARENT_LOCK_DESCRIPTION
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.RingNextButton
import com.huroofi.app.toddler.ToddlerHeader
import com.huroofi.app.toddler.ToddlerPhrases
import com.huroofi.app.toddler.ToddlerScaffold
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.LocalBuzz
import com.huroofi.app.ui.components.ParentLock
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StarBurst
import com.huroofi.app.ui.components.StarBurstSpec
import com.huroofi.app.ui.components.ToddlerHomeButton
import com.huroofi.app.ui.components.ToddlerHomeButtonSpec
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.components.parentLockPairs
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.highlightedWord
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Sizes and colours of Where's the…? (`ToddlerFind.html`, phone layout). */
object FindSpec {
    val SunButton = 84.dp
    /** Tallest a tile grows; on a tall phone the two tiles share the spare height up to this (plan 14 decision 1). */
    val TileHeight = 300.dp
    val TileCorner = 40.dp
    val TileBorder = 6.dp
    val TileShadow = 8.dp
    val PictureSize = 210.dp
    val HintIcon = 26.dp
    const val PROMPT_SP = 42f
    const val WORD_SP = 50f
    val IdleShadow = Color(0xFFCFE2F7)
    val SolvedBackground = Color(0xFFDFF5D5)
    val SolvedBorder = HuroofiTokens.Success
    val SolvedShadow = Color(0xFF9FD58A)

    /** Two pictures; the sun button repeats the question and is not a choice. */
    const val CHOICES = 2
    val touchSizes = listOf(SunButton, CountdownSpec.BarHeight, TileHeight)
    val colors = listOf(IdleShadow, SolvedBackground, SolvedBorder, SolvedShadow, StarBurstSpec.Light, CountdownSpec.BarFill)

    /**
     * The star burst is reward decoration (a toddler loses nothing if they are missed); the English
     * line and the hint are 18 sp, so normal text.
     */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "tap a picture hint"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sun, large = true, "speaker icon"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "question Arabic"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "question English"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "tile word"),
        ContrastPair(HuroofiTokens.Navy, SolvedBackground, large = true, "found tile word"),
        ContrastPair(SolvedBorder, HuroofiTokens.Sky, large = true, "found tile border"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Success, large = true, "next chevron"),
        ContrastPair(HuroofiTokens.Card, CountdownSpec.BarFill, large = true, "next chevron on the filled bar"),
    ) + ToddlerHomeButtonSpec.textPairs + parentLockPairs

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "tile word first letter"),
        ContrastPair(stage.accent, SolvedBackground, large = true, "found tile word first letter"),
    )

    val tile = FindTileStyle(
        corner = TileCorner, border = TileBorder, shadow = TileShadow, picture = PictureSize, wordSp = WORD_SP,
        burstScale = 0.75f,
        // The phone tile is wide and short; stacked, the word left the picture 96 dp (plan 11 decision 5).
        sideBySide = true,
    )
}

/**
 * Where's the…? landscape tablet layout (`ToddlerFindTablet.html`, plan 08 decisions 3 and 5).
 * Sizes are the prototype's at 1180 x 820; [FindTablet] multiplies them by one scale.
 */
object FindTabletSpec {
    val PadTop = 28.dp
    val PadSide = 40.dp
    val PadBottom = 36.dp
    val HomeButton = 84.dp
    val HomeIcon = 40.dp
    val HomeShadow = 6.dp
    val Speaker = 104.dp
    val SpeakerIcon = 50.dp
    val SpeakerShadow = 7.dp
    val SpeakerGap = 24.dp
    val SentenceTop = 6.dp
    val SentenceCorner = 32.dp
    val SentencePadSide = 40.dp
    val SentencePadTop = 4.dp
    val SentencePadBottom = 10.dp
    const val SENTENCE_SP = 60f
    const val ENGLISH_SP = 20f
    val TilesTop = 16.dp
    val TilesSide = 60.dp
    val TileGap = 48.dp
    val TileHeight = 380.dp
    val NextRing = CountdownSpec.Ring

    val tile = FindTileStyle(
        corner = 48.dp, border = 8.dp, shadow = 10.dp, picture = 250.dp, wordSp = 72f,
        burstScale = 0.9f,
    )

    /** Touch sizes after scaling: buttons go through [minTouch]; tiles stay far above 64 dp. */
    fun touchSizes(scale: Float): List<Dp> =
        listOf(HomeButton, Speaker, NextRing).map { minTouch(it.value, scale).dp } + TileHeight * scale

    val colors = FindSpec.colors

    /** Same colours as the phone layout; the English line scales below 20 sp, so normal text there too. */
    val textPairs = FindSpec.textPairs

    fun stagePairs(stage: StageColors) = FindSpec.stagePairs(stage)
}

/** Sizes of one picture tile; the phone and tablet layouts differ only in these. */
class FindTileStyle(
    val corner: Dp,
    val border: Dp,
    val shadow: Dp,
    val picture: Dp,
    val wordSp: Float,
    /** Spread of the [StarBurst] from the found picture. */
    val burstScale: Float,
    /** Picture beside the word, for wide short tiles (phone), so the picture can fill the tile's height. */
    val sideBySide: Boolean = false,
) {
    fun scaled(s: Float) = FindTileStyle(corner * s, border * s, shadow * s, picture * s, wordSp * s, burstScale * s, sideBySide)
}

/**
 * Where's the…?: two pictures, find the one asked for, round after round (plan 05 decision 13). Once
 * found, Next counts down 1 + 4 s and moves on by itself; a tap skips the wait (plan 15 decision 4).
 * A wide landscape window gets the tablet layout; everything else the phone layout (plan 08 decision 3).
 */
@Composable
fun FindScreen(onHome: () -> Unit, onRequestParentZone: () -> Unit) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val game = remember { FindGame(container.content.letters, Random.Default) }
    val audio = remember { FindAudio(PromptPlayer(container.sound, scope), Random.Default) }
    var round by remember { mutableStateOf(game.start()) }

    LaunchedEffect(round.target) { audio.ask(round.target) }
    DisposableEffect(Unit) { onDispose { audio.stop() } }

    val buzz = LocalBuzz.current
    val ask = { audio.ask(round.target) }
    val pick = { option: Letter ->
        val (after, result) = game.tap(round, option)
        round = after
        if (result == TapResult.CORRECT) buzz.confirm()
        audio.onTap(result, after)
    }
    val next = { if (round.solved) round = game.next(round) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (useTabletFind(maxWidth.value, maxHeight.value)) {
            FindTablet(round, tabletFindScale(maxWidth.value, maxHeight.value), onHome, onRequestParentZone, ask, pick, next)
        } else {
            FindPhone(round, onHome, onRequestParentZone, ask, pick, next)
        }
    }
}

@Composable
private fun FindPhone(
    round: FindRound,
    onHome: () -> Unit,
    onRequestParentZone: () -> Unit,
    onAsk: () -> Unit,
    onPick: (Letter) -> Unit,
    onNext: () -> Unit,
) {
    val colors = LocalHuroofiColors.current
    ToddlerScaffold {
        ToddlerHeader(onHome = onHome, onRequestParentZone = onRequestParentZone)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoundIconButton(
                contentDescription = ToddlerPhrases.whereIsEn(round.target),
                onClick = onAsk,
                toddler = true,
                size = FindSpec.SunButton,
                containerColor = colors.sun,
                shadowColor = colors.sunShadow,
            ) { SoundIcon(color = colors.navy, size = 40.dp) }
            Column(
                Modifier
                    .weight(1f)
                    .background(colors.card, RoundedCornerShape(26.dp, 26.dp, 26.dp, 8.dp))
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.End,
            ) {
                ArabicText(ToddlerPhrases.whereIsAr(round.target), size = FindSpec.PROMPT_SP.sp, color = colors.navy)
                Text(
                    ToddlerPhrases.whereIsEn(round.target),
                    style = HuroofiText.body.copy(fontWeight = FontWeight.Bold),
                    color = colors.muted,
                    textAlign = TextAlign.End,
                )
            }
        }
        for (option in round.options) {
            FindTile(
                letter = option,
                round = round,
                onClick = { onPick(option) },
                style = FindSpec.tile,
                modifier = Modifier.weight(1f).heightIn(max = FindSpec.TileHeight),
            )
        }
        Box(Modifier.fillMaxWidth().height(CountdownSpec.BarHeight + CountdownSpec.BarShadow), contentAlignment = Alignment.Center) {
            if (round.solved) FillNextButton(round, onNext) else TapHint()
        }
    }
}

/** The landscape tablet layout: the prototype at 1180 x 820, scaled by [scale] and centred. */
@Composable
private fun FindTablet(
    round: FindRound,
    scale: Float,
    onHome: () -> Unit,
    onRequestParentZone: () -> Unit,
    onAsk: () -> Unit,
    onPick: (Letter) -> Unit,
    onNext: () -> Unit,
) {
    val spec = FindTabletSpec
    val s = scale
    val colors = LocalHuroofiColors.current
    Box(Modifier.fillMaxSize().background(colors.sky), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .size(TABLET_FIND_DESIGN_WIDTH.dp * s, TABLET_FIND_DESIGN_HEIGHT.dp * s)
                .padding(start = spec.PadSide * s, end = spec.PadSide * s, top = spec.PadTop * s, bottom = spec.PadBottom * s),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                ToddlerHomeButton(
                    onClick = onHome,
                    size = minTouch(spec.HomeButton.value, s).dp,
                    iconSize = spec.HomeIcon * s,
                    shadow = spec.HomeShadow * s,
                )
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(spec.SpeakerGap * s, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RoundIconButton(
                        contentDescription = ToddlerPhrases.whereIsEn(round.target),
                        onClick = onAsk,
                        toddler = true,
                        size = minTouch(spec.Speaker.value, s).dp,
                        containerColor = colors.sun,
                        shadowColor = colors.sunShadow,
                        shadowDepth = spec.SpeakerShadow * s,
                    ) { SoundIcon(color = colors.navy, size = spec.SpeakerIcon * s) }
                }
                ParentLock(onRequestParentZone = onRequestParentZone, contentDescription = PARENT_LOCK_DESCRIPTION)
            }
            Column(
                Modifier
                    .padding(top = spec.SentenceTop * s)
                    .align(Alignment.CenterHorizontally)
                    .background(colors.card, RoundedCornerShape(spec.SentenceCorner * s))
                    .padding(
                        start = spec.SentencePadSide * s,
                        end = spec.SentencePadSide * s,
                        top = spec.SentencePadTop * s,
                        bottom = spec.SentencePadBottom * s,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ArabicText(ToddlerPhrases.whereIsAr(round.target), size = (spec.SENTENCE_SP * s).sp, color = colors.navy)
                Text(
                    ToddlerPhrases.whereIsEn(round.target),
                    style = HuroofiText.body.copy(fontWeight = FontWeight.Bold, fontSize = (spec.ENGLISH_SP * s).sp),
                    color = colors.muted,
                )
            }
            val tile = spec.tile.scaled(s)
            // The tiles take what is left above Next, up to their designed height, so a taller
            // sentence (large text setting) shrinks them instead of pushing Next off the screen.
            Box(Modifier.weight(1f).fillMaxWidth().padding(top = spec.TilesTop * s, start = spec.TilesSide * s, end = spec.TilesSide * s)) {
                Row(
                    Modifier.fillMaxWidth().height(spec.TileHeight * s + tile.shadow),
                    horizontalArrangement = Arrangement.spacedBy(spec.TileGap * s),
                ) {
                    for (option in round.options) {
                        FindTile(
                            letter = option,
                            round = round,
                            onClick = { onPick(option) },
                            style = tile,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
            Box(Modifier.fillMaxWidth().height(spec.NextRing * s), contentAlignment = Alignment.Center) {
                if (round.solved) RingNextButton(round, onNext, size = minTouch(spec.NextRing.value, s).dp)
            }
        }
    }
}

/** Before the answer: "Tap a picture", with a pointing hand. */
@Composable
private fun TapHint() {
    val colors = LocalHuroofiColors.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LineIcon(ButtonIcons.Hand, colors.muted, size = FindSpec.HintIcon, strokeWidth = 2f)
        Text("Tap a picture", style = HuroofiText.body.copy(fontWeight = FontWeight.Bold), color = colors.muted)
    }
}

@Composable
private fun FindTile(letter: Letter, round: FindRound, onClick: () -> Unit, style: FindTileStyle, modifier: Modifier = Modifier) {
    val content = LocalAppContainer.current.content
    val colors = LocalHuroofiColors.current
    val isTarget = letter == round.target
    val solvedHere = round.solved && isTarget
    val shape = RoundedCornerShape(style.corner)
    val nudge = remember { Animatable(0f) }
    val tilt = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }

    // Wrong tile: a small sideways nudge. Never red, never an X.
    LaunchedEffect(round.wrongTaps) {
        if (round.wrongTaps == 0) return@LaunchedEffect
        if (round.lastWrong == letter) {
            nudge.animateTo(0f, keyframes {
                durationMillis = 400
                -8f at 120
                8f at 280
            })
        } else if (isTarget) {
            // The right one wiggles after the 0.3 s hint delay.
            delay(FindAudio.HINT_DELAY_MS)
            launch {
                tilt.animateTo(0f, keyframes {
                    durationMillis = 800
                    -9f at 200
                    9f at 600
                })
            }
            scale.animateTo(1f, keyframes {
                durationMillis = 800
                1.1f at 200
                1.1f at 600
            })
        }
    }
    LaunchedEffect(solvedHere) {
        if (!solvedHere) {
            scale.snapTo(1f)
            return@LaunchedEffect
        }
        scale.animateTo(1.08f, keyframes {
            durationMillis = 600
            1.18f at 240
        })
    }

    val background = if (solvedHere) FindSpec.SolvedBackground else colors.card
    val border = if (solvedHere) FindSpec.SolvedBorder else colors.card
    val shadow = if (solvedHere) FindSpec.SolvedShadow else FindSpec.IdleShadow
    Box(
        modifier
            .fillMaxWidth()
            .offset { IntOffset(nudge.value.dp.roundToPx(), 0) }
            .semantics { contentDescription = letter.meaningEn.replaceFirstChar { it.uppercase() } }
            // The nudge or the party is the feedback; a square ripple would spill past the rounded tile.
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onClick),
    ) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = style.shadow)
                .background(shadow, shape),
        )
        val face = Modifier
            .fillMaxSize()
            .padding(bottom = style.shadow)
            .background(background, shape)
            .border(style.border, border, shape)
        val wiggle = Modifier.graphicsLayer {
            rotationZ = tilt.value
            scaleX = scale.value
            scaleY = scale.value
        }
        val word = @Composable {
            ArabicText(
                highlightedWord(letter.wordFirst, letter.wordRest, content.stageColors(letter).accent),
                size = style.wordSp.sp,
                color = colors.navy,
            )
        }
        if (style.sideBySide) {
            Row(
                face.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    letterPicture(letter),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.82f)
                        .sizeIn(maxWidth = style.picture, maxHeight = style.picture)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .then(wiggle),
                )
                word()
            }
        } else {
            Column(face, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Image(
                    letterPicture(letter),
                    contentDescription = null,
                    modifier = Modifier.weight(1f, fill = false).size(style.picture).then(wiggle),
                )
                word()
            }
        }
        if (solvedHere) StarBurst(scale = style.burstScale, topFraction = 0.5f)
    }
}
