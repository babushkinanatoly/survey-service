package ru.babushkinanatoly.feature_profile.profile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*

internal interface ProfileModel {
    val event: Event<ProfileEvent>
    fun onLogOut()
}

internal enum class ProfileEvent {
    LOGGED_OUT, ERROR
}

internal class ProfileModelImpl(
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : ProfileModel {

    override val event = MutableEvent<ProfileEvent>()

    override fun onLogOut() {
        scope.launch(Dispatchers.Default) {
            if (repo.logOut()) {
                event.dispatch(ProfileEvent.LOGGED_OUT)
            } else {
                event.dispatch(ProfileEvent.ERROR)
            }
        }
    }
}
