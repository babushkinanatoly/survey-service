package ru.babushkinanatoly.surveyservice.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.babushkinanatoly.surveyservice.data.LogInResult
import ru.babushkinanatoly.surveyservice.data.Repo
import ru.babushkinanatoly.surveyservice.data.UserAuthData
import ru.babushkinanatoly.surveyservice.util.Event
import ru.babushkinanatoly.surveyservice.util.MutableEvent
import ru.babushkinanatoly.surveyservice.util.dispatch

interface AuthModel {
    val email: Flow<String>
    val password: Flow<String>
    val emailError: Flow<String>
    val passwordError: Flow<String>
    val loginEnabled: Flow<Boolean>
    val loading: Flow<Boolean>
    val loginEvent: Event<LogInEvent>
    fun onLogIn(userAuthData: UserAuthData)
    fun onEmailChange(email: String)
    fun onPasswordChange(password: String)
}

class AuthViewModel(repo: Repo) : ViewModel() {
    val authModel: AuthModel = AuthModelImpl(viewModelScope, repo)
}

class AuthViewModelFactory(private val repo: Repo) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class LogInEvent {
    object Success : LogInEvent()
    data class Error(val msg: String) : LogInEvent()
}

class AuthModelImpl(
    private val scope: CoroutineScope,
    private val repo: Repo,
) : AuthModel {

    override val email = MutableStateFlow("")
    override val password = MutableStateFlow("")

    override val emailError = MutableStateFlow("")
    override val passwordError = MutableStateFlow("")

    override val loginEnabled = combine(email, password) { values ->
        values.all { it.isNotBlank() }
    }

    override val loading = MutableStateFlow(false)

    override val loginEvent = MutableEvent<LogInEvent>()

    override fun onLogIn(userAuthData: UserAuthData) {
        loading.value = true
        scope.launch {
            loginEvent.dispatch(
                when (repo.onLogIn(userAuthData)) {
                    // TODO: Inject Strings and replace hardcoded strings
                    LogInResult.OK -> LogInEvent.Success
                    LogInResult.INVALID_CREDENTIALS -> LogInEvent.Error("Invalid email or password")
                    LogInResult.CONNECTION_ERROR -> LogInEvent.Error("Check internet connection or try again later")
                }
            )
            loading.value = false
        }
    }

    override fun onEmailChange(email: String) {
        this.email.value = email
        emailError.value = (if (emailValid()) "" else "Illegal email format")
    }

    override fun onPasswordChange(password: String) {
        this.password.value = password
        passwordError.value = (if (passwordValid()) "" else "Illegal password format")
    }

    private fun emailValid() = email.value.isNotBlank()
    private fun passwordValid() = password.value.isNotBlank()
}
