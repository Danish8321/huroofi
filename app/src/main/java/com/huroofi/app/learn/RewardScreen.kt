package com.huroofi.app.learn

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import kotlin.random.Random

/** Sizes and colours of Reward (`Reward.html`, plan 07 decision 5). */
object RewardSpec {
    val Background = Color(0xFFFFF4D6)
    val StickerEdge = Color(0xFFF3DC9C)
    val NewSticker = Color(0xFFA35F00)
    val StarOutline = Color(0xFFC98A00)
    val BigStar = 104.dp
    val SmallStar = 78.dp
    val Tile = 58.dp
    val TileBorder = 3.dp
    val CardCorner = 32.dp
    const val TITLE_SP = 36f
    const val TILE_SP = 34f
    const val STICKER_NAME_SP = 24f
    val textSizes = listOf(TITLE_SP, TILE_SP, STICKER_NAME_SP)

    /** Confetti from the prototype, minus its red-orange (no red on child screens). */
    val confetti = listOf(Color(0xFFFF6FA5), Color(0xFF3B8CF0), Color(0xFF3BAA5C), Color(0xFF8A6CE8), HuroofiTokens.Sun)

    val touchSizes = listOf(HuroofiDimens.PrimaryButtonHeight)
    val colors = listOf(
        Background, StickerEdge, NewSticker, StarOutline, HuroofiTokens.Sun, HuroofiTokens.Success,
        HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted,
    ) + confetti
}

/**
 * Stage complete: confetti, always 3 stars, the stage's letters, its sticker and what opens next.
 * One button. [next] is null after the last stage.
 */
@Composable
fun RewardScreen(stage: Stage, letters: List<Letter>, next: Stage?, onNext: () -> Unit) {
    Box(Modifier.fillMaxSize().background(RewardSpec.Background)) {
        Confetti(Modifier.fillMaxSize())
        Column(
            Modifier.fillMaxSize().safeDrawingPadding().padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Stars()
                Spacer(Modifier.height(14.dp))
                Text(
                    "Stage ${stage.stage} complete!",
                    style = HuroofiText.screenTitle.copy(fontSize = RewardSpec.TITLE_SP.sp),
                    color = HuroofiTokens.Navy,
                    textAlign = TextAlign.Center,
                )
                Text(
                    "You learned ${letters.size} new letters",
                    style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold),
                    color = HuroofiTokens.Muted,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(18.dp))
                LetterTiles(letters)
                Spacer(Modifier.height(26.dp))
                StickerCard(stage, letters)
                Spacer(Modifier.height(16.dp))
                Text(
                    next?.let { "${it.name} unlocked" } ?: "All letters learned!",
                    Modifier.background(HuroofiTokens.Card, RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 8.dp),
                    style = HuroofiText.body.copy(fontWeight = FontWeight.ExtraBold),
                    color = HuroofiTokens.Navy,
                )
            }
            Spacer(Modifier.height(16.dp))
            PrimaryButton("Next stage", onClick = onNext, icon = ButtonIcons.Next)
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
                    ArabicText(letter.letter, Modifier.wrapContentHeight(unbounded = true), size = RewardSpec.TILE_SP.sp, color = HuroofiTokens.Navy)
                }
            }
        }
    }
}

@Composable
private fun StickerCard(stage: Stage, letters: List<Letter>) {
    val shape = RoundedCornerShape(RewardSpec.CardCorner)
    Row(
        Modifier
            .fillMaxWidth()
            .dropEdge(RewardSpec.StickerEdge, 8.dp, shape)
            .background(HuroofiTokens.Card, shape)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StickerBadge(stage, letters)
        Column {
            Text(
                "NEW STICKER",
                style = HuroofiText.caption.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.3.sp),
                color = RewardSpec.NewSticker,
            )
            Text(
                stickerName(stage),
                style = HuroofiText.screenTitle.copy(fontSize = RewardSpec.STICKER_NAME_SP.sp, lineHeight = 26.sp),
                color = HuroofiTokens.Navy,
            )
            Text("Added to your sticker book", style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold), color = HuroofiTokens.Muted)
        }
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
    HuroofiTheme { RewardScreen(LearnPreviewData.stage, LearnPreviewData.letters, LearnPreviewData.nextStage, onNext = {}) }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun RewardLastStagePreview() {
    HuroofiTheme { RewardScreen(LearnPreviewData.stages.last(), LearnPreviewData.letters, next = null, onNext = {}) }
}
