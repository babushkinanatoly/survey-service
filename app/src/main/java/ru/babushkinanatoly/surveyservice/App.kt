package ru.babushkinanatoly.surveyservice

import android.app.Application
import kotlinx.coroutines.flow.map
import ru.babushkinanatoly.core.DaggerRepoComponent
import ru.babushkinanatoly.core.DaggerStringResComponent
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.surveyservice.di.DaggerAppComponent

@Suppress("unused")
class App : Application(), RepoProvider, StringResProvider {

    private val appComponent by lazy {
        DaggerAppComponent.factory().create(
            DaggerRepoComponent.factory().create(this),
            DaggerStringResComponent.factory().create(this)
        )
    }

    val loggedIn by lazy {
        appComponent.provideRepo().currentUser.map { it != null }
    }

    override fun provideRepo() = appComponent.provideRepo()
    override fun provideStringRes() = appComponent.provideStringRes()
}
