package com.huroofi.app.ui.components

import androidx.compose.foundation.LocalIndication
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.minTouchTarget

/** Size actually used: never below the touch minimum for the mode. */
fun roundButtonSize(requested: Dp, toddler: Boolean): Dp =
    maxOf(requested, minTouchTarget(toddler))

/**
 * Circular icon button. [contentDescription] is required and not nullable, so an unlabeled
 * icon button cannot be built. [toddler] raises the minimum to 64 dp (default 84 dp).
 * With [shadowColor], a solid edge of [shadowDepth] sits below the face and sinks when pressed,
 * like [PrimaryButton].
 */
@Composable
fun RoundIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    toddler: Boolean = false,
    size: Dp = if (toddler) HuroofiDimens.ToddlerMainControlMin else HuroofiDimens.MinTouch,
    containerColor: Color = LocalHuroofiColors.current.primary,
    shadowColor: Color? = null,
    shadowDepth: Dp = HuroofiDimens.ButtonShadow,
    content: @Composable () -> Unit,
) {
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank" }
    val face = roundButtonSize(size, toddler)
    val depth = if (shadowColor != null) shadowDepth else 0.dp
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    Box(
        modifier = modifier
            .width(face)
            .height(face + depth)
            .semantics { this.contentDescription = contentDescription }
            .clickable(
                interactionSource = source,
                indication = if (shadowColor != null) null else LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        if (shadowColor != null) {
            Box(Modifier.align(Alignment.BottomCenter).size(face).background(shadowColor, CircleShape))
        }
        Box(
            Modifier
                .size(face)
                .offset { IntOffset(0, if (pressed) depth.roundToPx() else 0) }
                .background(containerColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) { content() }
    }
}
