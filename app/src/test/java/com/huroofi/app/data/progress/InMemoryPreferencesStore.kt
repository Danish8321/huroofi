package com.huroofi.app.data.progress

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Test double for the preferences DataStore.
 *
 * The real file-backed DataStore cannot be used for repeated writes in JVM tests on Windows:
 * it commits with File.renameTo, which fails when the target file already exists. Persistence
 * itself is DataStore's job; these tests cover this app's keys, defaults and rules.
 */
class InMemoryPreferencesStore : DataStore<Preferences> {
    private val state = MutableStateFlow<Preferences>(emptyPreferences())
    private val lock = Mutex()

    override val data: Flow<Preferences> = state

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
        lock.withLock {
            val updated = transform(state.value)
            state.value = updated
            updated
        }
}
