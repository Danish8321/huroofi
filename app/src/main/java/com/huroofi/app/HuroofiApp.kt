package com.huroofi.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.huroofi.app.data.progress.AgeMode
import com.huroofi.app.debug.DEBUG_GALLERY_ROUTE
import com.huroofi.app.debug.debugDestinations
import com.huroofi.app.gate.ParentGateScreen
import com.huroofi.app.toddler.ToddlerBackHandler
import com.huroofi.app.toddler.ToddlerHomeRoute
import com.huroofi.app.toddler.cards.LookListenScreen
import com.huroofi.app.toddler.find.FindScreen
import com.huroofi.app.toddler.paint.PaintScreen
import com.huroofi.app.ui.components.PrimaryButton
import com.huroofi.app.ui.theme.BalooBhaijaan2
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.LocalHuroofiColors
import com.huroofi.app.ui.theme.NotoNaskhArabic
import kotlinx.coroutines.flow.first

object Routes {
    const val Placeholder = "placeholder"
    const val Gate = "gate"
    const val ParentZone = "parent_zone"
    const val ToddlerHome = "toddler_home"
    const val ToddlerCards = "toddler_cards"
    const val ToddlerFind = "toddler_find"
    const val ToddlerPaint = "toddler_paint"
}

/** Home screen for the stored mode. Read once per app start (plan 05 decision 12: no live switch). */
fun startRoute(mode: AgeMode): String = when (mode) {
    AgeMode.TODDLER -> Routes.ToddlerHome
    // Main screen is plan 07.
    AgeMode.PRESCHOOL, AgeMode.READER -> Routes.Placeholder
}

@Composable
fun HuroofiApp(onCloseApp: () -> Unit = {}) {
    HuroofiTheme {
        val progress = LocalAppContainer.current.progress
        var mode by rememberSaveable { mutableStateOf<AgeMode?>(null) }
        LaunchedEffect(Unit) {
            if (mode == null) mode = progress.mode.first()
        }
        when (val start = mode) {
            null -> Box(Modifier.fillMaxSize().background(LocalHuroofiColors.current.sky))
            else -> HuroofiNavHost(startRoute(start), onCloseApp)
        }
    }
}

@Composable
private fun HuroofiNavHost(startRoute: String, onCloseApp: () -> Unit) {
    val nav = rememberNavController()
    val toHome: () -> Unit = { nav.popBackStack(Routes.ToddlerHome, inclusive = false) }
    // From a toddler screen the gate replaces it, so Back on the gate lands on Toddler Home (decision 6).
    val toddlerToGate: () -> Unit = { nav.navigate(Routes.Gate) { popUpTo(Routes.ToddlerHome) } }
    NavHost(navController = nav, startDestination = startRoute) {
        composable(Routes.Placeholder) { PlaceholderScreen() }
        composable(Routes.ToddlerHome) {
            ToddlerBackHandler(Routes.ToddlerHome, startRoute, onPopToToddlerHome = toHome)
            ToddlerHomeRoute(onOpenActivity = { nav.navigate(it.route) }, onRequestParentZone = toddlerToGate)
        }
        composable(Routes.ToddlerCards) {
            ToddlerBackHandler(Routes.ToddlerCards, startRoute, onPopToToddlerHome = toHome)
            LookListenScreen(onHome = toHome, onRequestParentZone = toddlerToGate)
        }
        composable(Routes.ToddlerFind) {
            ToddlerBackHandler(Routes.ToddlerFind, startRoute, onPopToToddlerHome = toHome)
            FindScreen(onHome = toHome, onRequestParentZone = toddlerToGate)
        }
        composable(Routes.ToddlerPaint) {
            ToddlerBackHandler(Routes.ToddlerPaint, startRoute, onPopToToddlerHome = toHome)
            PaintScreen(onHome = toHome, onRequestParentZone = toddlerToGate)
        }
        composable(Routes.Gate) {
            ToddlerBackHandler(Routes.Gate, startRoute, onPopToToddlerHome = toHome)
            ParentGateScreen(
                onOpenParentZone = { nav.navigate(Routes.ParentZone) { popUpTo(Routes.Gate) { inclusive = true } } },
                onCloseApp = onCloseApp,
            )
        }
        composable(Routes.ParentZone) {
            ParentZoneStub(onOpenGallery = DEBUG_GALLERY_ROUTE?.let { route -> { nav.navigate(route) } })
        }
        debugDestinations(onRequestParentZone = { nav.navigate(Routes.Gate) })
    }
}

@Composable
private fun PlaceholderScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Huroofi", style = TextStyle(fontFamily = BalooBhaijaan2, fontSize = 36.sp))
        Text("حروفي", style = TextStyle(fontFamily = NotoNaskhArabic, fontSize = 37.sp))
    }
}

/** Placeholder until plan 06 builds the real Parent zone. [onOpenGallery] is set in debug builds only. */
@Composable
private fun ParentZoneStub(onOpenGallery: (() -> Unit)?) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Parent zone", style = TextStyle(fontFamily = BalooBhaijaan2, fontSize = 26.sp))
        if (onOpenGallery != null) PrimaryButton("Component gallery (debug)", onClick = onOpenGallery)
    }
}
