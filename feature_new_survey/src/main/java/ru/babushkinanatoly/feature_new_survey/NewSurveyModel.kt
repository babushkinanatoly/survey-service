package ru.babushkinanatoly.feature_new_survey

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*

internal interface NewSurveyModel {
    val state: StateFlow<NewSurveyState>
    val event: Event<NewSurveyEvent>
    fun onTitleChange(title: String)
    fun onDescChange(desc: String)
    fun onCreate()
}

internal data class NewSurveyState(
    val title: String,
    val desc: String,
    val titleError: String,
    val descError: String,
    val creating: Boolean,
) {
    val createEnabled = isTitleValid(title) && isDescValid(desc)
}

internal enum class NewSurveyEvent {
    CREATED, ERROR
}

internal class NewSurveyModelImpl(
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : NewSurveyModel {

    private val titleError = stringRes[R.string.error_title_format]
    private val descError = stringRes[R.string.error_desc_format]

    override val state = MutableStateFlow(
        NewSurveyState(
            title = "",
            desc = "",
            titleError = titleError,
            descError = descError,
            creating = false
        )
    )

    override val event = MutableEvent<NewSurveyEvent>()

    override fun onTitleChange(title: String) {
        state.update {
            it.copy(
                title = title,
                titleError = if (isTitleValid(title)) "" else titleError
            )
        }
    }

    override fun onDescChange(desc: String) {
        state.update {
            it.copy(
                desc = desc,
                descError = if (isDescValid(desc)) "" else descError
            )
        }
    }

    override fun onCreate() {
        state.update { it.copy(creating = true) }
        scope.launch {
            if (repo.addSurvey(state.value.title, state.value.desc)) {
                event.dispatch(NewSurveyEvent.CREATED)
            } else {
                event.dispatch(NewSurveyEvent.ERROR)
                state.update { it.copy(creating = false) }
            }
        }
    }
}

private fun isTitleValid(title: String) = title.isNotBlank()
private fun isDescValid(desc: String) = desc.isNotBlank()
