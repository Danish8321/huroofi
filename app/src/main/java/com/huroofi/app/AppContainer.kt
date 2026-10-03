package com.huroofi.app

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.preferences.preferencesDataStore
import com.huroofi.app.audio.AndroidAudioBackend
import com.huroofi.app.audio.GatedSoundPlayer
import com.huroofi.app.audio.SoundPlayer
import com.huroofi.app.data.content.AssetReader
import com.huroofi.app.data.content.ContentRepository
import com.huroofi.app.data.progress.ProgressRepository
import kotlinx.coroutines.flow.first

private val Context.progressStore by preferencesDataStore(name = "progress")

/** Manual dependency container: one instance per process, created by [HuroofiApplication]. */
class AppContainer(context: Context) {
    private val app = context.applicationContext

    val content: ContentRepository = ContentRepository.load(object : AssetReader {
        override fun read(path: String): String =
            app.assets.open(path).bufferedReader().use { it.readText() }
    })

    val progress = ProgressRepository(
        store = app.progressStore,
        stageOf = content.letters.associate { it.index to it.stage },
    )

    val sound: SoundPlayer = GatedSoundPlayer(
        backend = AndroidAudioBackend(app),
        voiceEnabled = { progress.voiceEnabled.first() },
    )
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided")
}
