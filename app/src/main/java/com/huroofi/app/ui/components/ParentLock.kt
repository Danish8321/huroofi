package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.LocalHuroofiColors

/**
 * The one control exempt from the 64 dp toddler touch rule (plan 05, decision 1): a toddler
 * should not hit it, so it stays a small circle inside a 48 dp hit area.
 */
object ParentLockSize {
    val Visible = 44.dp
    val Hit = 48.dp
}

/**
 * Small lock button. It only reports [onRequestParentZone]; it never opens anything itself.
 * The 3-second hold lives on the parent gate screen.
 */
@Composable
fun ParentLock(
    onRequestParentZone: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalHuroofiColors.current
    Box(
        modifier = modifier
            .size(ParentLockSize.Hit)
            .semantics { this.contentDescription = contentDescription }
            .clickable(role = Role.Button, onClick = onRequestParentZone),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(ParentLockSize.Visible).background(colors.card, CircleShape),
            contentAlignment = Alignment.Center,
        ) { LockIcon(color = colors.muted, size = 28.dp) }
    }
}
