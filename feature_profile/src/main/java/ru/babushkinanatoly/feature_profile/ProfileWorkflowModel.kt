package ru.babushkinanatoly.feature_profile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*

internal interface ProfileWorkflowModel {
    val event: Event<ProfileWorkflowEvent>
    fun onLogOut()
}

internal enum class ProfileWorkflowEvent {
    LOGGED_OUT, ERROR
}

internal class ProfileWorkflowModelImpl(
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : ProfileWorkflowModel {

    override val event = MutableEvent<ProfileWorkflowEvent>()

    override fun onLogOut() {
        scope.launch(Dispatchers.Default) {
            if (repo.logOut()) {
                event.dispatch(ProfileWorkflowEvent.LOGGED_OUT)
            } else {
                event.dispatch(ProfileWorkflowEvent.ERROR)
            }
        }
    }
}
