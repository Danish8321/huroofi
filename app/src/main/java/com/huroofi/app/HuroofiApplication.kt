package com.huroofi.app

import android.app.Application

class HuroofiApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
