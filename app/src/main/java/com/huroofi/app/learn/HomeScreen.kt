package com.huroofi.app.learn

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.parent.LetterState
import com.huroofi.app.ui.components.ButtonIcons
import com.huroofi.app.ui.components.ButtonKind
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.components.calmMotion
import com.huroofi.app.ui.components.contrastPairs
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.components.rememberWiggle
import com.huroofi.app.ui.components.wiggle
import com.huroofi.app.ui.theme.CenteredLetter
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.withArabic

/**
 * Sizes and colours of Home (`Main.html`, plan 15 decision 6): the stage's letters along a winding
 * path, today's letter big on it, the stage sticker waiting at the end, one button.
 */
object HomeSpec {
    val Avatar = 52.dp
    val CardCorner = 36.dp
    val CardRing = 5.dp
    val Node = 68.dp
    val NodeRing = 3.dp
    val NodeEdge = 5.dp
    val Today = 112.dp
    val TodayEdge = 7.dp
    val TodayHalo = 16.dp
    val Picture = 88.dp
    val Prize = 70.dp
    val PrizeInset = 18.dp
    /** Height of the "Today" chip: 16 sp text plus its padding. */
    val Chip = 28.dp
    val PathWidth = 30.dp
    val DotWidth = 4.dp
    const val PULSE_MS = 2_400
    const val NODE_SP = 36f
    const val TODAY_SP = 58f
    const val GREETING_SP = 24f
    val textSizes = listOf(NODE_SP, TODAY_SP, GREETING_SP)

    /** The prototype's path box; the path and the four spots stretch with the card. */
    const val BOX_W = 340f
    const val BOX_H = 500f
    const val PATH = "M90 430 C 40 380, 120 340, 200 330 S 320 250, 250 200 S 60 150, 110 92"
    /** Where the stage's letters sit on the path, first letter at the bottom. */
    val SLOTS = listOf(90f to 430f, 200f to 330f, 250f to 200f, 110f to 92f)

    val touchSizes = listOf(Today, Picture, HuroofiDimens.PrimaryButtonHeight, NavSpec.Item)
    val colors = listOf(HuroofiTokens.Sky, HuroofiTokens.Card, HuroofiTokens.Navy, HuroofiTokens.Muted, HuroofiTokens.Primary) + NavSpec.colors

    /** 16 sp ExtraBold "Today" is under the 18.66 sp bold line, so normal text. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sky, large = true, "greeting"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Sky, large = false, "greeting subtitle"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Primary, large = true, "today's letter"),
        ContrastPair(HuroofiTokens.PrimaryShadow, HuroofiTokens.Card, large = false, "Today chip"),
        ContrastPair(HuroofiTokens.Card, HuroofiTokens.Success, large = true, "learned letter"),
    ) + ButtonKind.Primary.contrastPairs() + NavSpec.textPairs

    fun stagePairs(stage: StageColors) = listOf(
        ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "coming-up letter"),
    )

    /** Centre of spot [k] in a card [w] × [h]. */
    fun slot(k: Int, w: Dp, h: Dp): DpOffset = SLOTS[k].let { (x, y) -> DpOffset(w * (x / BOX_W), h * (y / BOX_H)) }

    /** Today's picture sits beside its spot, on the side with room. */
    fun pictureOnLeft(k: Int): Boolean = SLOTS[k].first > BOX_W / 2
}

/** "Let's learn", "Practise" on a review day, "Trace" in trace-only mode; the letter follows. */
fun homeButtonLabel(review: Boolean, traceOnly: Boolean): String = when {
    traceOnly -> "Trace"
    review -> "Practise"
    else -> "Let's learn"
}

/** "Stage 1 · Sunny Meadow · 1 of 4". */
fun homeSubtitle(stage: Stage, chips: List<StageChip>): String =
    "Stage ${stage.stage} · ${stage.name} · ${chips.count { it.state == LetterState.LEARNED }} of ${chips.size}"

@Composable
fun HomeScreen(
    today: Letter,
    stage: Stage,
    chips: List<StageChip>,
    review: Boolean,
    traceOnly: Boolean,
    stickerEarned: Boolean,
    onGo: () -> Unit,
    onHear: () -> Unit,
    navTabs: List<NavTab>,
    onTab: (NavTab) -> Unit,
) {
    CappedWidth(HuroofiTokens.Sky) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column(
                Modifier.weight(1f).padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Greeting(review, homeSubtitle(stage, chips))
                PathCard(today, stage, chips, stickerEarned, onHear, Modifier.weight(1f))
                PrimaryButton(
                    withArabic(homeButtonLabel(review, traceOnly) + "  ", today.letter),
                    onClick = onGo,
                    icon = ButtonIcons.Next,
                )
            }
            LearnNav(NavTab.HOME, navTabs, onTab)
        }
    }
}

@Composable
private fun Greeting(review: Boolean, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Image(
            painterResource(R.drawable.pic_lion),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(HomeSpec.Avatar)
                .clip(CircleShape)
                .background(HuroofiTokens.Card)
                .border(3.dp, HuroofiTokens.Sun, CircleShape)
                .graphicsLayer {
                    scaleX = 1.15f
                    scaleY = 1.15f
                },
        )
        Column {
            Text(
                if (review) "Let's practise a letter!" else "Hi there!",
                style = HuroofiText.screenTitle.copy(fontSize = HomeSpec.GREETING_SP.sp, lineHeight = 26.sp),
                color = HuroofiTokens.Navy,
            )
            Text(subtitle, style = HuroofiText.body.copy(fontWeight = FontWeight.SemiBold), color = HuroofiTokens.Muted)
        }
    }
}

@Composable
private fun PathCard(today: Letter, stage: Stage, chips: List<StageChip>, stickerEarned: Boolean, onHear: () -> Unit, modifier: Modifier) {
    val colors = stage.colors()
    val shape = RoundedCornerShape(HomeSpec.CardCorner)
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.pastel)
            .border(HomeSpec.CardRing, colors.border, shape)
            .padding(HomeSpec.CardRing),
    ) {
        val w = maxWidth
        val h = maxHeight
        WindingPath(colors.border, Modifier.fillMaxSize())
        val letterWiggle = rememberWiggle()
        val pictureWiggle = rememberWiggle()
        chips.forEachIndexed { k, chip ->
            val at = HomeSpec.slot(k, w, h)
            if (chip.letter.index == today.index) {
                TodayNode(
                    today,
                    Modifier
                        .offset(at.x - HomeSpec.Today / 2, at.y - HomeSpec.Today / 2)
                        .wiggle(letterWiggle)
                        .clickable(interactionSource = null, indication = null, role = Role.Button) {
                            letterWiggle.play()
                            onHear()
                        }
                        .clearAndSetSemantics { contentDescription = "Today's letter ${today.nameLatin}. Tap to hear" },
                )
                Text(
                    "Today",
                    Modifier
                        // Under the letter, but never past the card's bottom edge (first letter of a stage).
                        .offset(at.x - 30.dp, minOf(at.y + 60.dp, h - HomeSpec.Chip - 6.dp))
                        .background(HuroofiTokens.Card, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .clearAndSetSemantics {},
                    style = HuroofiText.caption.copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold),
                    color = HuroofiTokens.PrimaryShadow,
                )
                val left = if (HomeSpec.pictureOnLeft(k)) at.x - 150.dp else at.x + 62.dp
                Image(
                    letterPicture(today),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(left, at.y - 44.dp)
                        .size(HomeSpec.Picture)
                        .wiggle(pictureWiggle)
                        .clickable(interactionSource = null, indication = null, role = Role.Button) {
                            pictureWiggle.play()
                            onHear()
                        }
                        .clearAndSetSemantics { contentDescription = "${today.meaningEn.replaceFirstChar { it.uppercase() }}. Tap to hear" },
                )
            } else {
                PathNode(chip, colors, Modifier.offset(at.x - HomeSpec.Node / 2, at.y - HomeSpec.Node / 2))
            }
        }
        val prize = Modifier.align(Alignment.TopEnd).padding(HomeSpec.PrizeInset).size(HomeSpec.Prize)
        if (stickerEarned) {
            StickerBadge(stage, prize.clearAndSetSemantics { contentDescription = "${stickerName(stage)}, earned" }, HomeSpec.Prize)
        } else {
            EmptyStickerBadge(
                stage,
                prize.clearAndSetSemantics { contentDescription = "${stickerName(stage)}, waiting at the end of this stage" },
                HomeSpec.Prize,
            )
        }
    }
}

/** The prototype's path: a wide white road with a faint dotted line down its middle. Decoration. */
@Composable
private fun WindingPath(dots: Color, modifier: Modifier) {
    val path = remember { PathParser().parsePathString(HomeSpec.PATH).toPath() }
    Canvas(modifier.clearAndSetSemantics {}) {
        val sx = size.width / HomeSpec.BOX_W
        val sy = size.height / HomeSpec.BOX_H
        // Stretched like the prototype's SVG; strokes are drawn in box units, so divide the scale back out.
        val unit = (sx + sy) / 2f
        scale(sx, sy, pivot = androidx.compose.ui.geometry.Offset.Zero) {
            drawPath(path, Color.White, style = Stroke(HomeSpec.PathWidth.toPx() / unit, cap = StrokeCap.Round))
            drawPath(
                path,
                dots.copy(alpha = 0.45f),
                style = Stroke(
                    HomeSpec.DotWidth.toPx() / unit,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx() / unit, 14.dp.toPx() / unit)),
                ),
            )
        }
    }
}

/** Today's letter: big, blue, with a halo that keeps pulsing outwards (none with calm motion). */
@Composable
private fun TodayNode(letter: Letter, modifier: Modifier) {
    val calm = calmMotion()
    val pulse by rememberInfiniteTransition(label = "today pulse").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(HomeSpec.PULSE_MS / 2, easing = LinearEasing), RepeatMode.Reverse),
        label = "halo",
    )
    Box(
        modifier
            .size(HomeSpec.Today)
            .drawBehind {
                val r = size.minDimension / 2f
                if (!calm) drawCircle(HuroofiTokens.Primary.copy(alpha = 0.35f * (1f - pulse)), r + HomeSpec.TodayHalo.toPx() * pulse)
                drawCircle(HuroofiTokens.PrimaryShadow, r, center.copy(y = center.y + HomeSpec.TodayEdge.toPx()))
                drawCircle(HuroofiTokens.Primary, r)
            },
        contentAlignment = Alignment.Center,
    ) {
        CenteredLetter(letter.letter, size = HomeSpec.TODAY_SP.sp, color = HuroofiTokens.Card, style = HuroofiText.arabicChip)
    }
}

/** Another letter of the stage: green once learned, white with a stage ring while still to come. */
@Composable
private fun PathNode(chip: StageChip, colors: StageColors, modifier: Modifier) {
    val learned = chip.state == LetterState.LEARNED
    Box(
        modifier
            .size(HomeSpec.Node)
            .then(if (learned) Modifier.dropEdgeCircle(HuroofiTokens.SuccessShadow, HomeSpec.NodeEdge) else Modifier)
            .background(if (learned) HuroofiTokens.Success else HuroofiTokens.Card, CircleShape)
            .then(if (learned) Modifier else Modifier.border(HomeSpec.NodeRing, colors.border, CircleShape))
            .clearAndSetSemantics { contentDescription = "${chip.letter.nameLatin}, ${if (learned) "learned" else "coming up"}" },
        contentAlignment = Alignment.Center,
    ) {
        CenteredLetter(
            chip.letter.letter,
            size = HomeSpec.NODE_SP.sp,
            color = if (learned) HuroofiTokens.Card else colors.accent,
            style = HuroofiText.arabicChip,
        )
    }
}

private fun Modifier.dropEdgeCircle(color: Color, depth: Dp): Modifier = dropEdge(color, depth, CircleShape)

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun HomePreview() {
    HuroofiTheme {
        val letters = LearnPreviewData.letters
        HomeScreen(
            today = letters[1],
            stage = LearnPreviewData.stage,
            chips = letters.mapIndexed { i, l ->
                StageChip(l, if (i == 0) LetterState.LEARNED else if (i == 1) LetterState.LEARNING else LetterState.TO_GO)
            },
            review = false,
            traceOnly = false,
            stickerEarned = false,
            onGo = {},
            onHear = {},
            navTabs = NavTab.entries,
            onTab = {},
        )
    }
}
