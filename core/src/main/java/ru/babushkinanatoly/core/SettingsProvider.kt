package ru.babushkinanatoly.core

import ru.babushkinanatoly.core_api.Settings

interface SettingsProvider {
    fun provideSettings(): Settings
}
