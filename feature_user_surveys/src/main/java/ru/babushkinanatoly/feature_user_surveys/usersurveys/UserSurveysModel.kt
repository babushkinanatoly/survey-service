package ru.babushkinanatoly.feature_user_surveys.usersurveys

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.core_api.UserSurvey

interface UserSurveysModel {
    val state: StateFlow<UserSurveysState>
}

data class UserSurveysState(
    val surveys: List<UserSurvey>,
)

class UserSurveysModelImpl(
    scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : UserSurveysModel {

    override val state = MutableStateFlow(UserSurveysState(listOf()))

    init {
        scope.launch {
            repo.getUserSurveys().collect { userSurveys ->
                state.update { it.copy(surveys = userSurveys) }
            }
        }
    }
}
