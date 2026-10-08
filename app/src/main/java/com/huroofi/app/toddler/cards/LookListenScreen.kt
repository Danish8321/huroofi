package com.huroofi.app.toddler.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.LocalAppContainer
import com.huroofi.app.data.content.Letter
import com.huroofi.app.learn.dropEdge
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.ToddlerHeader
import com.huroofi.app.toddler.ToddlerScaffold
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.ToddlerHomeButtonSpec
import com.huroofi.app.ui.components.calmMotion
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.components.parentLockPairs
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.highlightedWord
import kotlinx.coroutines.launch

/** Sizes and colours of Look & listen (`ToddlerCards.html`, plan 15 decision 6). The page is the letter's stage pastel. */
object CardsSpec {
    val Ring = 8.dp
    val Corner = 40.dp
    val Edge = 10.dp
    const val TILT = -1f
    val Chip = 76.dp
    val ChipCorner = 22.dp
    val ChipInset = 18.dp
    val SoundButton = 64.dp
    val SoundEdge = 5.dp
    val SoundInset = 22.dp
    val PictureMax = 230.dp
    val PictureMin = 120.dp
    val NavButton = 96.dp
    val NavEdge = 6.dp
    val PrevEdge = Color(0x1F000000)
    val Peek = 44.dp
    val PeekFace = Color(0xCCFFFFFF)
    const val LETTER_SP = 44f
    const val WORD_SP = 64f

    /**
     * Previous, next and "hear it". The letter chip, picture and sound button all answer a tap with
     * sound and a wiggle (plan 11 decision 2); they are one "hear it" choice. Home and lock are navigation.
     */
    const val CHOICES = 3
    val touchSizes = listOf(NavButton, Chip, SoundButton, PictureMin)
    val colors = listOf(PrevEdge, PeekFace, HuroofiTokens.Sun, HuroofiTokens.SunShadow)

    /** The card ring, edge and peek pictures are decoration. Stage-coloured text is in [stagePairs]. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "word"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sun, large = true, "sound icon"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Primary, large = true, "next chevron"),
    ) + ToddlerHomeButtonSpec.textPairs + parentLockPairs

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(stage.accent, stage.pastel, large = true, "letter chip"),
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "word first letter"),
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "previous chevron"),
    )
}

/** Look & listen: one letter card at a time, alphabet order, wrapping both ways (plan 05 decisions 8, 9). */
@Composable
fun LookListenScreen(onHome: () -> Unit, onRequestParentZone: () -> Unit) {
    val container = LocalAppContainer.current
    val content = container.content
    val letters = content.letters
    val scope = rememberCoroutineScope()
    val audio = remember { CardAudio(PromptPlayer(container.sound, scope)) }
    val deck = remember { CardDeck(letters.size) }
    val pager = rememberPagerState(initialPage = deck.startPage) { deck.pageCount }
    val speaking by audio.speaking.collectAsState()

    LaunchedEffect(pager.settledPage) {
        audio.onArrive(letters[deck.indexOf(pager.settledPage)])
    }
    DisposableEffect(Unit) { onDispose { audio.stop() } }

    val current = letters[deck.indexOf(pager.currentPage)]
    val stage = content.stageColors(current)
    val page by animateColorAsState(stage.pastel, label = "page")
    ToddlerScaffold(background = page) {
        ToddlerHeader(onHome = onHome, onRequestParentZone = onRequestParentZone)
        HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { index ->
            val letter = letters[deck.indexOf(index)]
            LetterCard(
                letter = letter,
                stage = content.stageColors(letter),
                speaking = index == pager.settledPage && speaking,
                onLetterTap = { audio.onLetterTap(letter) },
                onWordTap = { audio.onPictureTap(letter) },
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            RoundIconButton(
                contentDescription = "Previous picture",
                onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } },
                toddler = true,
                size = CardsSpec.NavButton,
                containerColor = HuroofiTokens.Card,
                shadowColor = CardsSpec.PrevEdge,
                shadowDepth = CardsSpec.NavEdge,
            ) { ChevronIcon(stage.accent, pointsRight = false) }
            Peek(
                listOf(letters[deck.indexOf(pager.currentPage - 1)], letters[deck.indexOf(pager.currentPage + 1)]),
            )
            RoundIconButton(
                contentDescription = "Next picture",
                onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
                toddler = true,
                size = CardsSpec.NavButton,
                containerColor = HuroofiTokens.Primary,
                shadowColor = HuroofiTokens.PrimaryShadow,
                shadowDepth = CardsSpec.NavEdge,
            ) { ChevronIcon(HuroofiTokens.Card, pointsRight = true) }
        }
    }
}

/** The pictures either side, small: where the arrows go. Decorative. */
@Composable
private fun Peek(neighbours: List<Letter>) {
    Row(Modifier.clearAndSetSemantics { }, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (letter in neighbours) {
            Image(
                letterPicture(letter),
                contentDescription = null,
                modifier = Modifier
                    .size(CardsSpec.Peek)
                    .background(CardsSpec.PeekFace, RoundedCornerShape(12.dp))
                    .padding(4.dp),
            )
        }
    }
}

/**
 * The big white card, tilted a hair: the letter chip in the corner, the picture and its word in the
 * middle, the sound button bottom-left. The picture shrinks on short screens instead of clipping.
 */
@Composable
private fun LetterCard(
    letter: Letter,
    stage: StageColors,
    speaking: Boolean,
    onLetterTap: () -> Unit,
    onWordTap: () -> Unit,
) {
    val spec = CardsSpec
    val shape = RoundedCornerShape(spec.Corner)
    val pictureWiggle = rememberWiggle()
    val hearWord = {
        pictureWiggle.play()
        onWordTap()
    }
    val meaning = letter.meaningEn.replaceFirstChar { it.uppercase() }
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            // Room for the tilt's corners and the edge below, inside the page.
            .padding(start = 4.dp, end = 4.dp, top = 6.dp, bottom = spec.Edge)
            .rotate(spec.TILT)
            .dropEdge(stage.accent, spec.Edge, shape)
            .background(HuroofiTokens.Card, shape)
            .border(spec.Ring, stage.border, shape),
    ) {
        // The chip and the sound button sit in opposite corners; the centred middle keeps clear of both.
        val corners = (spec.ChipInset + spec.Chip) * 2
        val word = (spec.WORD_SP * 1.3f).dp
        val picture = (maxHeight - corners - word).coerceIn(spec.PictureMin, spec.PictureMax)
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(picture)
                    .semantics { contentDescription = "$meaning: tap to hear the word" }
                    .clickable(interactionSource = null, indication = null, role = Role.Button, onClick = hearWord),
                contentAlignment = Alignment.Center,
            ) { Image(letterPicture(letter), contentDescription = null, modifier = Modifier.fillMaxSize().wiggle(pictureWiggle)) }
            ArabicText(highlightedWord(letter.wordFirst, letter.wordRest, stage.accent), size = spec.WORD_SP.sp, color = HuroofiTokens.Navy)
        }
        LetterChip(letter, stage, onLetterTap, Modifier.align(Alignment.TopEnd).padding(top = spec.ChipInset, end = spec.ChipInset))
        SoundButton(
            speaking,
            onClick = hearWord,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = spec.SoundInset, bottom = spec.SoundInset),
        )
    }
}

@Composable
private fun LetterChip(letter: Letter, stage: StageColors, onTap: () -> Unit, modifier: Modifier) {
    val wiggle = rememberWiggle()
    val shape = RoundedCornerShape(CardsSpec.ChipCorner)
    Box(
        modifier
            .wiggle(wiggle)
            .size(CardsSpec.Chip)
            .clip(shape)
            .background(stage.pastel)
            .semantics { contentDescription = "Letter ${letter.nameLatin}: tap to hear it" }
            .clickable(role = Role.Button) {
                wiggle.play()
                onTap()
            },
        contentAlignment = Alignment.Center,
    ) { CenteredLetter(letter.letter, size = CardsSpec.LETTER_SP.sp, color = stage.accent) }
}

/** Says the word again. Breathes while a clip plays, unless motion is calmed. */
@Composable
private fun SoundButton(speaking: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val pulse by rememberInfiniteTransition(label = "talk").animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "pulse",
    )
    val scale = if (speaking && !calmMotion()) pulse else 1f
    Box(
        modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .size(CardsSpec.SoundButton)
            .dropEdge(HuroofiTokens.SunShadow, CardsSpec.SoundEdge, CircleShape)
            .clip(CircleShape)
            .background(HuroofiTokens.Sun)
            .semantics { contentDescription = "Hear the word" }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { SoundIcon(HuroofiTokens.Navy, size = 30.dp) }
}
