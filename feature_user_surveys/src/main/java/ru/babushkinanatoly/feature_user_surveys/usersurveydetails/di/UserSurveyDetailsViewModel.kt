package ru.babushkinanatoly.feature_user_surveys.usersurveydetails.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class UserSurveyDetailsViewModel(private val app: Application) : AndroidViewModel(app) {

    private var userSurveyDetailsComponent: UserSurveyDetailsComponent? = null

    fun getUserSurveyDetailsComponent(surveyId: String): UserSurveyDetailsComponent {
        return userSurveyDetailsComponent ?: DaggerUserSurveyDetailsComponent.factory()
            .create(surveyId, viewModelScope, (app as StringResProvider), (app as RepoProvider))
            .also { userSurveyDetailsComponent = it }
    }
}
