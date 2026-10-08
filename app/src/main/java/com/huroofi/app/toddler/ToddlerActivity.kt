package com.huroofi.app.toddler

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.huroofi.app.Routes
import com.huroofi.app.ui.components.parentLockPairs
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiTokens

/** Fill, border, bottom edge and label colour of one Toddler Home tile. */
data class TileColors(val background: Color, val border: Color, val shadow: Color, val text: Color)

/** The three toddler activities, in Toddler Home order. Colours from `ToddlerHome.html`. */
enum class ToddlerActivity(
    val route: String,
    val arabicLabel: String,
    val contentDescription: String,
    val tile: TileColors,
) {
    CARDS(
        Routes.ToddlerCards, "اسمع", "Look and listen: picture cards",
        TileColors(Color(0xFFFFEDC4), Color(0xFFF59E0B), Color(0xFFC77F00), Color(0xFFA35400)),
    ),
    FIND(
        Routes.ToddlerFind, "ابحث", "Find it: where is the picture?",
        TileColors(Color(0xFFDAEAFF), Color(0xFF2F80ED), Color(0xFF0B4FB0), Color(0xFF0B4FB0)),
    ),
    PAINT(
        Routes.ToddlerPaint, "ارسم", "Paint the letter",
        TileColors(Color(0xFFFFDDEA), Color(0xFFEC4F8C), Color(0xFFB83A6B), Color(0xFFB0185A)),
    ),
}

/** Sizes and colours on Toddler Home (`ToddlerHome.html`, plan 15 decision 6). */
object ToddlerHomeSpec {
    /** Tiles share the height left under the header; this caps them on tall screens. */
    val TileHeight = 196.dp
    val TileCorner = 36.dp
    val TileBorder = 5.dp
    val TileShadow = 8.dp
    val LabelMinWidth = 100.dp
    val GoCircle = 48.dp
    val MascotSize = 96.dp
    val BubbleHeight = 76.dp
    val BubbleEdge = 4.dp
    val SunButton = 56.dp
    val SunEdge = 5.dp
    val Gap = 16.dp
    const val LABEL_SP = 44f
    const val BUBBLE_SP = 34f
    val BubbleEdgeColor = Color(0xFFCFE2F7)
    val MiniCardBorder = Color(0xFFF59E0B)

    /** The prototype's paint letter is #FF7A59, a red; the tile's own pink stands in. */
    val PaintLetter = Color(0xFFEC4F8C)
    val colors: List<Color> = listOf(BubbleEdgeColor, MiniCardBorder, PaintLetter, HuroofiTokens.Success, HuroofiTokens.Sun)

    /** Tile art and the go circles are decoration: each tile is named by its label and description. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "prompt bubble Arabic"),
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Sun, large = true, "prompt speaker icon"),
    ) + ToddlerActivity.entries.map {
        ContrastPair(it.tile.text, it.tile.background, large = true, "${it.name} tile Arabic label")
    } + parentLockPairs
}
