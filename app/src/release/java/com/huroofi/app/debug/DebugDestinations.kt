package com.huroofi.app.debug

import androidx.navigation.NavGraphBuilder

/** Release builds ship no debug destinations. */
fun NavGraphBuilder.debugDestinations() = Unit
