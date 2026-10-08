package com.huroofi.app.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.huroofi.app.data.content.Stage
import com.huroofi.app.ui.components.stagePicture
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.StageColors

/**
 * Sizes of the stage sticker (prototype `stickerBadge()`, plan 15 decision 6). Every part scales
 * with the badge, so Home's 70 dp prize, the book's 112 dp and the reward's hero match.
 */
object StickerSpec {
    val Badge = 112.dp
    const val RING_FRACTION = 6f / 112f
    const val EDGE_FRACTION = 6f / 112f
    const val DASH_FRACTION = 4f / 112f
    const val PICTURE_FRACTION = 0.76f
    /** The "?" at [Badge] size; smaller badges scale it down with the disc. */
    const val QUESTION_SP = 34f

    /** Names are 18 sp. */
    val textPairs = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = false, "earned sticker name"),
        ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = false, "empty sticker name"),
    )

    fun stagePairs(stage: StageColors) =
        listOf(ContrastPair(stage.accent, stage.pastel, large = true, "empty sticker question mark"))
}

/** The sticker's name, for TalkBack and the reward. */
fun stickerName(stage: Stage): String = "${stage.name} sticker"

/** An earned sticker: the stage picture on a white disc, a solid ring in the stage colour, a darker edge below. */
@Composable
fun StickerBadge(stage: Stage, modifier: Modifier = Modifier, size: Dp = StickerSpec.Badge) {
    val colors = stage.colors()
    Box(
        modifier
            .size(size)
            .drawBehind {
                val ring = this.size.minDimension * StickerSpec.RING_FRACTION
                val edge = this.size.minDimension * StickerSpec.EDGE_FRACTION
                val radius = this.size.minDimension / 2f
                drawCircle(colors.accent, radius, center.plus(Offset(0f, edge)))
                drawCircle(HuroofiTokens.Card, radius)
                drawCircle(colors.border, radius - ring / 2f, style = Stroke(ring))
            }
            .semantics { contentDescription = stickerName(stage) },
        contentAlignment = Alignment.Center,
    ) {
        Image(stagePicture(stage), contentDescription = null, modifier = Modifier.size(size * StickerSpec.PICTURE_FRACTION))
    }
}

/** A sticker not earned yet: pastel disc, dashed ring in the stage colour, "?". No lock, no grey. */
@Composable
fun EmptyStickerBadge(stage: Stage, modifier: Modifier = Modifier, size: Dp = StickerSpec.Badge) {
    val colors = stage.colors()
    // The "?" scales with the disc, not with the font setting, so it always fits.
    val question = with(LocalDensity.current) { (size * (StickerSpec.QUESTION_SP / StickerSpec.Badge.value)).toSp() }
    Box(
        modifier
            .size(size)
            .drawBehind {
                val dash = this.size.minDimension * StickerSpec.DASH_FRACTION
                val radius = this.size.minDimension / 2f
                drawCircle(colors.pastel, radius)
                drawCircle(
                    colors.border,
                    radius - dash / 2f,
                    style = Stroke(dash, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash * 2.5f, dash * 1.5f))),
                )
            }
            .clearAndSetSemantics { contentDescription = "${stickerName(stage)}, not earned yet" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "?",
            Modifier.wrapContentHeight(unbounded = true),
            style = HuroofiText.screenTitle.copy(fontSize = question, fontWeight = FontWeight.ExtraBold),
            color = colors.accent,
        )
    }
}

/** One Sticker book slot: the badge and the stage name; quieter when not earned. */
@Composable
fun StickerSlot(stage: Stage, earned: Boolean, modifier: Modifier = Modifier) {
    // One TalkBack stop per slot (plan 08 task 6.2).
    val label = if (earned) "${stickerName(stage)}. Tap to hear its letters" else "${stickerName(stage)}, not earned yet"
    Column(
        modifier.clearAndSetSemantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (earned) StickerBadge(stage, Modifier.padding(bottom = StickerSpec.Badge * StickerSpec.EDGE_FRACTION)) else EmptyStickerBadge(stage)
        Text(
            stage.name,
            style = HuroofiText.body.copy(fontWeight = if (earned) FontWeight.ExtraBold else FontWeight.Bold),
            color = if (earned) HuroofiTokens.Navy else HuroofiTokens.Muted,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(widthDp = 390, heightDp = 600)
@Composable
private fun StickerBadgesPreview() {
    HuroofiTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (row in LearnPreviewData.stages.chunked(3)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (stage in row) Box(Modifier.size(120.dp, 170.dp)) { StickerSlot(stage, earned = stage.stage % 2 == 1, Modifier.fillMaxSize()) }
                }
            }
        }
    }
}
