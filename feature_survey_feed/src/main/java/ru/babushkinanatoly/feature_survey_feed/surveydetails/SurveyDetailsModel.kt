package ru.babushkinanatoly.feature_survey_feed.surveydetails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.core_api.Survey
import ru.babushkinanatoly.core_api.SurveyResult

internal interface SurveyDetailsModel {
    val state: StateFlow<SurveyDetailsState>
    fun reloadSurvey()
}

internal sealed class SurveyDetailsState {
    data class Data(val survey: Survey) : SurveyDetailsState()
    object Loading : SurveyDetailsState()
    object LoadingError : SurveyDetailsState()
}

internal class SurveyDetailsModelImpl(
    private val surveyId: Long,
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : SurveyDetailsModel {

    override val state = MutableStateFlow<SurveyDetailsState>(SurveyDetailsState.Loading)

    init {
        reloadSurvey()
    }

    override fun reloadSurvey() {
        state.update { SurveyDetailsState.Loading }
        scope.launch {
            when (val result = repo.getSurvey(surveyId)) {
                is SurveyResult.Success -> state.update { SurveyDetailsState.Data(result.survey) }
                SurveyResult.Error -> state.update { SurveyDetailsState.LoadingError }
            }
        }
    }
}
