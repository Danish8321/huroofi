package com.huroofi.app.toddler.cards

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.data.content.Letter
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.ToddlerHeader
import com.huroofi.app.toddler.ToddlerScaffold
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.ToddlerHomeButtonSpec
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.components.parentLockPairs
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.highlightedWord
import kotlinx.coroutines.launch

/** Sizes and colours of Look & listen (`ToddlerCards.html`). */
object CardsSpec {
    val Ring = 8.dp
    val Corner = 40.dp
    val InnerCorner = 34.dp
    val Shadow = 8.dp
    val ShadowColor = Color(0xFFCFE2F7)
    val LetterBoxMinWidth = 150.dp
    val LetterBoxHeight = 136.dp
    val LetterBoxCorner = 36.dp
    val PictureButtonMax = 226.dp
    val PictureButtonMin = 140.dp
    val NavButton = 96.dp
    val NavShadow = 6.dp
    const val LETTER_SP = 110f
    const val WORD_SP = 88f

    /**
     * Previous, next and "hear it". The letter, picture and word all answer a tap with sound and a
     * wiggle (plan 11 decision 2); they are one "hear it" choice. Home and lock are navigation.
     */
    const val CHOICES = 3
    val touchSizes = listOf(NavButton, PictureButtonMin)
    val colors = listOf(ShadowColor)

    /** The card ring is decoration; Arabic is bold and at least 88 sp, so large. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "word"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "previous chevron"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Primary, large = true, "next chevron"),
    ) + ToddlerHomeButtonSpec.textPairs + parentLockPairs

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(HuroofiTokens.Navy, stage.pastel, large = true, "big letter"),
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "word first letter"),
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "talking bars"),
    )
}

/** Look & listen: one letter card at a time, alphabet order, wrapping both ways (plan 05 decisions 8, 9). */
@Composable
fun LookListenScreen(onHome: () -> Unit, onRequestParentZone: () -> Unit) {
    val container = LocalAppContainer.current
    val letters = container.content.letters
    val scope = rememberCoroutineScope()
    val audio = remember { CardAudio(PromptPlayer(container.sound, scope)) }
    val deck = remember { CardDeck(letters.size) }
    val pager = rememberPagerState(initialPage = deck.startPage) { deck.pageCount }
    val speaking by audio.speaking.collectAsState()

    LaunchedEffect(pager.settledPage) {
        audio.onArrive(letters[deck.indexOf(pager.settledPage)])
    }
    DisposableEffect(Unit) { onDispose { audio.stop() } }

    ToddlerScaffold {
        ToddlerHeader(onHome = onHome, onRequestParentZone = onRequestParentZone)
        HorizontalPager(state = pager, modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) { page ->
            val letter = letters[deck.indexOf(page)]
            val current = page == pager.settledPage
            LetterCard(
                letter = letter,
                stage = container.content.stageColors(letter),
                speaking = current && speaking,
                onLetterTap = { audio.onLetterTap(letter) },
                onWordTap = { audio.onPictureTap(letter) },
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val colors = LocalHuroofiColors.current
            RoundIconButton(
                contentDescription = "Previous picture",
                onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } },
                toddler = true,
                size = CardsSpec.NavButton,
                containerColor = colors.card,
                shadowColor = CardsSpec.ShadowColor,
            ) { ChevronIcon(colors.navy, pointsRight = false) }
            RoundIconButton(
                contentDescription = "Next picture",
                onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
                toddler = true,
                size = CardsSpec.NavButton,
                containerColor = colors.primary,
                shadowColor = colors.primaryShadow,
            ) { ChevronIcon(colors.card, pointsRight = true) }
        }
    }
}

@Composable
private fun LetterCard(
    letter: Letter,
    stage: StageColors,
    speaking: Boolean,
    onLetterTap: () -> Unit,
    onWordTap: () -> Unit,
) {
    val spec = CardsSpec
    val colors = LocalHuroofiColors.current
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        // Everything but the picture button, so the picture shrinks on short screens instead of clipping.
        val fixed = spec.Ring * 2 + spec.Shadow + 38.dp + spec.LetterBoxHeight + 14.dp + (spec.WORD_SP * 1.3f).dp
        val picture = (maxHeight - fixed).coerceIn(spec.PictureButtonMin, spec.PictureButtonMax)
        Box {
            Box(
                Modifier
                    .matchParentSize()
                    .padding(top = spec.Shadow)
                    .background(spec.ShadowColor, RoundedCornerShape(spec.Corner)),
            )
            Column(
                Modifier
                    .padding(bottom = spec.Shadow)
                    .fillMaxWidth()
                    .background(stage.border, RoundedCornerShape(spec.Corner))
                    .padding(spec.Ring)
                    .background(colors.card, RoundedCornerShape(spec.InnerCorner))
                    .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val letterWiggle = rememberWiggle()
                    Box(
                        Modifier
                            .wiggle(letterWiggle)
                            .clip(RoundedCornerShape(spec.LetterBoxCorner))
                            .semantics { contentDescription = "${letter.nameLatin}: tap to hear the letter" }
                            .clickable(role = Role.Button) {
                                letterWiggle.play()
                                onLetterTap()
                            }
                            .widthIn(min = spec.LetterBoxMinWidth)
                            .height(spec.LetterBoxHeight)
                            .background(stage.pastel, RoundedCornerShape(spec.LetterBoxCorner))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) { CenteredLetter(letter.letter, size = spec.LETTER_SP.sp, color = colors.navy) }
                    if (speaking) TalkingBars(stage.accent, Modifier.align(Alignment.TopStart).padding(top = 6.dp))
                }
                TapPicture(letter, stage, picture, onWordTap)
                val wordWiggle = rememberWiggle()
                ArabicText(
                    highlightedWord(letter.wordFirst, letter.wordRest, stage.accent),
                    modifier = Modifier
                        .wiggle(wordWiggle)
                        .semantics { contentDescription = "${letter.meaningEn.replaceFirstChar { it.uppercase() }}: tap to hear the word" }
                        // No ripple box around a word; the wiggle is the answer.
                        .clickable(interactionSource = null, indication = null, role = Role.Button) {
                            wordWiggle.play()
                            onWordTap()
                        }
                        .padding(horizontal = 16.dp),
                    size = spec.WORD_SP.sp,
                    color = colors.navy,
                )
            }
        }
    }
}

@Composable
private fun TapPicture(letter: Letter, stage: StageColors, size: Dp, onTap: () -> Unit) {
    val wiggle = rememberWiggle()
    Box(
        Modifier
            .padding(top = 10.dp)
            .size(size)
            .background(stage.pastel, CircleShape)
            .semantics { contentDescription = "${letter.meaningEn.replaceFirstChar { it.uppercase() }}: tap to hear the word" }
            .clickable(role = Role.Button) {
                wiggle.play()
                onTap()
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(letterPicture(letter), contentDescription = null, modifier = Modifier.size(size * (196f / 226f)).wiggle(wiggle))
    }
}

/** Three bars bouncing while a clip plays. Decorative. */
@Composable
private fun TalkingBars(color: Color, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "talk")
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until 3) {
            val s by transition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse, StartOffset(i * 200)),
                label = "bar$i",
            )
            Box(
                Modifier
                    .size(width = 6.dp, height = 24.dp)
                    .graphicsLayer { scaleY = s }
                    .background(color, RoundedCornerShape(3.dp)),
            )
        }
    }
}
