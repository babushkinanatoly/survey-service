package ru.babushkinanatoly.core_api

import kotlinx.coroutines.flow.StateFlow

interface Settings {
    val appTheme: StateFlow<AppTheme>
    suspend fun setAppTheme(appTheme: AppTheme)

    enum class AppTheme {
        DARK, LIGHT, FOLLOW_SYSTEM
    }
}
