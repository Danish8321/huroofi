package com.huroofi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.huroofi.app.ui.theme.HuroofiDimens

/**
 * Fills the window with [background] and centres [content] no wider than
 * [HuroofiDimens.MaxContentWidth], so phone layouts keep their shape on tablets (plan 08 decision 4).
 * The content is at least [minHeight] tall; a shorter window (tablet landscape) scrolls it.
 */
@Composable
fun CappedWidth(
    background: Color,
    modifier: Modifier = Modifier,
    minHeight: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxSize().background(background)) {
        val height = max(maxHeight, minHeight)
        // Scrolls across the whole width, so a swipe beside the column works too.
        Box(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState(), enabled = height > maxHeight)
                .height(height),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(Modifier.fillMaxHeight().widthIn(max = HuroofiDimens.MaxContentWidth), content = content)
        }
    }
}
