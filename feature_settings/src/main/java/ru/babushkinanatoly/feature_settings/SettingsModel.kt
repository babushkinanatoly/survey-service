package ru.babushkinanatoly.feature_settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.Settings
import ru.babushkinanatoly.core_api.StringRes

internal interface SettingsModel {
    val state: StateFlow<SettingsState>
    fun onDarkTheme()
    fun onDarkThemeDismiss()
    fun onDarkThemeChange(value: Boolean?)
}

internal data class SettingsState(
    val darkTheme: Boolean?,
    val darkThemeSettingsDesc: String,
    val themeSelecting: Boolean,
)

internal class SettingsModelImpl(
    private val scope: CoroutineScope,
    private val stringRes: StringRes,
    private val settings: Settings,
) : SettingsModel {

    override val state = MutableStateFlow(
        SettingsState(
            darkTheme = settings.darkTheme.value,
            darkThemeSettingsDesc = settings.darkTheme.value.toDarkThemeSettingsDesc(),
            themeSelecting = false
        )
    )

    override fun onDarkTheme() {
        state.update { it.copy(themeSelecting = true) }
    }

    override fun onDarkThemeDismiss() {
        state.update { it.copy(themeSelecting = false) }
    }

    override fun onDarkThemeChange(value: Boolean?) {
        state.update {
            it.copy(
                darkTheme = value,
                darkThemeSettingsDesc = value.toDarkThemeSettingsDesc(),
                themeSelecting = false
            )
        }
        scope.launch {
            settings.setDarkTheme(value)
        }
    }

    private fun Boolean?.toDarkThemeSettingsDesc() = when (this) {
        true -> stringRes[R.string.dark]
        false -> stringRes[R.string.light]
        null -> stringRes[R.string.follow_system]
    }
}
