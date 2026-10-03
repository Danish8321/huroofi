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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.huroofi.app.toddler.PromptPlayer
import com.huroofi.app.toddler.ToddlerHeader
import com.huroofi.app.toddler.ToddlerPhrases
import com.huroofi.app.toddler.ToddlerScaffold
import com.huroofi.app.ui.components.ChevronIcon
import com.huroofi.app.ui.components.RoundIconButton
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.highlightedWord
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Sizes and colours of Where's the…? (`ToddlerFind.html`, phone layout). */
object FindSpec {
    val SunButton = 84.dp
    val NextButton = 96.dp
    val TileHeight = 230.dp
    val TileCorner = 40.dp
    val TileBorder = 6.dp
    val TileShadow = 8.dp
    val PictureSize = 140.dp
    val HeaderStar = 30.dp
    const val PROMPT_SP = 42f
    const val WORD_SP = 50f
    val IdleShadow = Color(0xFFCFE2F7)
    val SolvedBackground = Color(0xFFDFF5D5)
    val SolvedBorder = Color(0xFF3BAA5C)
    val SolvedShadow = Color(0xFF9FD58A)
    val PartyPink = Color(0xFFFF6FA5)
    val PartyBlue = Color(0xFF3B8CF0)

    /** Two pictures; the sun button repeats the question and is not a choice. */
    const val CHOICES = 2
    val touchSizes = listOf(SunButton, NextButton, TileHeight)
    val colors = listOf(IdleShadow, SolvedBackground, SolvedBorder, SolvedShadow, PartyPink, PartyBlue)
}

/** Where's the…?: two pictures, find the one asked for. Endless sets of three (plan 05 decisions 5, 13). */
@Composable
fun FindScreen(onHome: () -> Unit, onRequestParentZone: () -> Unit) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val game = remember { FindGame(container.content.letters, Random.Default) }
    val audio = remember { FindAudio(PromptPlayer(container.sound, scope), Random.Default) }
    var set by remember { mutableStateOf(game.start()) }
    val round = set.round
    val colors = LocalHuroofiColors.current

    LaunchedEffect(round.target) { audio.ask(round.target) }
    DisposableEffect(Unit) { onDispose { audio.stop() } }

    ToddlerScaffold {
        ToddlerHeader(onHome = onHome, onRequestParentZone = onRequestParentZone) {
            Row(
                Modifier.semantics { contentDescription = "${set.stars} of ${FindGame.ROUNDS_PER_SET} stars" },
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                repeat(FindGame.ROUNDS_PER_SET) { i ->
                    StarIcon(fill = if (i < set.stars) colors.sun else colors.card, size = FindSpec.HeaderStar)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoundIconButton(
                contentDescription = ToddlerPhrases.whereIsEn(round.target),
                onClick = { audio.ask(round.target) },
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
                onClick = {
                    val (after, result) = game.tap(set, option)
                    set = after
                    audio.onTap(result, after)
                },
                modifier = Modifier.weight(1f).heightIn(max = FindSpec.TileHeight),
            )
        }
        Box(Modifier.fillMaxWidth().height(FindSpec.NextButton + 6.dp), contentAlignment = Alignment.Center) {
            if (round.solved) {
                val pop = remember(round) { Animatable(0f) }
                LaunchedEffect(pop) { pop.animateTo(1f, tween(400)) }
                RoundIconButton(
                    contentDescription = "Next",
                    onClick = { set = game.next(set) },
                    toddler = true,
                    size = FindSpec.NextButton,
                    containerColor = colors.success,
                    shadowColor = colors.successShadow,
                    modifier = Modifier.graphicsLayer {
                        scaleX = pop.value
                        scaleY = pop.value
                        alpha = pop.value
                    },
                ) { ChevronIcon(colors.card, pointsRight = true, size = 46.dp) }
            }
        }
    }
}

@Composable
private fun FindTile(letter: Letter, round: FindRound, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val content = LocalAppContainer.current.content
    val colors = LocalHuroofiColors.current
    val isTarget = letter == round.target
    val solvedHere = round.solved && isTarget
    val shape = RoundedCornerShape(FindSpec.TileCorner)
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
                .padding(top = FindSpec.TileShadow)
                .background(shadow, shape),
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = FindSpec.TileShadow)
                .background(background, shape)
                .border(FindSpec.TileBorder, border, shape),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                letterPicture(letter),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .size(FindSpec.PictureSize)
                    .graphicsLayer {
                        rotationZ = tilt.value
                        scaleX = scale.value
                        scaleY = scale.value
                    },
            )
            ArabicText(
                highlightedWord(letter.wordFirst, letter.wordRest, content.stageColors(letter).accent),
                size = FindSpec.WORD_SP.sp,
                color = colors.navy,
            )
        }
        if (solvedHere) {
            PartyStar(colors.sun, 64.dp, 0, Modifier.align(Alignment.TopStart).padding(start = 18.dp, top = 14.dp))
            PartyStar(FindSpec.PartyPink, 48.dp, 150, Modifier.align(Alignment.TopEnd).padding(end = 22.dp, top = 28.dp))
            PartyStar(FindSpec.PartyBlue, 54.dp, 300, Modifier.align(Alignment.BottomEnd).padding(end = 34.dp, bottom = 26.dp))
        }
    }
}

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
