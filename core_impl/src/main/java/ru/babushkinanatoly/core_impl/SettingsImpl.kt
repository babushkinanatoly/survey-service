package ru.babushkinanatoly.core_impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import ru.babushkinanatoly.core_api.Settings
import ru.babushkinanatoly.core_api.Settings.AppTheme
import ru.babushkinanatoly.core_api.Settings.AppTheme.*
import ru.babushkinanatoly.core_impl.SettingsImpl.PreferencesKeys.APP_THEME

class SettingsImpl(private val context: Context) : Settings {

    companion object {
        private const val USER_PREFS_NAME = "user"
        private const val THEME_DARK = "dark"
        private const val THEME_LIGHT = "light"
        private const val THEME_FOLLOW_SYSTEM = "follow_system"
    }

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("prefs.app_theme")
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val Context.dataStore by preferencesDataStore(USER_PREFS_NAME)

    private val appThemeValue
        get() = runBlocking {
            context.dataStore.data.first()
        }[APP_THEME]?.toAppThemeValue() ?: FOLLOW_SYSTEM

    override val appTheme = context.dataStore.data
        .map { it[APP_THEME]?.toAppThemeValue() ?: FOLLOW_SYSTEM }
        .stateIn(scope, SharingStarted.WhileSubscribed(), appThemeValue)

    override suspend fun setAppTheme(appTheme: AppTheme) {
        context.dataStore.edit {
            it[APP_THEME] = appTheme.toAppThemePref()
        }
    }

    private fun AppTheme.toAppThemePref() = when (this) {
        DARK -> THEME_DARK
        LIGHT -> THEME_LIGHT
        FOLLOW_SYSTEM -> THEME_FOLLOW_SYSTEM
    }

    private fun String.toAppThemeValue() = when (this) {
        THEME_DARK -> DARK
        THEME_LIGHT -> LIGHT
        THEME_FOLLOW_SYSTEM -> FOLLOW_SYSTEM
        else -> error("Unexpected app theme pref: $this")
    }
}
