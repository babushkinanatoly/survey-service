package ru.babushkinanatoly.feature_survey_feed.surveydetails.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class SurveyDetailsViewModel(private val app: Application) : AndroidViewModel(app) {

    private var surveyDetailsComponent: SurveyDetailsComponent? = null

    fun getSurveyDetailsComponent(surveyId: Long): SurveyDetailsComponent {
        return surveyDetailsComponent ?: DaggerSurveyDetailsComponent.factory()
            .create(surveyId, viewModelScope, (app as StringResProvider), (app as RepoProvider))
            .also { surveyDetailsComponent = it }
    }
}
