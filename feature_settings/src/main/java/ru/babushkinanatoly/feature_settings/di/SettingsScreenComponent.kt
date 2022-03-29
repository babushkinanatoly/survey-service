package ru.babushkinanatoly.feature_settings.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.SettingsProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_settings.SettingsModel

@SettingsScreenScope
@Component(
    dependencies = [
        SettingsProvider::class,
        StringResProvider::class
    ],
    modules = [SettingsScreenModule::class]
)
internal interface SettingsScreenComponent {

    fun provideModel(): SettingsModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            settingsProvider: SettingsProvider,
        ): SettingsScreenComponent
    }
}
