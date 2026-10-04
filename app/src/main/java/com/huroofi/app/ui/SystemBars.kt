package com.huroofi.app.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.huroofi.app.Routes

/**
 * Every screen a child can reach hides the status and navigation bars; only the Parent zone shows
 * them (plan 08 decision 7). Null (nothing shown yet) counts as a child screen.
 */
fun barsHidden(route: String?): Boolean = route != Routes.ParentZone

/**
 * Hides or shows the system bars for [route]. An edge swipe shows hidden bars for a moment only.
 * Applied again when the window regains focus, since the app switcher or a dialog can bring them back.
 */
@Composable
fun ImmersiveBars(route: String?) {
    val window = LocalActivity.current?.window ?: return
    val hidden = barsHidden(route)
    val focused = LocalWindowInfo.current.isWindowFocused
    LaunchedEffect(hidden, focused) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (hidden) controller.hide(WindowInsetsCompat.Type.systemBars())
        else {
            // The Parent zone background is light, so its bar icons are dark.
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}
