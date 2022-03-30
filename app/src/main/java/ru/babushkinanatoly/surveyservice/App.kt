package ru.babushkinanatoly.surveyservice

import android.app.Application
import ru.babushkinanatoly.core.*
import ru.babushkinanatoly.surveyservice.di.DaggerAppComponent

@Suppress("unused")
class App : Application(), RepoProvider, StringResProvider, SettingsProvider {

    private val appComponent by lazy {
        DaggerAppComponent.factory().create(
            DaggerRepoComponent.factory().create(this),
            DaggerStringResComponent.factory().create(this),
            DaggerSettingsComponent.factory().create(this)
        )
    }

    val darkTheme by lazy { provideSettings().darkTheme }
    val loggedIn get() = provideRepo().currentUser.value != null

    override fun provideRepo() = appComponent.provideRepo()
    override fun provideStringRes() = appComponent.provideStringRes()
    override fun provideSettings() = appComponent.provideSettings()
}
