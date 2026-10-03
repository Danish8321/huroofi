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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.LocalHuroofiColors

/** Sizes and colours of the toddler Home button, from the toddler prototypes. */
object ToddlerHomeButtonSpec {
    val Size = 68.dp
    val Shadow = 5.dp
    val IconSize = 34.dp
    val ShadowColor = Color(0xFFCFE2F7)
}

/** Big round Home button on every toddler activity. Its edge sinks when pressed, like [PrimaryButton]. */
@Composable
fun ToddlerHomeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalHuroofiColors.current
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val spec = ToddlerHomeButtonSpec
    Box(
        modifier = modifier
            .width(spec.Size)
            .height(spec.Size + spec.Shadow)
            .semantics { contentDescription = "Home" }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick),
    ) {
        Box(Modifier.align(Alignment.BottomCenter).size(spec.Size).background(spec.ShadowColor, CircleShape))
        Box(
            Modifier
                .size(spec.Size)
                .offset { IntOffset(0, if (pressed) spec.Shadow.roundToPx() else 0) }
                .background(colors.card, CircleShape),
            contentAlignment = Alignment.Center,
        ) { HomeIcon(color = colors.navy, size = spec.IconSize) }
    }
}
