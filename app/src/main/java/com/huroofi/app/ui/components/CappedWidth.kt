package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.huroofi.app.ui.theme.HuroofiDimens

/**
 * Fills the window with [background] and centres [content] no wider than
 * [HuroofiDimens.MaxContentWidth], so phone layouts keep their shape on tablets (plan 08 decision 4).
 * A screen that may not fit a short window scrolls its own middle ([FillColumn]).
 * [wide] (a screen's own wide landscape tablet layout, [useWideLayout]) uses the whole width instead.
 */
@Composable
fun CappedWidth(
    background: Color,
    modifier: Modifier = Modifier,
    wide: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxSize().background(background), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.fillMaxHeight().then(if (wide) Modifier.fillMaxWidth() else Modifier.widthIn(max = HuroofiDimens.MaxContentWidth)), content = content)
    }
}
