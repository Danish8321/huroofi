package com.huroofi.app.learn

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.highlightedWord
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Sizes and colours of Play (`Quiz.html`, plan 07 decision 4). */
object QuizSpec {
    val Close = 64.dp
    val Sound = 64.dp
    val Chip = 64.dp
    val Dot = 18.dp
    val CardCorner = 28.dp
    val TileCorner = 30.dp
    val TileBorder = 5.dp
    val Picture = 128.dp
    val Badge = 40.dp
    val Panel = 150.dp
    val EdgeColor = Color(0xFFCFE2F7)
    val RightShadow = Color(0xFF9FD58A)
    val RightBackground = Color(0xFFD6F2C8)
    val RightText = HuroofiTokens.SuccessShadow
    /** Soft orange, never red (the prototype's red-orange fails the no-red rule). */
    val WrongBackground = Color(0xFFFFE4D6)
    val WrongText = Color(0xFFA35400)
    const val DIMMED = 0.4f
    const val CHIP_SP = 46f

    /** The smallest option tile; option tiles share the space left, so this is their floor. */
    val TileMin = 96.dp

    val touchSizes = listOf(Close, Sound, TileMin)
    val colors = listOf(
        HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, HuroofiTokens.Sun, HuroofiTokens.SunShadow,
        HuroofiTokens.Success, EdgeColor, RightShadow, RightBackground, RightText, WrongBackground, WrongText,
    )
}

private val CloseIcon = listOf("M6 6l12 12M18 6L6 18")

/** Delay before the right picture wiggles after a wrong tap, as in toddler Find. */
private const val HINT_DELAY_MS = 300L

/**
 * "Which one starts with …?". A wrong tap dims that picture and wiggles the right one; a right tap
 * turns it green. Continue appears once the last round is right and progress is saved.
 */
@Composable
fun QuizScreen(
    game: QuizGame,
    stage: Stage,
    canContinue: Boolean,
    onClose: () -> Unit,
    onHear: () -> Unit,
    onPick: (Letter) -> Unit,
    onContinue: () -> Unit,
) {
    val colors = stage.colors()
    val target = game.target
    Column(
        Modifier.fillMaxSize().background(HuroofiTokens.Sky).safeDrawingPadding().padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            RoundIconButton(
                contentDescription = "Leave game",
                onClick = onClose,
                toddler = true,
                size = QuizSpec.Close,
                containerColor = HuroofiTokens.Card,
                shadowColor = QuizSpec.EdgeColor,
                shadowDepth = 4.dp,
            ) { LineIcon(CloseIcon, HuroofiTokens.Navy, size = 26.dp, strokeWidth = 2.8f) }
            Row(
                Modifier.semantics { contentDescription = "${game.roundsDone} of ${game.rounds} done" },
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                repeat(game.rounds) { i ->
                    Box(
                        Modifier
                            .size(QuizSpec.Dot)
                            .background(if (i < game.roundsDone) HuroofiTokens.Success else HuroofiTokens.Card, CircleShape),
                    )
                }
            }
        }
        val card = RoundedCornerShape(QuizSpec.CardCorner)
        Row(
            Modifier
                .fillMaxWidth()
                .dropEdge(QuizSpec.EdgeColor, 6.dp, card)
                .background(HuroofiTokens.Card, card)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            RoundIconButton(
                contentDescription = "Hear the question",
                onClick = onHear,
                toddler = true,
                size = QuizSpec.Sound,
                containerColor = HuroofiTokens.Sun,
                shadowColor = HuroofiTokens.SunShadow,
            ) { SoundIcon(HuroofiTokens.Navy, size = 30.dp) }
            Text("Which one starts with", Modifier.weight(1f), style = HuroofiText.buttonPrimary, color = HuroofiTokens.Navy)
            Box(Modifier.size(QuizSpec.Chip).background(colors.pastel, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                ArabicText(target.letter, Modifier.wrapContentHeight(unbounded = true), size = QuizSpec.CHIP_SP.sp, color = HuroofiTokens.Navy)
            }
        }
        OptionGrid(game, Modifier.weight(1f), onPick)
        Box(Modifier.fillMaxWidth().height(QuizSpec.Panel), contentAlignment = Alignment.BottomCenter) {
            val wrong = game.wrongPick
            when {
                game.solved -> RightPanel(target, colors.accent, showContinue = game.finished, canContinue = canContinue, onContinue = onContinue)
                wrong != null -> WrongPanel(wrong)
                else -> Box(
                    Modifier.fillMaxWidth().height(64.dp).background(HuroofiTokens.Card, RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Listen, then tap a picture", style = HuroofiText.body.copy(fontWeight = FontWeight.Bold), color = HuroofiTokens.Muted)
                }
            }
        }
    }
}

/** 2 × 2 for four pictures, one column for three; tiles share the space left. */
@Composable
private fun OptionGrid(game: QuizGame, modifier: Modifier, onPick: (Letter) -> Unit) {
    val rows = if (game.options.size == 4) game.options.chunked(2) else game.options.map { listOf(it) }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        for (row in rows) {
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (option in row) {
                    OptionTile(option, game, Modifier.weight(1f).fillMaxHeight()) { onPick(option) }
                }
            }
        }
    }
}

@Composable
private fun OptionTile(option: Letter, game: QuizGame, modifier: Modifier, onClick: () -> Unit) {
    val isTarget = game.isRight(option)
    val solvedHere = game.solved && isTarget
    val dimmed = game.wrongPick == option
    val tilt = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    // After a wrong tap the right picture wiggles; never red, never an X (decision 4).
    LaunchedEffect(game.wrongTaps) {
        if (game.wrongTaps == 0 || !isTarget) return@LaunchedEffect
        delay(HINT_DELAY_MS)
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
    val shape = RoundedCornerShape(QuizSpec.TileCorner)
    Box(
        modifier
            .sizeIn(minHeight = QuizSpec.TileMin)
            .rotate(tilt.value)
            .scale(scale.value)
            .alpha(if (dimmed) QuizSpec.DIMMED else 1f)
            .semantics { contentDescription = option.meaningEn.replaceFirstChar { it.uppercase() } }
            // The colour change is the feedback; a square ripple would spill past the rounded tile.
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onClick),
    ) {
        Box(
            Modifier
                .matchParentSize()
                .dropEdge(if (solvedHere) QuizSpec.RightShadow else QuizSpec.EdgeColor, 6.dp, shape)
                .background(HuroofiTokens.Card, shape)
                .border(QuizSpec.TileBorder, if (solvedHere) HuroofiTokens.Success else HuroofiTokens.Card, shape)
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(letterPicture(option), contentDescription = null, modifier = Modifier.sizeIn(maxWidth = QuizSpec.Picture, maxHeight = QuizSpec.Picture).fillMaxSize())
        }
        if (solvedHere) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp)
                    .size(QuizSpec.Badge)
                    .background(HuroofiTokens.Success, CircleShape),
                contentAlignment = Alignment.Center,
            ) { LineIcon(LearnIcons.Check, HuroofiTokens.Card, size = 24.dp, strokeWidth = 3.2f) }
        }
    }
}

@Composable
private fun WrongPanel(pick: Letter) {
    Column(
        Modifier.fillMaxWidth().background(QuizSpec.WrongBackground, RoundedCornerShape(24.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text("Almost! Try again", style = HuroofiText.buttonPrimary, color = QuizSpec.WrongText)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "${pick.meaningEn.replaceFirstChar { it.uppercase() }} starts with",
                style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold),
                color = HuroofiTokens.Navy,
            )
            ArabicText(pick.wordFirst, size = 24.sp, color = HuroofiTokens.Navy)
        }
    }
}

@Composable
private fun RightPanel(target: Letter, accent: Color, showContinue: Boolean, canContinue: Boolean, onContinue: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(QuizSpec.RightBackground, RoundedCornerShape(26.dp)).padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Great job!", style = HuroofiText.buttonPrimary, color = QuizSpec.RightText)
            ArabicText(highlightedWord(target.wordFirst, target.wordRest, accent), size = 24.sp, color = HuroofiTokens.Navy)
            Text("starts with", style = HuroofiText.body.copy(fontWeight = FontWeight.Bold), color = HuroofiTokens.Navy)
            ArabicText(target.letter, size = 24.sp, color = HuroofiTokens.Navy)
        }
        if (showContinue) PrimaryButton("Continue", onClick = onContinue, kind = ButtonKind.Success, enabled = canContinue)
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun QuizReaderPreview() {
    val l = LearnPreviewData.letters
    HuroofiTheme {
        QuizScreen(QuizGame(l[1], l, wrongPick = l[0], wrongTaps = 1), LearnPreviewData.stage, false, {}, {}, {}, {})
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun QuizPreschoolDonePreview() {
    val l = LearnPreviewData.letters
    HuroofiTheme {
        QuizScreen(QuizGame(l[1], l.take(3), roundsDone = 3, solved = true), LearnPreviewData.stage, true, {}, {}, {}, {})
    }
}
