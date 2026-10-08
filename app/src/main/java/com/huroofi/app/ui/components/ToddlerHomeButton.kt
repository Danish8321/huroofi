package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiTokens

/** Sizes and colours of the toddler Home button (`.t-home` in the prototype). */
object ToddlerHomeButtonSpec {
    val Size = 64.dp
    val Shadow = 5.dp
    val IconSize = 32.dp

    /** Corner over size, so the scaled tablet button keeps the shape. */
    const val CORNER_FRACTION = 22f / 64f
    val ShadowColor = Color(0xFFCFE2F7)
    val textPairs = listOf(ContrastPair(HuroofiTokens.Primary, HuroofiTokens.Card, large = true, "home icon"))
}

/**
 * The white rounded-square Home button on every toddler activity. The tablet Find layout passes
 * scaled sizes; Paint passes its pink.
 */
@Composable
fun ToddlerHomeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = ToddlerHomeButtonSpec.Size,
    iconSize: Dp = ToddlerHomeButtonSpec.IconSize,
    shadow: Dp = ToddlerHomeButtonSpec.Shadow,
    iconColor: Color = HuroofiTokens.Primary,
    shadowColor: Color = ToddlerHomeButtonSpec.ShadowColor,
) {
    val shape = RoundedCornerShape(size * ToddlerHomeButtonSpec.CORNER_FRACTION)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    Box(
        modifier
            .width(size)
            .height(size + shadow)
            .semantics { contentDescription = "Home" }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick),
    ) {
        Box(Modifier.align(Alignment.BottomCenter).size(size).background(shadowColor, shape))
        Box(
            Modifier
                .size(size)
                .offset { IntOffset(0, if (pressed) shadow.roundToPx() else 0) }
                .background(HuroofiTokens.Card, shape),
            contentAlignment = Alignment.Center,
        ) { HomeIcon(color = iconColor, size = iconSize) }
    }
}
