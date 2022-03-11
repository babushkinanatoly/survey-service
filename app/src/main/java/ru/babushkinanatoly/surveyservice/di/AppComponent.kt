package ru.babushkinanatoly.surveyservice.di

import dagger.Component
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ]
)
interface AppComponent : RepoProvider, StringResProvider {

    @Component.Factory
    interface Factory {

        fun create(
            repoProvider: RepoProvider,
            stringResProvider: StringResProvider,
        ): AppComponent
    }
}
