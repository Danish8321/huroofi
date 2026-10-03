package com.huroofi.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Phones: portrait only. Tablets (sw600dp): free rotation. Decided by a resource qualifier.
        requestedOrientation = if (resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        val container = (application as HuroofiApplication).container
        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                HuroofiApp(onCloseApp = { finishAffinity() })
            }
        }
    }
}
