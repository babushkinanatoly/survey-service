package ru.babushkinanatoly.core_api

import kotlinx.coroutines.flow.StateFlow

interface Settings {
    val darkTheme: StateFlow<Boolean?>
    suspend fun setDarkTheme(value: Boolean?)
}
