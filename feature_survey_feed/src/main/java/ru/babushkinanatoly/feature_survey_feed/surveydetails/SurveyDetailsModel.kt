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

internal class SurveyDetailsModelImpl(
    private val surveyId: Long,
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : SurveyDetailsModel {

    override val state = MutableStateFlow<SurveyDetailsState>(SurveyDetailsState.Loading)

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
        var voteId: Long? = null
        state.update { state ->
            (state as SurveyDetailsState.Data).let {
                it.survey.userVote?.let { userVote -> voteId = userVote.id }
                it.copy(voting = true)
            }
        }
        scope.launch {
            // TODO: add error status
            // TODO: return result from repo and change the status depending on it
            repo.updateSurveyVote(surveyId, voteId, value)
            // TODO: remove later
            reloadSurvey()
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
            SurveyResult.Error -> state.update { SurveyDetailsState.LoadingError }
        }
    }
}
