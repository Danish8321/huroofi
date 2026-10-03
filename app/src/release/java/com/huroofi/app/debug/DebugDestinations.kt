package com.huroofi.app.debug

import androidx.navigation.NavGraphBuilder

/** Release builds ship no debug destinations. */
val DEBUG_GALLERY_ROUTE: String? = null

fun NavGraphBuilder.debugDestinations(onRequestParentZone: () -> Unit) = Unit
