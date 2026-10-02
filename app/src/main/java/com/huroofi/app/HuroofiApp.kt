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
import com.huroofi.app.debug.debugDestinations
import com.huroofi.app.ui.theme.BalooBhaijaan2
import com.huroofi.app.ui.theme.HuroofiTheme
import com.huroofi.app.ui.theme.NotoNaskhArabic

object Routes {
    const val Placeholder = "placeholder"
}

@Composable
fun HuroofiApp() {
    HuroofiTheme {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = Routes.Placeholder) {
            composable(Routes.Placeholder) { PlaceholderScreen() }
            debugDestinations()
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
