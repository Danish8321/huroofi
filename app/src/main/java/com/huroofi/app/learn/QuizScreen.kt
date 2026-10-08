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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.LineIcon
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.NotoNaskhArabic
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.highlightedWord
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Sizes and colours of Play (`Quiz.html`, plan 15 decision 6). */
object QuizSpec {
    val Close = 64.dp
    val Leo = 70.dp
    val Sound = 56.dp
    val Chip = 52.dp
    val ChipRing = 3.dp
    val Dot = 14.dp
    val DotNow = 36.dp
    val BubbleCorner = 28.dp
    val TileCorner = 32.dp
    val TileBorder = 6.dp
    val Picture = 120.dp
    val PictureFour = 110.dp
    val Star = 34.dp
    val Foot = 64.dp
    val EdgeColor = Color(0xFFCFE2F7)
    val DotTodo = Color(0xFFC9D6E6)
    val ChipFill = Color(0xFFFFF4D6)
    val ChipText = Color(0xFFA35400)
    /** Soft orange, never red (no-red rule). */
    val WrongBackground = Color(0xFFFFE4D6)
    val WrongText = Color(0xFF8A3B12)
    const val DIMMED = 0.45f
    const val FADED = 0.5f
    const val CHIP_SP = 34f
    const val WORD_SP = 44f
    const val WORD_FOUR_SP = 30f
    const val INLINE_SP = 20f
    val textSizes = listOf(CHIP_SP, WORD_SP, WORD_FOUR_SP, INLINE_SP)

    /** The smallest option tile; option tiles share the space left, so this is their floor. */
    val TileMin = 96.dp

    /** Tall enough for three [TileMin] tiles; a shorter window scrolls (tablet landscape). */
    val MinHeight = 740.dp

    /** The question bubble is at least as tall as Leo, so the whole bubble is one big tap target. */
    val touchSizes = listOf(Close, Leo, TileMin, HuroofiDimens.PrimaryButtonHeight)
    val colors = listOf(
        HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, HuroofiTokens.Sun, HuroofiTokens.SunShadow,
        HuroofiTokens.Primary, HuroofiTokens.Success, HuroofiTokens.SuccessShadow, EdgeColor, DotTodo, ChipFill, ChipText, WrongBackground, WrongText,
    )

    /** A dimmed or faded tile is inactive, so it is not checked. 20 sp ExtraBold is large; 16–18 sp is normal. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "close icon"),
        ContrastPair(HuroofiTokens.Success, HuroofiTokens.Sky, large = true, "round dot, done"),
        ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Sky, large = true, "round dot, now"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sun, large = true, "sound icon"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "Which starts with"),
        ContrastPair(ChipText, ChipFill, large = true, "target letter"),
        ContrastPair(HuroofiTokens.Success, HuroofiTokens.Sky, large = true, "right tile border"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "idle hint"),
        ContrastPair(WrongText, WrongBackground, large = true, "Almost! Try again"),
        ContrastPair(WrongText, WrongBackground, large = false, "wrong explanation"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "right word"),
    ) + ButtonKind.Success.contrastPairs()

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "right word first letter"),
    )
}

/** One round's dot: done (green), now (a blue pill), or still to come. */
enum class QuizDot { DONE, NOW, TODO }

/** Round [i]'s dot: a round turns green the moment it is answered. */
fun quizDot(game: QuizGame, i: Int): QuizDot = when {
    i < game.roundsDone -> QuizDot.DONE
    i == game.roundsDone && !game.solved -> QuizDot.NOW
    else -> QuizDot.TODO
}

private val CloseIcon = listOf("M6 6l12 12M18 6L6 18")

/** Delay before the right picture wiggles after a wrong tap, as in toddler Find. */
private const val HINT_DELAY_MS = 300L

/**
 * The button under a right answer (plan 15 decision 2): "Great job! Next" between rounds; after the
 * last round it names where it goes, once [next] is known.
 */
fun quizNextLabel(game: QuizGame, next: PathNext?, allLearned: Boolean): String = when {
    !game.finished -> "Great job! Next"
    next is PathNext.Reward -> if (allLearned) "See your sticker" else "Get your sticker"
    next == PathNext.Home -> "Back home"
    else -> "Next letter"
}

/**
 * "Which starts with …?". A wrong tap dims that picture and wiggles the right one; a right tap
 * turns it green with its word and a star, and shows [nextLabel]'s button, which nothing presses
 * but the child.
 */
@Composable
fun QuizScreen(
    game: QuizGame,
    stage: Stage,
    nextLabel: String,
    canContinue: Boolean,
    onClose: () -> Unit,
    onHear: () -> Unit,
    onPick: (Letter) -> Unit,
    onContinue: () -> Unit,
) {
    val colors = stage.colors()
    val target = game.target
    CappedWidth(HuroofiTokens.Sky, minHeight = QuizSpec.MinHeight) {
        Column(
            Modifier.fillMaxSize().safeDrawingPadding().padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundIconButton(
                    contentDescription = "Leave game",
                    onClick = onClose,
                    toddler = true,
                    size = QuizSpec.Close,
                    containerColor = HuroofiTokens.Card,
                    shadowColor = QuizSpec.EdgeColor,
                    shadowDepth = 4.dp,
                ) { LineIcon(CloseIcon, HuroofiTokens.Navy, size = 26.dp, strokeWidth = 2.8f) }
                RoundDots(game, Modifier.weight(1f))
                // Balances the close button so the dots sit in the middle.
                Spacer(Modifier.width(QuizSpec.Close))
            }
            Question(target, onHear)
            OptionGrid(game, colors, Modifier.weight(1f), onPick)
            Box(Modifier.fillMaxWidth().heightIn(min = QuizSpec.Foot), contentAlignment = Alignment.Center) {
                val wrong = game.wrongPick
                when {
                    game.solved -> PrimaryButton(nextLabel, onClick = onContinue, kind = ButtonKind.Success, enabled = canContinue, icon = ButtonIcons.Next)
                    wrong != null -> WrongPanel(wrong)
                    else -> IdleHint()
                }
            }
        }
    }
}

@Composable
private fun RoundDots(game: QuizGame, modifier: Modifier) {
    val now = (game.roundsDone + if (game.solved) 0 else 1).coerceAtMost(game.rounds)
    Row(
        modifier.clearAndSetSemantics { contentDescription = "Question $now of ${game.rounds}" },
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(game.rounds) { i ->
            val dot = quizDot(game, i)
            Box(
                Modifier
                    .height(QuizSpec.Dot)
                    .width(if (dot == QuizDot.NOW) QuizSpec.DotNow else QuizSpec.Dot)
                    .background(
                        when (dot) {
                            QuizDot.DONE -> HuroofiTokens.Success
                            QuizDot.NOW -> HuroofiTokens.Primary
                            QuizDot.TODO -> QuizSpec.DotTodo
                        },
                        CircleShape,
                    ),
            )
        }
    }
}

/** Leo and the question bubble; the whole bubble says the question again. */
@Composable
private fun Question(target: Letter, onHear: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Image(painterResource(R.drawable.pic_lion), contentDescription = null, modifier = Modifier.size(QuizSpec.Leo))
        val shape = RoundedCornerShape(QuizSpec.BubbleCorner)
        Row(
            Modifier
                .weight(1f)
                .heightIn(min = QuizSpec.Leo)
                .dropEdge(QuizSpec.EdgeColor, 4.dp, shape)
                .clip(shape)
                .background(HuroofiTokens.Card)
                .clickable(role = Role.Button, onClick = onHear)
                .clearAndSetSemantics { contentDescription = "Which starts with ${target.nameLatin}? Hear the question" }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier.size(QuizSpec.Sound).dropEdge(HuroofiTokens.SunShadow, 5.dp, CircleShape).background(HuroofiTokens.Sun, CircleShape),
                contentAlignment = Alignment.Center,
            ) { SoundIcon(HuroofiTokens.Navy, size = 28.dp) }
            // Heading breaks balance the lines ("Which starts / with"), never a lone word (plan 11 decision 4).
            Text(
                "Which starts with",
                Modifier.weight(1f),
                style = HuroofiText.buttonPrimary.copy(fontSize = 20.sp, lineHeight = 23.sp, lineBreak = LineBreak.Heading),
                color = HuroofiTokens.Navy,
            )
            Box(
                Modifier
                    .size(QuizSpec.Chip)
                    .background(QuizSpec.ChipFill, RoundedCornerShape(16.dp))
                    .border(QuizSpec.ChipRing, HuroofiTokens.Sun, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                CenteredLetter(target.letter, size = QuizSpec.CHIP_SP.sp, color = QuizSpec.ChipText)
            }
        }
    }
}

/** 2 × 2 for four pictures (Early reader), one column for three; tiles share the space left. */
@Composable
private fun OptionGrid(game: QuizGame, colors: StageColors, modifier: Modifier, onPick: (Letter) -> Unit) {
    val four = game.options.size == 4
    val rows = if (four) game.options.chunked(2) else game.options.map { listOf(it) }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        for (row in rows) {
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (option in row) {
                    OptionTile(option, game, colors, four, Modifier.weight(1f).fillMaxHeight()) { onPick(option) }
                }
            }
        }
    }
}

@Composable
private fun OptionTile(option: Letter, game: QuizGame, colors: StageColors, four: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val isTarget = game.isRight(option)
    val solvedHere = game.solved && isTarget
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
    val alpha = when {
        game.solved && !isTarget -> QuizSpec.FADED
        game.wrongPick == option -> QuizSpec.DIMMED
        else -> 1f
    }
    val shape = RoundedCornerShape(QuizSpec.TileCorner)
    Box(
        modifier
            .sizeIn(minHeight = QuizSpec.TileMin)
            .rotate(tilt.value)
            .scale(scale.value)
            .alpha(alpha)
            .semantics { contentDescription = option.meaningEn.replaceFirstChar { it.uppercase() } }
            // The colour change is the feedback; a square ripple would spill past the rounded tile.
            .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = onClick),
    ) {
        // The edge sits inside the tile's bounds: a faded tile draws in its own layer, which clips.
        Box(
            Modifier
                .matchParentSize()
                .padding(bottom = 6.dp)
                .dropEdge(if (solvedHere) HuroofiTokens.SuccessShadow else QuizSpec.EdgeColor, 6.dp, shape)
                .background(HuroofiTokens.Card, shape)
                .border(QuizSpec.TileBorder, if (solvedHere) HuroofiTokens.Success else HuroofiTokens.Card, shape)
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            val picture = if (four) QuizSpec.PictureFour else QuizSpec.Picture
            val image = @Composable { m: Modifier ->
                Image(letterPicture(option), contentDescription = null, modifier = m.sizeIn(maxWidth = picture, maxHeight = picture).fillMaxSize())
            }
            val word = @Composable {
                ArabicText(
                    highlightedWord(option.wordFirst, option.wordRest, colors.accent),
                    size = (if (four) QuizSpec.WORD_FOUR_SP else QuizSpec.WORD_SP).sp,
                    color = HuroofiTokens.Navy,
                )
            }
            when {
                !solvedHere -> image(Modifier)
                // Early reader: the word sits under the picture, which moves up to make room.
                four -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    image(Modifier.weight(1f, fill = false))
                    word()
                }
                else -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)) {
                    image(Modifier)
                    word()
                }
            }
        }
        if (solvedHere) {
            StarIcon(HuroofiTokens.Sun, Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-12).dp), size = QuizSpec.Star)
        }
    }
}

/** "Listen, then tap a picture" with a hand, on a flat white strip (the prototype's `.q-hint`). */
@Composable
private fun IdleHint() {
    Row(
        Modifier.fillMaxWidth().height(QuizSpec.Foot).background(HuroofiTokens.Card, RoundedCornerShape(22.dp)),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LineIcon(ButtonIcons.Hand, HuroofiTokens.Muted, size = 24.dp, strokeWidth = 2f)
        Text("Listen, then tap a picture", style = HuroofiText.body.copy(fontWeight = FontWeight.Bold), color = HuroofiTokens.Muted)
    }
}

@Composable
private fun WrongPanel(pick: Letter) {
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = QuizSpec.Foot)
            .background(QuizSpec.WrongBackground, RoundedCornerShape(22.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Almost! Try again", style = HuroofiText.buttonPrimary.copy(fontSize = 20.sp), color = QuizSpec.WrongText)
        // "Fox (ثعلب) starts with ث" as in Quiz.html: the Arabic word, not the English one, has the letter.
        val arabic = SpanStyle(fontFamily = NotoNaskhArabic, fontWeight = FontWeight.Bold, fontSize = QuizSpec.INLINE_SP.sp)
        Text(
            buildAnnotatedString {
                append("${pick.meaningEn.replaceFirstChar { it.uppercase() }} (")
                withStyle(arabic) { append(pick.wordAr) }
                append(") starts with ")
                withStyle(arabic) { append(pick.wordFirst) }
            },
            style = HuroofiText.body.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = QuizSpec.WrongText,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun QuizReaderPreview() {
    val l = LearnPreviewData.letters
    HuroofiTheme {
        QuizScreen(QuizGame(l[1], l, wrongPick = l[0], wrongTaps = 1), LearnPreviewData.stage, "Great job! Next", false, {}, {}, {}, {})
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun QuizPreschoolDonePreview() {
    val l = LearnPreviewData.letters
    HuroofiTheme {
        QuizScreen(QuizGame(l[1], l.take(3), roundsDone = 3, solved = true), LearnPreviewData.stage, "Next letter", true, {}, {}, {}, {})
    }
}
