package com.huroofi.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.huroofi.app.debug.DEBUG_START_ROUTE
import com.huroofi.app.debug.debugDestinations
import com.huroofi.app.gate.ParentGateScreen
import com.huroofi.app.ui.theme.BalooBhaijaan2
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.NotoNaskhArabic

object Routes {
    const val Placeholder = "placeholder"
    const val Gate = "gate"
    const val ParentZone = "parent_zone"
}

@Composable
fun HuroofiApp(onCloseApp: () -> Unit = {}) {
    HuroofiTheme {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = DEBUG_START_ROUTE) {
            composable(Routes.Placeholder) { PlaceholderScreen() }
            composable(Routes.Gate) {
                ParentGateScreen(
                    onOpenParentZone = { nav.navigate(Routes.ParentZone) { popUpTo(Routes.Gate) { inclusive = true } } },
                    onCloseApp = onCloseApp,
                )
            }
            composable(Routes.ParentZone) { ParentZoneStub() }
            debugDestinations(onRequestParentZone = { nav.navigate(Routes.Gate) })
        }
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

/** Placeholder until plan 06 builds the real Parent zone. */
@Composable
private fun ParentZoneStub() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Parent zone", style = TextStyle(fontFamily = BalooBhaijaan2, fontSize = 26.sp))
    }
}
