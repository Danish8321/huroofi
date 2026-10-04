package com.huroofi.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.LocalHuroofiColors

/** Sizes and colours of the toddler Home button, from the toddler prototypes. */
object ToddlerHomeButtonSpec {
    val Size = 68.dp
    val Shadow = 5.dp
    val IconSize = 34.dp
    val ShadowColor = Color(0xFFCFE2F7)
}

/** Big round Home button on every toddler activity. The tablet Find layout passes scaled sizes. */
@Composable
fun ToddlerHomeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = ToddlerHomeButtonSpec.Size,
    iconSize: Dp = ToddlerHomeButtonSpec.IconSize,
    shadow: Dp = ToddlerHomeButtonSpec.Shadow,
) {
    val colors = LocalHuroofiColors.current
    RoundIconButton(
        contentDescription = "Home",
        onClick = onClick,
        modifier = modifier,
        toddler = true,
        size = size,
        containerColor = colors.card,
        shadowColor = ToddlerHomeButtonSpec.ShadowColor,
        shadowDepth = shadow,
    ) { HomeIcon(color = colors.navy, size = iconSize) }
}
