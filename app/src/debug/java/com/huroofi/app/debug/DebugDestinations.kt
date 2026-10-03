package com.huroofi.app.debug

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.huroofi.app.ui.components.ComponentSamples

const val GALLERY_ROUTE = "gallery"

/** Debug builds reach the component gallery from the Parent zone placeholder. */
val DEBUG_GALLERY_ROUTE: String? = GALLERY_ROUTE

fun NavGraphBuilder.debugDestinations(onRequestParentZone: () -> Unit) {
    composable(GALLERY_ROUTE) {
        ComponentSamples(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            onRequestParentZone = onRequestParentZone,
        )
    }
}
