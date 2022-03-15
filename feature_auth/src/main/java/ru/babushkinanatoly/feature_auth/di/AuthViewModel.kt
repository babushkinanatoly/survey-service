package ru.babushkinanatoly.feature_auth.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class AuthViewModel(app: Application) : AndroidViewModel(app) {

    val authComponent = DaggerAuthComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
