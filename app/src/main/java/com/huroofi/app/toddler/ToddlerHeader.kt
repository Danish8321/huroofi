package com.huroofi.app.toddler

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.huroofi.app.ui.components.ParentLock
import com.huroofi.app.ui.components.ToddlerHomeButton
import com.huroofi.app.ui.components.ToddlerHomeButtonSpec
import com.huroofi.app.ui.theme.HuroofiTokens

/** Top row of every toddler activity: Home on the left, [middle] centred, the small lock on the right. */
@Composable
fun ToddlerHeader(
    onHome: () -> Unit,
    onRequestParentZone: () -> Unit,
    modifier: Modifier = Modifier,
    homeIcon: Color = HuroofiTokens.Primary,
    homeShadow: Color = ToddlerHomeButtonSpec.ShadowColor,
    middle: @Composable () -> Unit = {},
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ToddlerHomeButton(onClick = onHome, iconColor = homeIcon, shadowColor = homeShadow)
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { middle() }
        ParentLock(onRequestParentZone = onRequestParentZone, contentDescription = PARENT_LOCK_DESCRIPTION)
    }
}
