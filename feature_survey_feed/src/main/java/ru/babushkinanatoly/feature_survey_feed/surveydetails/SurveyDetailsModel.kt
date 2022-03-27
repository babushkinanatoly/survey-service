package ru.babushkinanatoly.feature_survey_feed.surveydetails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*

internal interface SurveyDetailsModel {
    val state: StateFlow<SurveyDetailsState>
    val event: Event<SurveyDetailsEvent>
    fun onReloadSurvey()
    fun onYes()
    fun onNo()
}

internal sealed class SurveyDetailsState {
    data class Data(
        val survey: Survey,
        val voting: Boolean,
    ) : SurveyDetailsState()

    object Loading : SurveyDetailsState()
    object LoadingError : SurveyDetailsState()
}

internal sealed class SurveyDetailsEvent {
    data class Error(val msg: String) : SurveyDetailsEvent()
}

internal class SurveyDetailsModelImpl(
    private val surveyId: String,
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : SurveyDetailsModel {

    override val state = MutableStateFlow<SurveyDetailsState>(SurveyDetailsState.Loading)

    override val event = MutableEvent<SurveyDetailsEvent>()

    init {
        onReloadSurvey()
    }

    override fun onReloadSurvey() {
        state.update { SurveyDetailsState.Loading }
        scope.launch {
            reloadSurvey()
        }
    }

    override fun onYes() = onVote(true)
    override fun onNo() = onVote(false)

    private fun onVote(value: Boolean) {
        var desiredValue: Boolean? = null
        state.update {
            (it as SurveyDetailsState.Data).apply {
                desiredValue = if (survey.userVote == value) null else value
            }.copy(voting = true)
        }
        scope.launch {
            when (val result = repo.updateSurveyVote(surveyId, desiredValue)) {
                is SurveyResult.Success -> {
                    state.update {
                        (it as SurveyDetailsState.Data).copy(survey = result.survey, voting = false)
                    }
                }
                is SurveyResult.Error -> {
                    state.update { (it as SurveyDetailsState.Data).copy(voting = false) }
                    event.dispatch(SurveyDetailsEvent.Error(result.msg))
                }
            }
        }
    }

    private suspend fun reloadSurvey() {
        when (val result = repo.getSurvey(surveyId)) {
            is SurveyResult.Success -> state.update {
                SurveyDetailsState.Data(
                    survey = result.survey,
                    voting = false
                )
            }
            is SurveyResult.Error -> {
                state.update { SurveyDetailsState.LoadingError }
                event.dispatch(SurveyDetailsEvent.Error(result.msg))
            }
        }
    }
}
