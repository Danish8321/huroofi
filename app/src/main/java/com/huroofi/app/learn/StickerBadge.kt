package com.huroofi.app.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.data.content.Letter
import com.huroofi.app.data.content.Stage
import com.huroofi.app.ui.components.letterPicture
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.StageColors

/** Sizes of the stage sticker (`Reward.html` sticker card, plan 07 decision 5). */
object StickerSpec {
    val Badge = 116.dp
    const val RING_FRACTION = 5f / 116f
    const val PICTURE_FRACTION = 0.34f
    const val QUESTION_SP = 40f

    /** Discs and rings are decoration: an earned sticker shows its pictures, an empty one a "?". Names are 18 sp. */
    val textPairs = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "earned sticker name"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "empty sticker name"),
    )

    fun stagePairs(stage: StageColors) =
        listOf(ContrastPair(stage.accent, HuroofiTokens.Card, large = true, "empty sticker question mark"))
}

/** The sticker's name (decision 5). */
fun stickerName(stage: Stage): String = "${stage.name} sticker"

/** An earned sticker: stage pastel disc, dashed ring in the stage border colour, the stage's 4 pictures 2 × 2. */
@Composable
fun StickerBadge(stage: Stage, letters: List<Letter>, modifier: Modifier = Modifier, size: Dp = StickerSpec.Badge) {
    val colors = stage.colors()
    val picture = size * StickerSpec.PICTURE_FRACTION
    Box(
        modifier
            .size(size)
            .stickerDisc(colors.pastel, colors.border, dashed = true)
            .semantics { contentDescription = stickerName(stage) },
        contentAlignment = Alignment.Center,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            for (row in letters.sortedBy { it.index }.chunked(2)) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (letter in row) Image(letterPicture(letter), contentDescription = null, modifier = Modifier.size(picture))
                }
            }
        }
    }
}

/** A sticker not earned yet: a soft pastel outline with "?". No lock, no grey (decision 5). */
@Composable
fun EmptyStickerBadge(stage: Stage, modifier: Modifier = Modifier, size: Dp = StickerSpec.Badge) {
    val colors = stage.colors()
    Box(
        modifier
            .size(size)
            .stickerDisc(HuroofiTokens.Card, colors.pastel, dashed = false)
            .clearAndSetSemantics { contentDescription = "${stickerName(stage)}, not yet" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "?",
            Modifier.wrapContentHeight(unbounded = true),
            style = HuroofiText.screenTitle.copy(fontSize = StickerSpec.QUESTION_SP.sp),
            color = colors.accent,
        )
    }
}

/**
 * One Sticker book slot: badge and name when earned; the outline and the name, quieter, when not,
 * so an unearned slot never shows a blank band.
 */
@Composable
fun StickerSlot(stage: Stage, letters: List<Letter>, earned: Boolean, modifier: Modifier = Modifier) {
    // One TalkBack stop per slot; the caption repeats the badge's name (plan 08 task 6.2).
    val label = if (earned) stickerName(stage) else "${stickerName(stage)}, not yet"
    Column(
        modifier.clearAndSetSemantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (earned) StickerBadge(stage, letters) else EmptyStickerBadge(stage)
        Text(
            stickerName(stage),
            style = HuroofiText.body.copy(fontWeight = if (earned) FontWeight.ExtraBold else FontWeight.SemiBold),
            color = if (earned) HuroofiTokens.Navy else HuroofiTokens.Muted,
            textAlign = TextAlign.Center,
            minLines = 2,
        )
    }
}

private fun Modifier.stickerDisc(fill: Color, ring: Color, dashed: Boolean): Modifier = drawBehind {
    val width = size.minDimension * StickerSpec.RING_FRACTION
    val radius = size.minDimension / 2f
    drawCircle(fill, radius)
    val dash = if (dashed) PathEffect.dashPathEffect(floatArrayOf(width * 2.2f, width * 1.4f)) else null
    drawCircle(ring, radius - width / 2f, style = Stroke(width = width, pathEffect = dash))
}

@Preview(widthDp = 390, heightDp = 600)
@Composable
private fun StickerBadgesPreview() {
    HuroofiTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (row in LearnPreviewData.stages.chunked(3)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (stage in row) StickerSlot(stage, LearnPreviewData.letters, earned = stage.stage % 2 == 1)
                }
            }
        }
    }
}
