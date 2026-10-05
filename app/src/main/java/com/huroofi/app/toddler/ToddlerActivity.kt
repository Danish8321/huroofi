package com.huroofi.app.toddler

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.huroofi.app.Routes
import com.huroofi.app.ui.components.ParentLockSize
import com.huroofi.app.ui.components.parentLockPairs
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiTokens

/** Fill, border, bottom edge and label colour of one Toddler Home tile. */
data class TileColors(val background: Color, val border: Color, val shadow: Color, val text: Color)

/** The three toddler activities, in Toddler Home order. Colours from `ToddlerHome.html`. */
enum class ToddlerActivity(
    val route: String,
    val arabicLabel: String,
    val englishLabel: String,
    val contentDescription: String,
    val tile: TileColors,
) {
    CARDS(
        Routes.ToddlerCards, "اسمع", "Look & listen", "Look and listen: picture cards",
        TileColors(Color(0xFFFFEDC4), Color(0xFFF59E0B), Color(0xFFC77F00), Color(0xFF6B4A00)),
    ),
    FIND(
        Routes.ToddlerFind, "ابحث", "Find it", "Find it: where is the picture?",
        TileColors(Color(0xFFDAEAFF), Color(0xFF2F80ED), Color(0xFF1F5FB8), Color(0xFF0B4FB0)),
    ),
    PAINT(
        Routes.ToddlerPaint, "ارسم", "Paint", "Paint the letter",
        TileColors(Color(0xFFFFDDEA), Color(0xFFEC4F8C), Color(0xFFB83A6B), Color(0xFF8A1E4C)),
    ),
}

/** Sizes and colours on Toddler Home. */
object ToddlerHomeSpec {
    val TileHeight = 196.dp
    val TileCorner = 40.dp
    val TileBorder = 5.dp
    val TileShadow = 8.dp
    val LabelColumnWidth = 92.dp
    val MascotSize = 96.dp
    val BubbleHeight = 68.dp

    /** Lock above the bubble. The prototype's 112 assumes a 44 dp lock; ours is 48 dp to touch, so 116. */
    val HeaderHeight = ParentLockSize.Hit + BubbleHeight
    val Gap = 20.dp
    val MiniCardBorder = Color(0xFFF59E0B)
    val PaintLetter = Color(0xFFEC4F8C)
    val ArtOutline = Color(0xFF26324A)
    val MagnifierHandle = Color(0xFF8D5A2B)
    val CrayonCollar = Color(0xFFB0BEC5)
    val colors: List<Color> = listOf(MiniCardBorder, PaintLetter, ArtOutline, MagnifierHandle, CrayonCollar)

    /** Tile art is decoration: each tile is named by its label. */
    val textPairs: List<ContrastPair> = listOf(
        ContrastPair(HuroofiTokens.Navy, HuroofiTokens.Card, large = true, "prompt bubble Arabic"),
        ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = true, "prompt speaker icon"),
    ) + ToddlerActivity.entries.flatMap {
        listOf(
            ContrastPair(it.tile.text, it.tile.background, large = true, "${it.name} tile Arabic label"),
            ContrastPair(it.tile.text, it.tile.background, large = false, "${it.name} tile English label"),
        )
    } + parentLockPairs
}
