package com.huroofi.app.learn

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
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
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.components.calmMotion
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.StageColors
import kotlin.random.Random

/** Sizes and colours of Reward (`Reward.html`, plan 07 decision 5). */
object RewardSpec {
    val Background = Color(0xFFFFF4D6)
    val StarOutline = Color(0xFFC98A00)
    val BigStar = 72.dp
    val SmallStar = 54.dp

    /** The new sticker is the hero (plan 11 decision 6); the words under it are for the grown-up. */
    val Hero = 260.dp
    val Tile = 58.dp
    val TileBorder = 3.dp
    const val TITLE_SP = 28f
    const val TILE_SP = 34f
    val textSizes = listOf(TITLE_SP, TILE_SP)

    /** Confetti from the prototype, minus its red-orange (no red on child screens). */
    val confetti = listOf(Color(0xFFFF6FA5), Color(0xFF3B8CF0), Color(0xFF3BAA5C), Color(0xFF8A6CE8), HuroofiTokens.Sun)

    val touchSizes = listOf(HuroofiDimens.PrimaryButtonHeight)
    val colors = listOf(
        Background, StarOutline, HuroofiTokens.Sun, HuroofiTokens.Success,
        HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted,
    ) + confetti

    /** Stars and confetti are celebration, not cues. Text is checked on the page colour. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, Background, large = true, "sticker name"),
        ContrastPair(HuroofiTokens.Muted, Background, large = false, "Stage complete, added to your sticker book"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "learned letter tile"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "next stage unlocked"),
    ) + ButtonKind.Primary.contrastPairs() + StickerSpec.textPairs

    fun stagePairs(stage: StageColors) = StickerSpec.stagePairs(stage)
}

/**
 * Stage complete: confetti, always 3 stars, the new sticker big in the middle, the stage's letters
 * and what opens next. One button. Tapping the sticker says its letters. [next] is null after the
 * last stage. [unlocked] is false for a stage finished ahead of progress under Unlock all: no pill,
 * and the button goes back to the map (plan 13 decision 3).
 */
@Composable
fun RewardScreen(stage: Stage, letters: List<Letter>, next: Stage?, onSticker: () -> Unit, onNext: () -> Unit, unlocked: Boolean = true) {
    Box(Modifier.fillMaxSize().background(RewardSpec.Background)) {
        // With animations removed, confetti would hang frozen in the air; leave it out (plan 14 decision 3).
        if (!calmMotion()) Confetti(Modifier.fillMaxSize())
        CappedWidth(Color.Transparent) {
            Column(
                Modifier.fillMaxSize().safeDrawingPadding().padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Stars()
                    Spacer(Modifier.height(12.dp))
                    StickerHero(stage, onSticker)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stickerName(stage),
                        style = HuroofiText.screenTitle.copy(fontSize = RewardSpec.TITLE_SP.sp),
                        color = HuroofiTokens.Navy,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        "Stage ${stage.stage} complete · added to your sticker book",
                        style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold),
                        color = HuroofiTokens.Muted,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(18.dp))
                    LetterTiles(letters)
                    if (unlocked) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            next?.let { "${it.name} unlocked" } ?: "All letters learned!",
                            Modifier.background(HuroofiTokens.Card, RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 8.dp),
                            style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold),
                            color = HuroofiTokens.Navy,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                PrimaryButton(if (unlocked) "Next stage" else "Back to map", onClick = onNext, icon = ButtonIcons.Next)
            }
        }
    }
}

@Composable
private fun Stars() {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        StarIcon(HuroofiTokens.Sun, Modifier.rotate(-12f), size = RewardSpec.SmallStar, outline = RewardSpec.StarOutline, outlineWidth = 1.2f)
        StarIcon(HuroofiTokens.Sun, size = RewardSpec.BigStar, outline = RewardSpec.StarOutline, outlineWidth = 1.2f)
        StarIcon(HuroofiTokens.Sun, Modifier.rotate(12f), size = RewardSpec.SmallStar, outline = RewardSpec.StarOutline, outlineWidth = 1.2f)
    }
}

@Composable
private fun LetterTiles(letters: List<Letter>) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (letter in letters.sortedBy { it.index }) {
                val shape = RoundedCornerShape(18.dp)
                Box(
                    Modifier
                        .size(RewardSpec.Tile)
                        .background(HuroofiTokens.Card, shape)
                        .border(RewardSpec.TileBorder, HuroofiTokens.Success, shape),
                    contentAlignment = Alignment.Center,
                ) {
                    CenteredLetter(letter.letter, size = RewardSpec.TILE_SP.sp, color = HuroofiTokens.Navy)
                }
            }
        }
    }
}

/**
 * The new sticker, big: it springs in, then bobs gently while the screen is open. A tap wiggles it
 * and says its letters (plan 11 decisions 2 and 6).
 */
@Composable
private fun StickerHero(stage: Stage, onTap: () -> Unit) {
    val pop = remember { Animatable(0f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow)) }
    val bob by rememberInfiniteTransition(label = "sticker bob").animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(BOB_MS, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob",
    )
    val wiggle = rememberWiggle()
    StickerBadge(
        stage,
        size = RewardSpec.Hero,
        modifier = Modifier
            .graphicsLayer {
                scaleX = pop.value
                scaleY = pop.value
                rotationZ = bob * BOB_DEGREES
                translationY = bob * BOB_DP.dp.toPx()
            }
            .wiggle(wiggle)
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClickLabel = "Hear the letters") {
                wiggle.play()
                onTap()
            },
    )
}

private const val BOB_MS = 1400
private const val BOB_DEGREES = 4f
private const val BOB_DP = 6

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
    HuroofiTheme { RewardScreen(LearnPreviewData.stage, LearnPreviewData.letters, LearnPreviewData.nextStage, onSticker = {}, onNext = {}) }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun RewardLastStagePreview() {
    HuroofiTheme { RewardScreen(LearnPreviewData.stages.last(), LearnPreviewData.letters, next = null, onSticker = {}, onNext = {}) }
}
