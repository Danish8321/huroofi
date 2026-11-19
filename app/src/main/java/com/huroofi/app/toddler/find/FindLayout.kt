package com.huroofi.app.toddler.find

import com.huroofi.app.ui.theme.TypeScale
import kotlin.math.max
import kotlin.math.min

/** The size `ToddlerFindTablet.html` is drawn at. */
const val TABLET_FIND_DESIGN_WIDTH = 1180f
const val TABLET_FIND_DESIGN_HEIGHT = 820f

/** One uniform scale so the whole design fits the window; never larger than designed. */
fun tabletFindScale(widthDp: Float, heightDp: Float): Float =
    min(min(widthDp / TABLET_FIND_DESIGN_WIDTH, heightDp / TABLET_FIND_DESIGN_HEIGHT), 1f)

/** A control's size after scaling, never below the 64 dp toddler minimum. */
fun minTouch(designDp: Float, scale: Float): Float = max(designDp * scale, 64f)

/** A text size after scaling, never below the 16 sp type floor. */
fun minText(designSp: Float, scale: Float): Float = max(designSp * scale, TypeScale.FLOOR_SP)
