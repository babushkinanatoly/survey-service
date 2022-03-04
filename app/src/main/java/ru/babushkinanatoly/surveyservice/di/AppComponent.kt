package ru.babushkinanatoly.surveyservice.di

import dagger.Component
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringsProvider
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [
        RepoProvider::class,
        StringsProvider::class
    ]
)
interface AppComponent : RepoProvider, StringsProvider {

    @Component.Factory
    interface Factory {

        fun create(
            repoProvider: RepoProvider,
            stringsProvider: StringsProvider,
        ): AppComponent
    }
}
