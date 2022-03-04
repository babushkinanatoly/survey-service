package ru.babushkinanatoly.surveyservice

import android.app.Application
import ru.babushkinanatoly.core.DaggerRepoComponent
import ru.babushkinanatoly.core.DaggerStringsComponent
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringsProvider
import ru.babushkinanatoly.surveyservice.di.DaggerAppComponent

@Suppress("unused")
class App : Application(), RepoProvider, StringsProvider {

    private val appComponent by lazy {
        DaggerAppComponent.factory().create(
            DaggerRepoComponent.factory().create(this),
            DaggerStringsComponent.factory().create(this)
        )
    }

    override fun provideRepo() = appComponent.provideRepo()
    override fun provideStrings() = appComponent.provideStrings()
}
