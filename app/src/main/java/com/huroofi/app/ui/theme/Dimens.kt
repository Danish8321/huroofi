package com.huroofi.app.ui.theme

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Shape and size tokens, HANDOFF section 5. */
object HuroofiDimens {
    val PrimaryButtonHeight = 64.dp
    val PrimaryButtonCorner = 22.dp
    val ButtonShadow = 6.dp
    val TileCornerMin = 26.dp
    val TileCornerMax = 40.dp
    val TileBorderMin = 5.dp
    val TileBorderMax = 8.dp
    val MinTouch = 48.dp
    val ToddlerMinTouch = 64.dp
    val ToddlerMainControlMin = 84.dp
    val ToddlerMainControlMax = 104.dp
    val EdgeSafe = 16.dp

    /** Widest any screen's content gets; wider windows centre it (plan 08 decision 4). */
    val MaxContentWidth = 480.dp
}

fun minTouchTarget(toddler: Boolean): Dp =
    if (toddler) HuroofiDimens.ToddlerMinTouch else HuroofiDimens.MinTouch

/** Guarantees a touch area of at least 64 dp (toddler rule). */
fun Modifier.toddlerTouchTarget(): Modifier =
    defaultMinSize(HuroofiDimens.ToddlerMinTouch, HuroofiDimens.ToddlerMinTouch)

/** Guarantees a touch area of at least 48 dp. */
fun Modifier.minTouch(): Modifier =
    defaultMinSize(HuroofiDimens.MinTouch, HuroofiDimens.MinTouch)
