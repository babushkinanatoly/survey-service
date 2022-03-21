package ru.babushkinanatoly.feature_survey_feed.surveyfeed

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.core_api.Survey
import ru.babushkinanatoly.core_api.SurveysResult

internal interface SurveyFeedModel {
    val state: StateFlow<SurveyFeedState>
    fun reloadSurveys()
}

internal sealed class SurveyFeedState {
    data class Surveys(val data: List<Survey>) : SurveyFeedState()
    object Loading : SurveyFeedState()
    object LoadingError : SurveyFeedState()
}

internal class SurveyFeedModelImpl(
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : SurveyFeedModel {

    override val state = MutableStateFlow<SurveyFeedState>(SurveyFeedState.Loading)

    init {
        reloadSurveys()
    }

    override fun reloadSurveys() {
        state.update { SurveyFeedState.Loading }
        scope.launch {
            when (val result = repo.getSurveys()) {
                is SurveysResult.Success -> state.update { SurveyFeedState.Surveys(result.surveys) }
                SurveysResult.Error -> state.update { SurveyFeedState.LoadingError }
            }
        }
    }
}
