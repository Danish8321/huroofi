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
import com.huroofi.app.toddler.PARENT_LOCK_DESCRIPTION
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.ToddlerHeader
import com.huroofi.app.toddler.ToddlerPhrases
import com.huroofi.app.toddler.ToddlerScaffold
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.components.ParentLock
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.components.ToddlerHomeButton
import com.huroofi.app.ui.components.ToddlerHomeButtonSpec
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.components.parentLockPairs
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiDimens
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
    val NextButton = 96.dp
    /** Tallest a tile grows; on a tall phone the two tiles share the spare height up to this (plan 14 decision 1). */
    val TileHeight = 300.dp
    val TileCorner = 40.dp
    val TileBorder = 6.dp
    val TileShadow = 8.dp
    val PictureSize = 210.dp
    val HeaderStar = 30.dp
    const val PROMPT_SP = 42f
    const val WORD_SP = 50f
    val IdleShadow = Color(0xFFCFE2F7)
    val SolvedBackground = Color(0xFFDFF5D5)
    val SolvedBorder = HuroofiTokens.Success
    val SolvedShadow = Color(0xFF9FD58A)
    val PartyPink = Color(0xFFFF6FA5)
    val PartyBlue = Color(0xFF3B8CF0)

    /** Two pictures; the sun button repeats the question and is not a choice. */
    const val CHOICES = 2
    val touchSizes = listOf(SunButton, NextButton, TileHeight)
    val colors = listOf(IdleShadow, SolvedBackground, SolvedBorder, SolvedShadow, PartyPink, PartyBlue)

    /**
     * Party stars and the header stars' fill are reward decoration (a toddler loses nothing if
     * they are missed); the English line is 18 sp, so normal text.
     */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Outline, HuroofiTokens.Sky, large = true, "header star outline"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sun, large = true, "speaker icon"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "question Arabic"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "question English"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "tile word"),
        ContrastPair(HuroofiTokens.Navy, SolvedBackground, large = true, "found tile word"),
        ContrastPair(SolvedBorder, HuroofiTokens.Sky, large = true, "found tile border"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Success, large = true, "next chevron"),
    ) + ToddlerHomeButtonSpec.textPairs + parentLockPairs

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "tile word first letter"),
        ContrastPair(stage.accent, SolvedBackground, large = true, "found tile word first letter"),
    )

    val tile = FindTileStyle(
        corner = TileCorner, border = TileBorder, shadow = TileShadow, picture = PictureSize, wordSp = WORD_SP,
        stars = listOf(
            PartyStarSpot(64.dp, Alignment.TopStart, start = 18.dp, top = 14.dp),
            PartyStarSpot(48.dp, Alignment.TopEnd, end = 22.dp, top = 28.dp),
            PartyStarSpot(54.dp, Alignment.BottomEnd, end = 34.dp, bottom = 26.dp),
        ),
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
    val HeaderStar = 40.dp
    val StarGap = 12.dp
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
    val NextButton = 104.dp
    val NextIcon = 52.dp
    val NextShadow = 7.dp

    val tile = FindTileStyle(
        corner = 48.dp, border = 8.dp, shadow = 10.dp, picture = 250.dp, wordSp = 72f,
        stars = listOf(
            PartyStarSpot(84.dp, Alignment.TopStart, start = 26.dp, top = 22.dp),
            PartyStarSpot(64.dp, Alignment.TopEnd, end = 30.dp, top = 40.dp),
            PartyStarSpot(72.dp, Alignment.BottomEnd, end = 44.dp, bottom = 26.dp),
        ),
    )

    /** Touch sizes after scaling: buttons go through [minTouch]; tiles stay far above 64 dp. */
    fun touchSizes(scale: Float): List<Dp> =
        listOf(HomeButton, Speaker, NextButton).map { minTouch(it.value, scale).dp } + TileHeight * scale

    val colors = FindSpec.colors

    /** Same colours as the phone layout; the English line scales below 20 sp, so normal text there too. */
    val textPairs = FindSpec.textPairs

    fun stagePairs(stage: StageColors) = FindSpec.stagePairs(stage)
}

/** Where a party star sits on a solved tile. */
class PartyStarSpot(
    val size: Dp,
    val alignment: Alignment,
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp,
) {
    fun scaled(s: Float) = PartyStarSpot(size * s, alignment, start * s, top * s, end * s, bottom * s)
}

/** Sizes of one picture tile; the phone and tablet layouts differ only in these. */
class FindTileStyle(
    val corner: Dp,
    val border: Dp,
    val shadow: Dp,
    val picture: Dp,
    val wordSp: Float,
    val stars: List<PartyStarSpot>,
    /** Picture beside the word, for wide short tiles (phone), so the picture can fill the tile's height. */
    val sideBySide: Boolean = false,
) {
    fun scaled(s: Float) = FindTileStyle(corner * s, border * s, shadow * s, picture * s, wordSp * s, stars.map { it.scaled(s) }, sideBySide)
}

/**
 * Where's the…?: two pictures, find the one asked for. Endless sets of three (plan 05 decisions 5, 13).
 * A wide landscape window gets the tablet layout; everything else the phone layout (plan 08 decision 3).
 */
@Composable
fun FindScreen(onHome: () -> Unit, onRequestParentZone: () -> Unit) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val game = remember { FindGame(container.content.letters, Random.Default) }
    val audio = remember { FindAudio(PromptPlayer(container.sound, scope), Random.Default) }
    var set by remember { mutableStateOf(game.start()) }
    val round = set.round

    LaunchedEffect(round.target) { audio.ask(round.target) }
    DisposableEffect(Unit) { onDispose { audio.stop() } }

    val ask = { audio.ask(round.target) }
    val pick = { option: Letter ->
        val (after, result) = game.tap(set, option)
        set = after
        audio.onTap(result, after)
    }
    val next = { set = game.next(set) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (useTabletFind(maxWidth.value, maxHeight.value)) {
            FindTablet(set, tabletFindScale(maxWidth.value, maxHeight.value), onHome, onRequestParentZone, ask, pick, next)
        } else {
            FindPhone(set, onHome, onRequestParentZone, ask, pick, next)
        }
    }
}

@Composable
private fun FindPhone(
    set: FindSet,
    onHome: () -> Unit,
    onRequestParentZone: () -> Unit,
    onAsk: () -> Unit,
    onPick: (Letter) -> Unit,
    onNext: () -> Unit,
) {
    val round = set.round
    val colors = LocalHuroofiColors.current
    ToddlerScaffold {
        ToddlerHeader(onHome = onHome, onRequestParentZone = onRequestParentZone) {
            Stars(set, FindSpec.HeaderStar, 10.dp)
        }
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
        Box(Modifier.fillMaxWidth().height(FindSpec.NextButton + 6.dp), contentAlignment = Alignment.Center) {
            if (round.solved) NextButton(round, FindSpec.NextButton, 46.dp, HuroofiDimens.ButtonShadow, onNext)
        }
    }
}

/** The landscape tablet layout: the prototype at 1180 x 820, scaled by [scale] and centred. */
@Composable
private fun FindTablet(
    set: FindSet,
    scale: Float,
    onHome: () -> Unit,
    onRequestParentZone: () -> Unit,
    onAsk: () -> Unit,
    onPick: (Letter) -> Unit,
    onNext: () -> Unit,
) {
    val spec = FindTabletSpec
    val s = scale
    val round = set.round
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
                    Stars(set, spec.HeaderStar * s, spec.StarGap * s)
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
            Box(Modifier.fillMaxWidth().height((spec.NextButton + spec.NextShadow) * s), contentAlignment = Alignment.Center) {
                if (round.solved) {
                    NextButton(round, minTouch(spec.NextButton.value, s).dp, spec.NextIcon * s, spec.NextShadow * s, onNext)
                }
            }
        }
    }
}

/** The set's progress as header stars. */
@Composable
private fun Stars(set: FindSet, size: Dp, gap: Dp) {
    val colors = LocalHuroofiColors.current
    Row(
        Modifier.semantics { contentDescription = "${set.stars} of ${FindGame.ROUNDS_PER_SET} stars" },
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        repeat(FindGame.ROUNDS_PER_SET) { i ->
            StarIcon(fill = if (i < set.stars) colors.sun else colors.card, size = size)
        }
    }
}

/** The green Next button that pops in once the round is solved. */
@Composable
private fun NextButton(round: FindRound, size: Dp, iconSize: Dp, shadow: Dp, onNext: () -> Unit) {
    val colors = LocalHuroofiColors.current
    val pop = remember(round) { Animatable(0f) }
    LaunchedEffect(pop) { pop.animateTo(1f, tween(400)) }
    RoundIconButton(
        contentDescription = "Next",
        onClick = onNext,
        toddler = true,
        size = size,
        containerColor = colors.success,
        shadowColor = colors.successShadow,
        shadowDepth = shadow,
        modifier = Modifier.graphicsLayer {
            scaleX = pop.value
            scaleY = pop.value
            alpha = pop.value
        },
    ) { ChevronIcon(colors.card, pointsRight = true, size = iconSize) }
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
        if (solvedHere) {
            val fills = listOf(colors.sun, FindSpec.PartyPink, FindSpec.PartyBlue)
            style.stars.forEachIndexed { i, spot ->
                PartyStar(
                    fills[i],
                    spot.size,
                    i * PARTY_STAR_STAGGER_MS,
                    Modifier.align(spot.alignment).padding(start = spot.start, top = spot.top, end = spot.end, bottom = spot.bottom),
                )
            }
        }
    }
}

private const val PARTY_STAR_STAGGER_MS = 150

/** A star that pops in after [delayMs]. Decorative. */
@Composable
private fun PartyStar(fill: Color, size: Dp, delayMs: Int, modifier: Modifier) {
    val pop = remember { Animatable(0f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, tween(400, delayMillis = delayMs)) }
    StarIcon(
        fill = fill,
        size = size,
        outlineWidth = 1.2f,
        modifier = modifier.graphicsLayer {
            scaleX = pop.value
            scaleY = pop.value
            rotationZ = -30f * (1f - pop.value)
            alpha = pop.value
        },
    )
}
