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
    fun onTitleChange(title: String)
    fun onDescChange(desc: String)
    fun onTitleUpdate(title: String)
    fun onDescUpdate(desc: String)
}

internal data class UserSurveyDetailsState(
    val userSurvey: UserSurvey,
)

internal class UserSurveyDetailsModelImpl(
    private val surveyId: String,
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : UserSurveyDetailsModel {

    override val state = MutableStateFlow(
        UserSurveyDetailsState(
            UserSurvey("0", "", "", listOf(), listOf())
        )
    )

    init {
        scope.launch {
            repo.getUserSurvey(surveyId).collect { userSurvey ->
                state.update { it.copy(userSurvey = userSurvey) }
            }
        }
    }

    override fun onTitleChange(title: String) {
        state.update {
            it.copy(userSurvey = it.userSurvey.copy(title = title))
        }
    }

    override fun onDescChange(desc: String) {
        state.update {
            it.copy(userSurvey = it.userSurvey.copy(desc = desc))
        }
    }

    override fun onTitleUpdate(title: String) {
        scope.launch {
            repo.updateUserSurveyTitle(state.value.userSurvey.id, title)
        }
    }

    override fun onDescUpdate(desc: String) {
        scope.launch {
            repo.updateUserSurveyDesc(state.value.userSurvey.id, desc)
        }
    }
}
