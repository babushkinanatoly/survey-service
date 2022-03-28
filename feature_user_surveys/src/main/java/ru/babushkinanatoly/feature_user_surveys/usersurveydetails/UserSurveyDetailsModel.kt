package ru.babushkinanatoly.feature_user_surveys.usersurveydetails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.feature_user_surveys.R

internal interface UserSurveyDetailsModel {
    val state: StateFlow<UserSurveyDetailsState>
    val event: Event<UserSurveyDetailsEvent>
    fun onTitleEdit()
    fun onDescEdit()
    fun onTitleChange(title: String)
    fun onDescChange(desc: String)
    fun onTitleUpdate(title: String)
    fun onDescUpdate(desc: String)
    fun onDelete()
}

internal data class UserSurveyDetailsState(
    val userSurvey: UserSurvey,
    val titleEditable: Boolean,
    val descEditable: Boolean,
    val processing: Boolean,
)

internal sealed class UserSurveyDetailsEvent {
    object Deleted : UserSurveyDetailsEvent()
    data class Error(val msg: String) : UserSurveyDetailsEvent()
}

internal class UserSurveyDetailsModelImpl(
    private val surveyId: String,
    private val scope: CoroutineScope,
    private val stringRes: StringRes,
    private val repo: Repo,
) : UserSurveyDetailsModel {

    override val state = MutableStateFlow(
        UserSurveyDetailsState(
            userSurvey = UserSurvey("0", "", "", listOf(), listOf()),
            titleEditable = false,
            descEditable = false,
            processing = false
        )
    )

    override val event = MutableEvent<UserSurveyDetailsEvent>()

    init {
        scope.launch {
            repo.getUserSurvey(surveyId).collect { userSurvey ->
                state.update { state ->
                    state.copy(
                        userSurvey = userSurvey ?: state.userSurvey,
                        processing = false
                    )
                }
            }
        }
    }

    override fun onTitleEdit() {
        state.update { it.copy(titleEditable = true) }
    }

    override fun onDescEdit() {
        state.update { it.copy(descEditable = true) }
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
        state.update { it.copy(processing = true) }
        scope.launch {
            when (val result = repo.updateUserSurveyTitle(state.value.userSurvey.id, title)) {
                is SurveyResult.Success -> {
                    state.update { it.copy(titleEditable = false) }
                }
                is SurveyResult.Error -> {
                    state.update { it.copy(processing = false) }
                    event.dispatch(UserSurveyDetailsEvent.Error(result.msg))
                }
            }
        }
    }

    override fun onDescUpdate(desc: String) {
        state.update { it.copy(processing = true) }
        scope.launch {
            when (val result = repo.updateUserSurveyDesc(state.value.userSurvey.id, desc)) {
                is SurveyResult.Success -> {
                    state.update { it.copy(descEditable = false) }
                }
                is SurveyResult.Error -> {
                    state.update { it.copy(processing = false) }
                    event.dispatch(UserSurveyDetailsEvent.Error(result.msg))
                }
            }
        }
    }

    override fun onDelete() {
        state.update { it.copy(processing = true) }
        scope.launch {
            if (repo.deleteSurvey(surveyId)) {
                event.dispatch(UserSurveyDetailsEvent.Deleted)
            } else {
                state.update { it.copy(processing = false) }
                event.dispatch(UserSurveyDetailsEvent.Error(stringRes[R.string.error_no_connection]))
            }
        }
    }
}
