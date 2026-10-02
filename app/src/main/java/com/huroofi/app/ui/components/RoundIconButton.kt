package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.minTouchTarget

/** Size actually used: never below the touch minimum for the mode. */
fun roundButtonSize(requested: Dp, toddler: Boolean): Dp =
    maxOf(requested, minTouchTarget(toddler))

/**
 * Circular icon button. [contentDescription] is required and not nullable, so an unlabeled
 * icon button cannot be built. [toddler] raises the minimum to 64 dp (default 84 dp).
 */
@Composable
fun RoundIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    toddler: Boolean = false,
    size: Dp = if (toddler) HuroofiDimens.ToddlerMainControlMin else HuroofiDimens.MinTouch,
    containerColor: Color = LocalHuroofiColors.current.primary,
    content: @Composable () -> Unit,
) {
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank" }
    Box(
        modifier = modifier
            .size(roundButtonSize(size, toddler))
            .background(containerColor, CircleShape)
            .semantics { this.contentDescription = contentDescription }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
