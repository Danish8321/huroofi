package com.huroofi.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.StageColors
import com.huroofi.app.ui.theme.TypeScale

/**
 * Illustration with an optional Arabic word and a stage-coloured border (5-8 dp).
 * [rotationDegrees] and [nudgeX] are hoisted so the games own the wiggle and nudge animations.
 */
@Composable
fun PictureTile(
    image: Painter,
    contentDescription: String,
    stage: StageColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    word: String? = null,
    wordSizeSp: Float = TypeScale.ToddlerWord.min,
    borderWidth: Dp = HuroofiDimens.TileBorderMax,
    imageSize: Dp = 160.dp,
    rotationDegrees: Float = 0f,
    nudgeX: Dp = 0.dp,
) {
    require(borderWidth in HuroofiDimens.TileBorderMin..HuroofiDimens.TileBorderMax) {
        "Tile border must be 5-8 dp"
    }
    val shape = RoundedCornerShape(HuroofiDimens.TileCornerMax)
    Column(
        modifier = modifier
            .offset { IntOffset(nudgeX.roundToPx(), 0) }
            .rotate(rotationDegrees)
            .clip(shape)
            .background(LocalHuroofiColors.current.card)
            .border(borderWidth, stage.border, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(image, contentDescription = contentDescription, modifier = Modifier.size(imageSize))
        if (word != null) {
            ArabicText(word, size = wordSizeSp.sp, color = LocalHuroofiColors.current.navy)
        }
    }
}
