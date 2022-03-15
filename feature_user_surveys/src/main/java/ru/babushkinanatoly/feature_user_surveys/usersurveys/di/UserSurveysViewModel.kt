package ru.babushkinanatoly.feature_user_surveys.usersurveys.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class UserSurveysViewModel(app: Application) : AndroidViewModel(app) {

    val userSurveysComponent = DaggerUserSurveysComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
