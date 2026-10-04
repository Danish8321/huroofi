package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.HuroofiColors
import com.huroofi.app.ui.theme.HuroofiDimens
import com.huroofi.app.ui.theme.HuroofiText
import com.huroofi.app.ui.theme.LocalHuroofiColors

enum class ButtonKind { Primary, Success, Sun }

private data class ButtonColors(val face: Color, val shadow: Color, val label: Color)

private fun ButtonKind.colors(c: HuroofiColors) = when (this) {
    ButtonKind.Primary -> ButtonColors(c.primary, c.primaryShadow, c.card)
    ButtonKind.Success -> ButtonColors(c.success, c.successShadow, c.card)
    ButtonKind.Sun -> ButtonColors(c.sun, c.sunShadow, c.navy)
}

/**
 * 64 dp tall, 22 dp corners, solid face with a 6 dp darker bottom edge that sinks when pressed.
 * [icon] (see [ButtonIcons]) follows the label, as in the prototypes.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    kind: ButtonKind = ButtonKind.Primary,
    enabled: Boolean = true,
    icon: List<String>? = null,
) {
    val colors = kind.colors(LocalHuroofiColors.current)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val shape = RoundedCornerShape(HuroofiDimens.PrimaryButtonCorner)
    val depth = HuroofiDimens.ButtonShadow

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HuroofiDimens.PrimaryButtonHeight + depth)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(HuroofiDimens.PrimaryButtonHeight)
                .background(colors.shadow, shape),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(HuroofiDimens.PrimaryButtonHeight)
                .offset { IntOffset(0, if (pressed) depth.roundToPx() else 0) }
                .background(colors.face, shape)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text, style = HuroofiText.buttonPrimary, color = colors.label)
                if (icon != null) LineIcon(icon, colors.label, size = 26.dp, strokeWidth = 2.6f)
            }
        }
    }
}
