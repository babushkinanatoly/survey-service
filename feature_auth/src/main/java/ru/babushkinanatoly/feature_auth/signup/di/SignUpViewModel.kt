package ru.babushkinanatoly.feature_auth.signup.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class SignUpViewModel(app: Application) : AndroidViewModel(app) {

    val signUpComponent = DaggerSignUpComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
