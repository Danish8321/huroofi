package com.huroofi.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.minTouchTarget

/**
 * Small lock button. It only reports [onRequestParentZone]; it never opens anything itself.
 * The parent gate (3-second hold) is built in the toddler/parent plans.
 */
@Composable
fun ParentLock(
    onRequestParentZone: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    toddler: Boolean = false,
) {
    val colors = LocalHuroofiColors.current
    RoundIconButton(
        contentDescription = contentDescription,
        onClick = onRequestParentZone,
        modifier = modifier,
        toddler = toddler,
        size = minTouchTarget(toddler),
        containerColor = colors.card,
    ) { LockIcon(color = colors.muted, size = 28.dp) }
}
