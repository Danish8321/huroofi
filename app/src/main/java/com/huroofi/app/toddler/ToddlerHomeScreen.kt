package com.huroofi.app.toddler

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.ParentLock
import com.huroofi.app.ui.components.ScaleDownToFit
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.LocalHuroofiColors

const val PARENT_LOCK_DESCRIPTION = "Grown-ups: press and hold to open settings"

/**
 * Toddler Home (`ToddlerHome.html`): Leo asks "ماذا نلعب؟", three big activity tiles, a small lock.
 * Stateless; sound and navigation are wired by [ToddlerHomeRoute].
 */
@Composable
fun ToddlerHomeScreen(
    onReplayPrompt: () -> Unit,
    onOpenActivity: (ToddlerActivity) -> Unit,
    onRequestParentZone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spec = ToddlerHomeSpec
    ToddlerScaffold(modifier) {
        Box(Modifier.fillMaxWidth().height(spec.HeaderHeight)) {
            Row(Modifier.align(Alignment.BottomStart), verticalAlignment = Alignment.Bottom) {
                Image(
                    painterResource(R.drawable.pic_lion),
                    contentDescription = null,
                    modifier = Modifier.size(spec.MascotSize),
                )
                PromptBubble(onReplayPrompt, Modifier.padding(start = 8.dp).weight(1f))
            }
            ParentLock(
                onRequestParentZone = onRequestParentZone,
                contentDescription = PARENT_LOCK_DESCRIPTION,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
        for (activity in ToddlerActivity.entries) {
            ActivityTile(
                activity,
                onClick = { onOpenActivity(activity) },
                modifier = Modifier.weight(1f).heightIn(max = spec.TileHeight),
            )
        }
    }
}

/** Sky background, prototype padding, and a centred column ([CappedWidth]). */
@Composable
fun ToddlerScaffold(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    CappedWidth(LocalHuroofiColors.current.sky, modifier) {
        Column(
            Modifier
                .fillMaxHeight()
                .safeDrawingPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(ToddlerHomeSpec.Gap),
            content = content,
        )
    }
}

@Composable
private fun PromptBubble(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalHuroofiColors.current
    val talk by rememberInfiniteTransition(label = "talk").animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "talkScale",
    )
    Row(
        modifier = modifier
            .height(ToddlerHomeSpec.BubbleHeight)
            .background(colors.card, RoundedCornerShape(30.dp, 30.dp, 30.dp, 6.dp))
            .semantics { contentDescription = "Leo says: What shall we play? Tap to hear it again" }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArabicText("ماذا نلعب؟", size = 34.sp, color = colors.navy)
        SoundIcon(color = colors.primary, size = 28.dp, modifier = Modifier.scale(talk))
    }
}

@Composable
private fun ActivityTile(activity: ToddlerActivity, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val spec = ToddlerHomeSpec
    val tile = activity.tile
    val shape = RoundedCornerShape(spec.TileCorner)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    Box(
        modifier
            .fillMaxWidth()
            .semantics { contentDescription = activity.contentDescription }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick),
    ) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = spec.TileShadow)
                .background(tile.shadow, shape),
        )
        Row(
            Modifier
                .matchParentSize()
                .padding(bottom = spec.TileShadow)
                .offset { IntOffset(0, if (pressed) spec.TileShadow.roundToPx() else 0) }
                .background(tile.background, shape)
                .border(spec.TileBorder, tile.border, shape)
                .padding(end = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Art and label keep their designed size, shrinking only in a short window (tablet landscape).
            ScaleDownToFit(Modifier.weight(1f)) {
                when (activity) {
                    ToddlerActivity.CARDS -> CardsArt()
                    ToddlerActivity.FIND -> FindArt()
                    ToddlerActivity.PAINT -> PaintArt()
                }
            }
            ScaleDownToFit(Modifier.width(spec.LabelColumnWidth)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText(activity.arabicLabel, size = 40.sp, color = tile.text)
                    Text(
                        activity.englishLabel,
                        style = HuroofiText.caption.copy(fontWeight = FontWeight.Bold),
                        color = tile.text,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun CardsArt() {
    Row {
        MiniCard(R.drawable.pic_lion, Modifier.offset(x = 14.dp).rotate(-8f))
        MiniCard(R.drawable.pic_duck, Modifier.offset(x = (-14).dp).rotate(7f))
    }
}

@Composable
private fun MiniCard(picture: Int, modifier: Modifier) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier
            .size(width = 112.dp, height = 140.dp)
            .background(LocalHuroofiColors.current.card, shape)
            .border(4.dp, ToddlerHomeSpec.MiniCardBorder, shape),
        contentAlignment = Alignment.Center,
    ) { Image(painterResource(picture), contentDescription = null, modifier = Modifier.size(92.dp)) }
}

@Composable
private fun FindArt() {
    // The padding covers the magnifier hanging 24 dp below the apple, so ScaleDownToFit counts it.
    Row(Modifier.padding(vertical = 24.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.pic_apple), contentDescription = null, modifier = Modifier.size(132.dp))
        Canvas(Modifier.offset(x = (-46).dp, y = 30.dp).size(120.dp)) {
            val u = size.width / 120f
            val handleStart = Offset(76 * u, 76 * u)
            val handleEnd = Offset(108 * u, 108 * u)
            drawLine(ToddlerHomeSpec.ArtOutline, handleStart, handleEnd, 16 * u, StrokeCap.Round)
            drawLine(ToddlerHomeSpec.MagnifierHandle, handleStart, handleEnd, 8 * u, StrokeCap.Round)
            val centre = Offset(50 * u, 50 * u)
            drawCircle(Color.White.copy(alpha = 0.45f), 38 * u, centre)
            drawCircle(ToddlerHomeSpec.ArtOutline, 38 * u, centre, style = Stroke(10 * u))
            drawCircle(LocalSun, 38 * u, centre, style = Stroke(5 * u))
        }
    }
}

@Composable
private fun PaintArt() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        ArabicText("ب", size = 110.sp, color = ToddlerHomeSpec.PaintLetter, modifier = Modifier.offset(y = (-10).dp))
        Canvas(Modifier.size(104.dp)) {
            val u = size.width / 120f
            val outline = Stroke(4 * u, join = StrokeJoin.Round)
            rotate(35f, pivot = Offset(61 * u, 60 * u)) {
                val body = Offset(52 * u, 4 * u)
                val bodySize = Size(18 * u, 62 * u)
                drawRoundRect(LocalSun, body, bodySize, CornerRadius(9 * u))
                drawRoundRect(ToddlerHomeSpec.ArtOutline, body, bodySize, CornerRadius(9 * u), style = outline)
                val collar = Offset(49 * u, 62 * u)
                val collarSize = Size(24 * u, 14 * u)
                drawRect(ToddlerHomeSpec.CrayonCollar, collar, collarSize)
                drawRect(ToddlerHomeSpec.ArtOutline, collar, collarSize, style = outline)
                val tip = Path().apply {
                    moveTo(49 * u, 76 * u)
                    lineTo(73 * u, 76 * u)
                    lineTo(69 * u, 100 * u)
                    quadraticTo(61 * u, 112 * u, 53 * u, 100 * u)
                    close()
                }
                drawPath(tip, ToddlerHomeSpec.PaintLetter)
                drawPath(tip, ToddlerHomeSpec.ArtOutline, style = outline)
            }
        }
    }
}

private val LocalSun = Color(0xFFFFC93C)

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun ToddlerHomePreview() {
    HuroofiTheme { ToddlerHomeScreen(onReplayPrompt = {}, onOpenActivity = {}, onRequestParentZone = {}) }
}
