package ru.babushkinanatoly.feature_survey_feed.surveyfeed.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class SurveyFeedViewModel(app: Application) : AndroidViewModel(app) {

    val surveyFeedComponent = DaggerSurveyFeedComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
