package ru.babushkinanatoly.feature_settings.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.SettingsProvider
import ru.babushkinanatoly.core.StringResProvider

internal class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    val settingsComponent = DaggerSettingsScreenComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as SettingsProvider))
}
