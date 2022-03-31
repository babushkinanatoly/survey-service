package ru.babushkinanatoly.feature_auth.login.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class LogInViewModel(app: Application) : AndroidViewModel(app) {

    val logInComponent = DaggerLogInComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
