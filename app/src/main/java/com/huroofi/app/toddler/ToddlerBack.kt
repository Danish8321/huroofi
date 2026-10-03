package com.huroofi.app.toddler

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.huroofi.app.Routes

enum class BackAction { Swallow, PopToToddlerHome, Default }

/**
 * What system Back does on [route] (plan 05 decision 6). Toddler Home swallows it, so a child can't
 * leave the app; activities and the gate go back to Toddler Home. Leaving is only through the gate.
 * The Rest screen swallows Back in every mode (plan 06 decision 6).
 */
fun toddlerBack(route: String, startRoute: String): BackAction = when {
    route == Routes.Rest -> BackAction.Swallow
    startRoute != Routes.ToddlerHome -> BackAction.Default
    route == Routes.ToddlerHome -> BackAction.Swallow
    route in TODDLER_RETURNS_HOME -> BackAction.PopToToddlerHome
    else -> BackAction.Default
}

private val TODDLER_RETURNS_HOME = setOf(
    Routes.ToddlerCards,
    Routes.ToddlerFind,
    Routes.ToddlerPaint,
    Routes.Gate,
)

/** Applies [toddlerBack] for [route]; place it inside that route's destination. */
@Composable
fun ToddlerBackHandler(route: String, startRoute: String, onPopToToddlerHome: () -> Unit) {
    val action = toddlerBack(route, startRoute)
    BackHandler(enabled = action != BackAction.Default) {
        if (action == BackAction.PopToToddlerHome) onPopToToddlerHome()
    }
}
