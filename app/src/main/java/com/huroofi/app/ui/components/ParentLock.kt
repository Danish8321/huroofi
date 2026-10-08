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
import androidx.compose.ui.unit.dp
import com.huroofi.app.ui.theme.ContrastPair
import com.huroofi.app.ui.theme.HuroofiTokens
import com.huroofi.app.ui.theme.LocalHuroofiColors

/**
 * The one control exempt from the 64 dp toddler touch rule (plan 05, decision 1): a toddler
 * should not hit it, so it stays a small circle inside a 48 dp hit area.
 */
object ParentLockSize {
    val Visible = 44.dp
    val Hit = 48.dp
}

/** For the contrast sweep: every screen with a lock adds these. */
val parentLockPairs = listOf(ContrastPair(HuroofiTokens.Muted, HuroofiTokens.Card, large = true, "parent lock icon"))

/**
 * Small lock button. It only reports [onRequestParentZone]; it never opens anything itself.
 * The 2-second hold lives on the parent gate screen. [face] and [icon] change only on the night-time Rest screen.
 */
@Composable
fun ParentLock(
    onRequestParentZone: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    face: Color = LocalHuroofiColors.current.card,
    icon: Color = LocalHuroofiColors.current.muted,
) {
    Box(
        modifier = modifier
            .size(ParentLockSize.Hit)
            .semantics { this.contentDescription = contentDescription }
            .clickable(role = Role.Button, onClick = onRequestParentZone),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(ParentLockSize.Visible).background(face, CircleShape),
            contentAlignment = Alignment.Center,
        ) { LockIcon(color = icon, size = 28.dp) }
    }
}
