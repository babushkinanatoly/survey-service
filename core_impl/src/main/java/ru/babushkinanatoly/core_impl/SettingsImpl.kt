package ru.babushkinanatoly.core_impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import ru.babushkinanatoly.core_api.Settings
import ru.babushkinanatoly.core_impl.SettingsImpl.PreferencesKeys.DARK_THEME

class SettingsImpl(private val context: Context) : Settings {

    companion object {
        private const val USER_PREFS_NAME = "user"
    }

    private object PreferencesKeys {
        val DARK_THEME = intPreferencesKey("prefs.dark_theme")
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val Context.dataStore by preferencesDataStore(USER_PREFS_NAME)

    private val darkThemeValue
        get() = runBlocking {
            context.dataStore.data.first()
        }[DARK_THEME]?.toDarkThemeValue() ?: false

    override val darkTheme = context.dataStore.data
        .map { it[DARK_THEME]?.toDarkThemeValue() ?: false }
        .stateIn(scope, SharingStarted.WhileSubscribed(), darkThemeValue)

    override suspend fun setDarkTheme(value: Boolean?) {
        context.dataStore.edit {
            it[DARK_THEME] = value.toDarkThemePref()
        }
    }
}

private fun Boolean?.toDarkThemePref() = when (this) {
    true -> 1
    false -> -1
    null -> 0
}

private fun Int.toDarkThemeValue() = when (this) {
    1 -> true
    -1 -> false
    0 -> null
    else -> error("Unexpected dark theme pref: $this")
}
