package com.huroofi.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun HuroofiTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalHuroofiColors provides HuroofiColors()) {
        MaterialTheme(
            colorScheme = huroofiColorScheme(),
            typography = huroofiTypography(),
            content = content,
        )
    }
}
