package ru.babushkinanatoly.feature_profile.profile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*

internal interface ProfileModel {
    val state: StateFlow<ProfileState>
    val event: Event<ProfileEvent>
    fun onLogOut()
    fun onSaveChanges()
    fun onClearChanges()
    fun onAge()
    fun onAgeChange(age: String)
    fun onAgeDismiss()
    fun onSex()
    fun onSexChange(sex: String)
    fun onSexDismiss()
    fun onCountry()
    fun onCountryCodeChange(countryCode: String)
    fun onCountryDismiss()
    fun onNameChange(name: String)
}

internal data class ProfileState(
    val name: String,
    val email: String,
    val age: String,
    val ageSelecting: Boolean,
    val sex: String,
    val sexSelecting: Boolean,
    val countryCode: String,
    val countrySelecting: Boolean,
    val dataChanged: Boolean,
    val saving: Boolean,
)

internal enum class ProfileEvent {
    LOGGED_OUT, CRITICAL_ERROR, ERROR
}

internal class ProfileModelImpl(
    private val scope: CoroutineScope,
    stringRes: StringRes,
    private val repo: Repo,
) : ProfileModel {

    private val profileData
        get() = UserProfileData(
            name = state.value.name,
            age = state.value.age.toInt(),
            sex = state.value.sex,
            countryCode = state.value.countryCode
        )

    private val emptyState = ProfileState(
        name = "",
        email = "",
        age = "",
        ageSelecting = false,
        sex = "",
        sexSelecting = false,
        countryCode = "",
        countrySelecting = false,
        dataChanged = false,
        saving = false
    )

    private val savedState = MutableStateFlow(emptyState)

    override val state = MutableStateFlow(emptyState)

    override val event = MutableEvent<ProfileEvent>()

    init {
        scope.launch {
            repo.currentUser.value?.let { user ->
                val currentState = ProfileState(
                    name = user.name,
                    email = user.email,
                    age = user.age.toString(),
                    ageSelecting = false,
                    sex = user.sex,
                    sexSelecting = false,
                    countryCode = user.country,
                    countrySelecting = false,
                    dataChanged = false,
                    saving = false
                )
                state.update { currentState }
                savedState.update { currentState }
            }
        }
    }

    override fun onLogOut() {
        scope.launch(Dispatchers.Default) {
            if (repo.logOut()) {
                event.dispatch(ProfileEvent.LOGGED_OUT)
            } else {
                event.dispatch(ProfileEvent.CRITICAL_ERROR)
            }
        }
    }

    override fun onSaveChanges() {
        state.update { it.copy(saving = true) }
        scope.launch {
            if (!repo.updateUser(profileData)) {
                event.dispatch(ProfileEvent.ERROR)
                state.update { it.copy(saving = false) }
            } else {
                state.update {
                    it.copy(
                        dataChanged = false,
                        saving = false
                    )
                }
                savedState.update { state.value }
            }
        }
    }

    override fun onClearChanges() {
        state.update { savedState.value }
    }

    override fun onAge() {
        state.update { it.copy(ageSelecting = !it.ageSelecting) }
    }

    override fun onAgeChange(age: String) {
        val dataChanged = savedState.value.age != age
        state.update {
            it.copy(
                age = age,
                ageSelecting = false,
                dataChanged = dataChanged
            )
        }
    }

    override fun onAgeDismiss() {
        state.update { it.copy(ageSelecting = false) }
    }

    override fun onSex() {
        state.update { it.copy(sexSelecting = !it.sexSelecting) }
    }

    override fun onSexChange(sex: String) {
        val dataChanged = savedState.value.sex != sex
        state.update {
            it.copy(
                sex = sex,
                sexSelecting = false,
                dataChanged = dataChanged
            )
        }
    }

    override fun onSexDismiss() {
        state.update { it.copy(sexSelecting = false) }
    }

    override fun onCountry() {
        state.update { it.copy(countrySelecting = !it.countrySelecting) }
    }

    override fun onCountryCodeChange(countryCode: String) {
        val dataChanged = savedState.value.countryCode != countryCode
        state.update {
            it.copy(
                countryCode = countryCode,
                countrySelecting = false,
                dataChanged = dataChanged
            )
        }
    }

    override fun onCountryDismiss() {
        state.update { it.copy(countrySelecting = false) }
    }

    override fun onNameChange(name: String) {
        val dataChanged = savedState.value.name != name
        state.update {
            it.copy(
                name = name,
                dataChanged = dataChanged
            )
        }
    }
}
