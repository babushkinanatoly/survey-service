package ru.babushkinanatoly.feature_user_surveys.usersurveydetails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.core_api.UserSurvey

internal interface UserSurveyDetailsModel {
    val state: StateFlow<UserSurveyDetailsState>
}

internal data class UserSurveyDetailsState(
    val userSurvey: UserSurvey,
)

internal class UserSurveyDetailsModelImpl(
    private val surveyId: Long,
    scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : UserSurveyDetailsModel {

    override val state = MutableStateFlow(UserSurveyDetailsState(UserSurvey(0, "", "", listOf())))

    init {
        scope.launch {
            repo.getUserSurvey(surveyId).collect { userSurvey ->
                state.update { it.copy(userSurvey = userSurvey) }
            }
        }
    }
}
