package ru.babushkinanatoly.feature_new_survey.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class NewSurveyViewModel(app: Application) : AndroidViewModel(app) {

    val newSurveyComponent = DaggerNewSurveyComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
