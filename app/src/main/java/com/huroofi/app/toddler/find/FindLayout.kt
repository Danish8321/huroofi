package com.huroofi.app.toddler.find

import kotlin.math.max
import kotlin.math.min

/** The size `ToddlerFindTablet.html` is drawn at. */
const val TABLET_FIND_DESIGN_WIDTH = 1180f
const val TABLET_FIND_DESIGN_HEIGHT = 820f

/** The tablet layout is for wide landscape windows only; tablet portrait keeps the phone layout (plan 08 decision 3). */
fun useTabletFind(widthDp: Float, heightDp: Float): Boolean = widthDp >= 840f && widthDp > heightDp

/** One uniform scale so the whole design fits the window; never larger than designed. */
fun tabletFindScale(widthDp: Float, heightDp: Float): Float =
    min(min(widthDp / TABLET_FIND_DESIGN_WIDTH, heightDp / TABLET_FIND_DESIGN_HEIGHT), 1f)

/** A control's size after scaling, never below the 64 dp toddler minimum. */
fun minTouch(designDp: Float, scale: Float): Float = max(designDp * scale, 64f)
