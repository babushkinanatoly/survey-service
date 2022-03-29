package ru.babushkinanatoly.feature_settings.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Settings
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_settings.SettingsModel
import ru.babushkinanatoly.feature_settings.SettingsModelImpl

@Module
internal class SettingsScreenModule {

    @SettingsScreenScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        settings: Settings,
    ): SettingsModel = SettingsModelImpl(scope, stringRes, settings)
}
