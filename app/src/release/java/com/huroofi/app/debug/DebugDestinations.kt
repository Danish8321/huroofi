package com.huroofi.app.debug

import androidx.navigation.NavGraphBuilder

/** Release builds ship no debug destinations and start on the normal first screen. */
const val DEBUG_START_ROUTE: String = "placeholder"

fun NavGraphBuilder.debugDestinations() = Unit
