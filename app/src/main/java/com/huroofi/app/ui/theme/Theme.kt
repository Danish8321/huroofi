package com.huroofi.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun HuroofiTheme(content: @Composable () -> Unit) {
    // HANDOFF §1: the UI is English and never mirrors, even on an Arabic, Urdu or Hebrew phone.
    // Arabic letter rows set Rtl themselves.
    CompositionLocalProvider(
        LocalHuroofiColors provides HuroofiColors(),
        LocalLayoutDirection provides LayoutDirection.Ltr,
    ) {
        MaterialTheme(
            colorScheme = huroofiColorScheme(),
            typography = huroofiTypography(),
            content = content,
        )
    }
}
