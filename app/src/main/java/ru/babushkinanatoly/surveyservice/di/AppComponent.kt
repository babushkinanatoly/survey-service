package ru.babushkinanatoly.surveyservice.di

import dagger.Component
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.SettingsProvider
import ru.babushkinanatoly.core.StringResProvider
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class,
        SettingsProvider::class
    ]
)
interface AppComponent : RepoProvider, StringResProvider, SettingsProvider {

    @Component.Factory
    interface Factory {

        fun create(
            repoProvider: RepoProvider,
            stringResProvider: StringResProvider,
            settingsProvider: SettingsProvider,
        ): AppComponent
    }
}
