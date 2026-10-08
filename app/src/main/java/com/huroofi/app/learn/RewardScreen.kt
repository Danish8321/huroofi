package com.huroofi.app.learn

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.components.calmMotion
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.components.letterPicture
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
import kotlin.random.Random

/** Sizes and colours of Reward (plan 15 decision 6, `Reward.html`). The page is the stage's own pastel. */
object RewardSpec {
    val Medal = 250.dp
    val Sticker = 190.dp
    val TileHeight = 96.dp
    val TileCorner = 22.dp
    val TileEdge = Color(0x14000000)
    val TilePicture = 40.dp
    val CardPicture = 72.dp
    val SquareButton = 64.dp
    val SquareBorder = Color(0xFFF0CF6A)
    val SquareIcon = Color(0xFFA35400)
    val DoneBorder = Color(0xFF158048)
    val DonePastel = Color(0xFFE3F5EA)
    val DoneText = Color(0xFF0F5F35)
    const val TITLE_SP = 34f
    const val TILE_SP = 32f
    const val CARD_TITLE_SP = 22f
    const val CARD_ARABIC_SP = 24f
    const val CHIP_SP = 16f
    const val SUB_SP = 16f
    /** The prototype has 14; the type floor is 16. */
    const val TAG_SP = 16f
    val textSizes = listOf(TITLE_SP, TILE_SP, CARD_TITLE_SP, CARD_ARABIC_SP, CHIP_SP, SUB_SP, TAG_SP)

    /** Confetti from the prototype, minus its red-orange (no red on child screens). */
    val confetti = listOf(Color(0xFFFF6FA5), Color(0xFF3B8CF0), Color(0xFF3BAA5C), Color(0xFF8A6CE8), HuroofiTokens.Sun)

    val touchSizes = listOf(HuroofiDimens.PrimaryButtonHeight, SquareButton)
    val colors = listOf(
        TileEdge, SquareBorder, SquareIcon, DoneBorder, DonePastel, DoneText,
        HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted,
    ) + confetti

    /** Rays and confetti are celebration, not cues. Stage-coloured text is in [stagePairs]. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Navy, large = false, "NEW STICKER chip"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "learned letter tile"),
        ContrastPair(SquareIcon, HuroofiTokens.Card, large = true, "sticker book button icon"),
        ContrastPair(DoneText, DonePastel, large = false, "ALL DONE"),
        ContrastPair(HuroofiTokens.Navy, DonePastel, large = true, "All 28 letters learned"),
    ) + ButtonKind.Primary.contrastPairs()

    /** Each stage is the page once (its reward) and the unlock card once (the stage before's reward). */
    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(HuroofiTokens.Navy, stage.pastel, large = true, "reward title, unlocked stage name"),
        ContrastPair(HuroofiTokens.Muted, stage.pastel, large = false, "letters learned line"),
        ContrastPair(HuroofiTokens.Muted, stage.pastel, large = true, "unlocked stage letters"),
        ContrastPair(stage.accent, stage.pastel, large = false, "UNLOCKED"),
    )
}

/**
 * Stage complete: confetti, the new sticker on a turning medal, the stage's letters with their
 * pictures, and the stage it opens. Tapping the sticker says its letters. [next] is null after the
 * last stage. [unlocked] is false for a stage finished ahead of progress under Unlock all: no card,
 * and the button says Back to map (plan 13 decision 3). Both buttons leave the lesson for good.
 */
@Composable
fun RewardScreen(
    stage: Stage,
    letters: List<Letter>,
    next: Stage?,
    onSticker: () -> Unit,
    onNext: () -> Unit,
    onStickers: () -> Unit,
    unlocked: Boolean = true,
) {
    val colors = stage.colors()
    Box(Modifier.fillMaxSize().background(colors.pastel)) {
        // With animations removed, confetti would hang frozen in the air; leave it out (plan 14 decision 3).
        if (!calmMotion()) Confetti(Modifier.fillMaxSize())
        CappedWidth(Color.Transparent) {
            Column(
                Modifier.fillMaxSize().safeDrawingPadding().padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        "${stage.name} done!",
                        style = HuroofiText.screenTitle.copy(fontSize = RewardSpec.TITLE_SP.sp, fontWeight = FontWeight.ExtraBold),
                        color = HuroofiTokens.Navy,
                        textAlign = TextAlign.Center,
                    )
                    Medal(stage, colors, onSticker)
                    LetterTiles(letters)
                    Text(
                        "${letters.size} letters learned · added to your sticker book",
                        style = HuroofiText.body.copy(fontSize = RewardSpec.SUB_SP.sp, fontWeight = FontWeight.Bold),
                        color = HuroofiTokens.Muted,
                        textAlign = TextAlign.Center,
                    )
                    if (unlocked) UnlockCard(next)
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    StickerBookButton(onStickers)
                    PrimaryButton(
                        if (unlocked && next != null) "Go to ${next.name}" else "Back to map",
                        onClick = onNext,
                        modifier = Modifier.weight(1f),
                        icon = ButtonIcons.Next,
                    )
                }
            }
        }
    }
}

/** The new sticker on a slowly turning sunburst in the stage colour, with its "new" chip. */
@Composable
private fun Medal(stage: Stage, colors: StageColors, onTap: () -> Unit) {
    val calm = calmMotion()
    val turn = rememberInfiniteTransition(label = "rays")
    val spin by turn.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(RAYS_TURN_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "spin",
    )
    val pop = remember { Animatable(if (calm) 1f else 0f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow)) }
    val wiggle = rememberWiggle()
    Box(Modifier.size(RewardSpec.Medal), contentAlignment = Alignment.Center) {
        val ray = colors.border.copy(alpha = RAY_ALPHA)
        Canvas(Modifier.fillMaxSize()) {
            val start = if (calm) 0f else spin
            for (i in 0 until RAYS) drawArc(ray, start + i * RAY_STEP, RAY_SWEEP, useCenter = true)
        }
        StickerBadge(
            stage,
            size = RewardSpec.Sticker,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = pop.value
                    scaleY = pop.value
                }
                .wiggle(wiggle)
                .clickable(interactionSource = null, indication = null, role = Role.Button, onClickLabel = "Hear the letters") {
                    wiggle.play()
                    onTap()
                },
        )
        Text(
            "NEW STICKER",
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
                .background(HuroofiTokens.Navy, RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            style = HuroofiText.body.copy(fontSize = RewardSpec.CHIP_SP.sp, fontWeight = FontWeight.ExtraBold),
            color = HuroofiTokens.Card,
        )
    }
}

private const val RAYS = 12
private const val RAY_STEP = 30f
private const val RAY_SWEEP = 12f
private const val RAY_ALPHA = 0.35f
private const val RAYS_TURN_MS = 18_000

/** The stage's letters right to left, four to a row, each with its picture. */
@Composable
private fun LetterTiles(letters: List<Letter>) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in letters.sortedBy { it.index }.chunked(TILES_PER_ROW)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (letter in row) {
                        val shape = RoundedCornerShape(RewardSpec.TileCorner)
                        Column(
                            Modifier
                                .weight(1f)
                                .height(RewardSpec.TileHeight)
                                .dropEdge(RewardSpec.TileEdge, 4.dp, shape)
                                .background(HuroofiTokens.Card, shape),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            CenteredLetter(letter.letter, size = RewardSpec.TILE_SP.sp, color = HuroofiTokens.Navy)
                            Image(letterPicture(letter), contentDescription = null, modifier = Modifier.size(RewardSpec.TilePicture))
                        }
                    }
                    repeat(TILES_PER_ROW - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

private const val TILES_PER_ROW = 4

/** What this stage opened, in that stage's colours; after the last stage, the whole alphabet. */
@Composable
private fun UnlockCard(next: Stage?) {
    val colors = next?.colors()
    val border = colors?.border ?: RewardSpec.DoneBorder
    val pastel = colors?.pastel ?: RewardSpec.DonePastel
    val shape = RoundedCornerShape(28.dp)
    Row(
        Modifier.fillMaxWidth().background(pastel, shape).border(4.dp, border, shape).padding(start = 12.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(RewardSpec.CardPicture).background(HuroofiTokens.Card, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (next != null) {
                Image(stagePicture(next), contentDescription = null, modifier = Modifier.size(56.dp))
            } else {
                StarIcon(HuroofiTokens.Sun, size = 52.dp, outline = HuroofiTokens.SunShadow)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(
                if (next != null) "UNLOCKED" else "ALL DONE",
                style = HuroofiText.body.copy(fontSize = RewardSpec.TAG_SP.sp, fontWeight = FontWeight.ExtraBold),
                color = colors?.accent ?: RewardSpec.DoneText,
            )
            Text(
                next?.name ?: "All 28 letters learned!",
                style = HuroofiText.body.copy(fontSize = RewardSpec.CARD_TITLE_SP.sp, fontWeight = FontWeight.ExtraBold),
                color = HuroofiTokens.Navy,
            )
        }
        if (next != null) {
            ArabicText(next.letters.joinToString(" "), size = RewardSpec.CARD_ARABIC_SP.sp, color = HuroofiTokens.Muted)
        }
    }
}

/** A square way to the sticker book, beside the main button. */
@Composable
private fun StickerBookButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        Modifier
            .size(RewardSpec.SquareButton)
            .clip(shape)
            .background(HuroofiTokens.Card)
            .border(3.dp, RewardSpec.SquareBorder, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "Sticker book" },
        contentAlignment = Alignment.Center,
    ) {
        LineIcon(ButtonIcons.Sticker, RewardSpec.SquareIcon, size = 30.dp)
    }
}

/** One confetti piece: where it starts, how fast it falls, its turn and colour. Fractions of the box. */
private class Piece(val x: Float, val phase: Float, val speed: Float, val spin: Float, val color: Color, val round: Boolean)

/** Simple falling confetti on a Canvas, looping. Decorative. */
@Composable
private fun Confetti(modifier: Modifier) {
    val pieces = remember {
        val r = Random(7)
        List(CONFETTI_COUNT) {
            Piece(r.nextFloat(), r.nextFloat(), 0.6f + r.nextFloat() * 0.6f, r.nextFloat() * 360f, RewardSpec.confetti[it % RewardSpec.confetti.size], r.nextBoolean())
        }
    }
    val t by rememberInfiniteTransition(label = "confetti").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(CONFETTI_LOOP_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "fall",
    )
    Canvas(modifier) {
        val w = 12.dp.toPx()
        val h = 24.dp.toPx()
        for (p in pieces) {
            val y = ((p.phase + t * p.speed) % 1f) * (size.height + h) - h
            val center = Offset(p.x * size.width, y)
            if (p.round) {
                drawCircle(p.color, w / 2f, center)
            } else {
                rotate(p.spin + t * 360f * p.speed, center) {
                    drawRoundRect(p.color, Offset(center.x - w / 2f, center.y - h / 2f), Size(w, h), CornerRadius(4.dp.toPx()))
                }
            }
        }
    }
}

private const val CONFETTI_COUNT = 24
private const val CONFETTI_LOOP_MS = 6_000

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun RewardStage1Preview() {
    HuroofiTheme { RewardScreen(LearnPreviewData.stage, LearnPreviewData.letters, LearnPreviewData.nextStage, onSticker = {}, onNext = {}, onStickers = {}) }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun RewardLastStagePreview() {
    HuroofiTheme { RewardScreen(LearnPreviewData.stages.last(), LearnPreviewData.letters, next = null, onSticker = {}, onNext = {}, onStickers = {}) }
}
