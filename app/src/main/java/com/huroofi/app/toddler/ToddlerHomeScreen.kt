package com.huroofi.app.toddler

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.R
import com.huroofi.app.learn.dropEdge
import com.huroofi.app.ui.components.CappedWidth
import com.huroofi.app.ui.components.ParentLock
import com.huroofi.app.ui.components.ScaleDownToFit
import com.huroofi.app.ui.components.SoundIcon
import com.huroofi.app.ui.components.StarIcon
import com.huroofi.app.ui.components.useWideLayout
import com.huroofi.app.ui.components.windowHeight
import com.huroofi.app.ui.components.windowWidth
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.IgnoreFontScale
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
    BoxWithConstraints(modifier.fillMaxSize()) {
        // Wide landscape tablet (`ToddlerHomeTablet.html`): the question on one line, the tiles side by side.
        val wide = useWideLayout(windowWidth.value, windowHeight.value)
        ToddlerScaffold(
            top = 24.dp,
            wide = wide,
            overlay = {
                ParentLock(
                    onRequestParentZone = onRequestParentZone,
                    contentDescription = PARENT_LOCK_DESCRIPTION,
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 18.dp, end = 18.dp),
                )
            },
        ) {
            // The bubble stops short of the corner lock. On a narrow phone Leo gives up width first.
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val leo = (windowWidth * spec.MASCOT_SHARE).coerceIn(spec.MascotMin, spec.MascotSize)
                Row(
                    Modifier.padding(end = 52.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painterResource(R.drawable.pic_lion), contentDescription = null, modifier = Modifier.size(leo))
                    PromptBubble(onReplayPrompt, oneLine = wide, if (wide) Modifier else Modifier.weight(1f))
                }
            }
            if (wide) {
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(spec.WideGap)) {
                    for (activity in ToddlerActivity.entries) {
                        ActivityTile(activity, wide = true, onClick = { onOpenActivity(activity) }, modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                }
            } else {
                for (activity in ToddlerActivity.entries) {
                    ActivityTile(
                        activity,
                        wide = false,
                        onClick = { onOpenActivity(activity) },
                        modifier = Modifier.weight(1f).heightIn(max = spec.TileHeight + spec.TileShadow),
                    )
                }
            }
        }
    }
}

/**
 * [background] (the sky unless given), prototype padding, and a centred column ([CappedWidth]).
 * [overlay] sits over the column inside the safe area, for corner controls.
 */
@Composable
fun ToddlerScaffold(
    modifier: Modifier = Modifier,
    background: Color = LocalHuroofiColors.current.sky,
    top: Dp = 18.dp,
    /** A wide landscape tablet layout uses the whole width instead of the centred phone column. */
    wide: Boolean = false,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    CappedWidth(background, modifier, wide) {
        Column(
            Modifier
                .fillMaxHeight()
                .then(if (wide) Modifier.fillMaxWidth() else Modifier)
                .safeDrawingPadding()
                .padding(start = 20.dp, end = 20.dp, top = top, bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(ToddlerHomeSpec.Gap),
            content = content,
        )
        Box(Modifier.matchParentSize().safeDrawingPadding(), content = overlay)
    }
}

/** Leo's question. A tap anywhere on it asks again; the sun button says so. */
@Composable
private fun PromptBubble(onClick: () -> Unit, oneLine: Boolean, modifier: Modifier = Modifier) {
    val spec = ToddlerHomeSpec
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = modifier
            .heightIn(min = spec.BubbleHeight)
            .dropEdge(spec.BubbleEdgeColor, spec.BubbleEdge, shape)
            .background(HuroofiTokens.Card, shape)
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick)
            .clearAndSetSemantics { contentDescription = "Leo says: What shall we play? Tap to hear it again" }
            .padding(start = 10.dp, end = 18.dp, top = 10.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(spec.SunButton)
                .dropEdge(HuroofiTokens.SunShadow, spec.SunEdge, CircleShape)
                .background(HuroofiTokens.Sun, CircleShape),
            contentAlignment = Alignment.Center,
        ) { SoundIcon(HuroofiTokens.Navy, size = 28.dp) }
        // Two lines as designed, broken between the words: a narrow phone shrinks them, never splits a word.
        ScaleDownToFit(if (oneLine) Modifier else Modifier.weight(1f)) {
            ArabicText(if (oneLine) "ماذا نلعب؟" else "ماذا\nنلعب؟", size = spec.BUBBLE_SP.sp, color = HuroofiTokens.Navy)
        }
    }
}

@Composable
private fun ActivityTile(activity: ToddlerActivity, wide: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val spec = ToddlerHomeSpec
    val tile = activity.tile
    val shape = RoundedCornerShape(spec.TileCorner)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    Box(
        modifier
            .fillMaxWidth()
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .clearAndSetSemantics { contentDescription = activity.contentDescription },
    ) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = spec.TileShadow)
                .background(tile.shadow, shape),
        )
        Box(
            Modifier
                .matchParentSize()
                .padding(bottom = spec.TileShadow)
                .offset { IntOffset(0, if (pressed) spec.TileShadow.roundToPx() else 0) }
                .background(tile.background, shape)
                .border(spec.TileBorder, tile.border, shape),
        ) {
            // Art and label keep their designed size, shrinking only in a short window; a tall wide tile grows the art.
            val art = @Composable { m: Modifier ->
                ScaleDownToFit(m, maxScale = if (wide) spec.WIDE_ART_SCALE else 1f) {
                    when (activity) {
                        ToddlerActivity.CARDS -> CardsArt()
                        ToddlerActivity.FIND -> FindArt()
                        ToddlerActivity.PAINT -> PaintArt()
                    }
                }
            }
            // The play circle sits under its label, so a narrow or short tile never puts it over the word.
            val label = @Composable { m: Modifier ->
                ScaleDownToFit(m) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ArabicText(activity.arabicLabel, size = spec.LABEL_SP.sp, color = tile.text)
                        GoCircle(tile.border, Modifier)
                    }
                }
            }
            if (wide) {
                Column(
                    Modifier.matchParentSize().padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    art(Modifier.weight(1f).fillMaxWidth())
                    label(Modifier)
                }
            } else {
                Row(
                    Modifier.matchParentSize().padding(start = 16.dp, end = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    art(Modifier.weight(1f))
                    label(Modifier.widthIn(min = spec.LabelMinWidth))
                }
            }
        }
    }
}

/** The white play button in the tile's corner: decoration, the whole tile is the button. */
@Composable
private fun GoCircle(color: Color, modifier: Modifier) {
    Box(modifier.size(ToddlerHomeSpec.GoCircle).background(color, CircleShape), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(22.dp)) {
            val u = size.width / 24f
            scale(u, u, pivot = Offset.Zero) { drawPath(PlayTriangle, Color.White) }
        }
    }
}

private val PlayTriangle = Path().apply {
    moveTo(8f, 5.5f)
    lineTo(8f, 18.5f)
    lineTo(18.5f, 12f)
    close()
}

@Composable
private fun CardsArt() {
    Row {
        MiniCard(R.drawable.pic_lion, Modifier.offset(x = 10.dp).rotate(-8f))
        MiniCard(R.drawable.pic_duck, Modifier.padding(start = 8.dp).rotate(6f))
    }
}

@Composable
private fun MiniCard(picture: Int, modifier: Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier
            .size(width = 84.dp, height = 112.dp)
            .background(HuroofiTokens.Card, shape)
            .border(4.dp, ToddlerHomeSpec.MiniCardBorder, shape),
        contentAlignment = Alignment.Center,
    ) { Image(painterResource(picture), contentDescription = null, modifier = Modifier.size(66.dp)) }
}

/** A found duck with its star beside an apple: what Find it plays. */
@Composable
private fun FindArt() {
    // Top padding makes room for the star poking above the card, so ScaleDownToFit counts it.
    Row(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box {
            MiniPicture(R.drawable.pic_duck, Modifier.border(4.dp, HuroofiTokens.Success, RoundedCornerShape(18.dp)))
            StarIcon(HuroofiTokens.Sun, Modifier.align(Alignment.TopEnd).offset(x = 10.dp, y = (-14).dp), size = 28.dp, outline = HuroofiTokens.SunShadow)
        }
        MiniPicture(R.drawable.pic_apple)
    }
}

@Composable
private fun MiniPicture(picture: Int, modifier: Modifier = Modifier) {
    Box(
        Modifier.size(84.dp).background(HuroofiTokens.Card, RoundedCornerShape(18.dp)).then(modifier),
        contentAlignment = Alignment.Center,
    ) { Image(painterResource(picture), contentDescription = null, modifier = Modifier.size(62.dp)) }
}

@Composable
private fun PaintArt() {
    Box(
        Modifier.size(width = 150.dp, height = 104.dp).background(HuroofiTokens.Card, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        IgnoreFontScale { ArabicText("ب", size = 70.sp, color = ToddlerHomeSpec.PaintLetter) }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun ToddlerHomePreview() {
    HuroofiTheme { ToddlerHomeScreen(onReplayPrompt = {}, onOpenActivity = {}, onRequestParentZone = {}) }
}
