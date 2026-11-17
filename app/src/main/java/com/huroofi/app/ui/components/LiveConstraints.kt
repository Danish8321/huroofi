package com.huroofi.app.ui.components

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

// BoxWithConstraints' maxWidth/maxHeight keep the old density after a Display size change: the px
// constraints are the same, so it is not re-measured (plan 16 slice 3). These read the live density.

val BoxWithConstraintsScope.windowWidth: Dp
    @Composable get() = if (constraints.hasBoundedWidth) with(LocalDensity.current) { constraints.maxWidth.toDp() } else Dp.Infinity

val BoxWithConstraintsScope.windowHeight: Dp
    @Composable get() = if (constraints.hasBoundedHeight) with(LocalDensity.current) { constraints.maxHeight.toDp() } else Dp.Infinity
