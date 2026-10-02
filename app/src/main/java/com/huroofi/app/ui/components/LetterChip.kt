package com.huroofi.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.ArabicText
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.StageColors

/** Selected state is a thick navy ring plus tinted fill, so colour is never the only signal. */
@Composable
fun LetterChip(
    letter: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    stage: StageColors? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = LocalHuroofiColors.current
    val shape = RoundedCornerShape(HuroofiDimens.TileCornerMin)
    val border = stage?.border ?: colors.muted
    val ring = if (selected) BorderStroke(8.dp, colors.navy) else BorderStroke(4.dp, border)
    val fill = if (selected) (stage?.pastel ?: colors.sky) else colors.card

    Box(
        modifier = modifier
            .size(HuroofiDimens.ToddlerMinTouch)
            .clip(shape)
            .background(fill)
            .border(ring, shape)
            .semantics {
                this.contentDescription = contentDescription
                this.selected = selected
            }
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        ArabicText(letter, color = stage?.accent ?: colors.navy)
    }
}
