package ru.babushkinanatoly.feature_settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.Settings
import ru.babushkinanatoly.core_api.Settings.AppTheme
import ru.babushkinanatoly.core_api.Settings.AppTheme.*
import ru.babushkinanatoly.core_api.StringRes

internal interface SettingsModel {
    val state: StateFlow<SettingsState>
    fun onAppTheme()
    fun onAppThemeDismiss()
    fun onAppThemeChange(appTheme: AppTheme)
}

internal data class SettingsState(
    val appTheme: AppTheme,
    val appThemeSettingsDesc: String,
    val themeSelecting: Boolean,
)

internal class SettingsModelImpl(
    private val scope: CoroutineScope,
    private val stringRes: StringRes,
    private val settings: Settings,
) : SettingsModel {

    override val state = MutableStateFlow(
        SettingsState(
            appTheme = settings.appTheme.value,
            appThemeSettingsDesc = settings.appTheme.value.toAppThemeSettingsDesc(),
            themeSelecting = false
        )
    )

    init {
        scope.launch {
            settings.appTheme.collect { theme ->
                state.update {
                    it.copy(
                        appTheme = theme,
                        appThemeSettingsDesc = theme.toAppThemeSettingsDesc(),
                        themeSelecting = false
                    )
                }
            }
        }
    }

    override fun onAppTheme() {
        state.update { it.copy(themeSelecting = true) }
    }

    override fun onAppThemeDismiss() {
        state.update { it.copy(themeSelecting = false) }
    }

    override fun onAppThemeChange(appTheme: AppTheme) {
        scope.launch {
            settings.setAppTheme(appTheme)
        }
    }

    private fun AppTheme.toAppThemeSettingsDesc() = when (this) {
        DARK -> stringRes[R.string.dark]
        LIGHT -> stringRes[R.string.light]
        FOLLOW_SYSTEM -> stringRes[R.string.follow_system]
    }
}
