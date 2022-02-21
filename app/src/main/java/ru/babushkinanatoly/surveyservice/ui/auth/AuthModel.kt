package ru.babushkinanatoly.surveyservice.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.surveyservice.data.LogInResult
import ru.babushkinanatoly.surveyservice.data.Repo
import ru.babushkinanatoly.surveyservice.data.UserAuthData
import ru.babushkinanatoly.surveyservice.util.Event
import ru.babushkinanatoly.surveyservice.util.MutableEvent
import ru.babushkinanatoly.surveyservice.util.dispatch

interface AuthModel {
    val state: StateFlow<AuthState>
    val loginEvent: Event<LogInEvent>
    fun onLogIn(userAuthData: UserAuthData)
    fun onEmailChange(email: String)
    fun onPasswordChange(password: String)
}

data class AuthState(
    val email: String,
    val password: String,
    val emailError: String,
    val passwordError: String,
    val loginEnabled: Boolean,
    val loading: Boolean,
)

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

    override val state = MutableStateFlow(
        AuthState(
            email = "",
            password = "",
            emailError = "",
            passwordError = "",
            loginEnabled = false,
            loading = false
        )
    )

    override val loginEvent = MutableEvent<LogInEvent>()

    override fun onLogIn(userAuthData: UserAuthData) {
        state.update { it.copy(loading = true) }
        scope.launch {
            loginEvent.dispatch(
                when (repo.onLogIn(userAuthData)) {
                    // TODO: Inject Strings and replace hardcoded strings
                    LogInResult.OK -> LogInEvent.Success
                    LogInResult.INVALID_CREDENTIALS -> LogInEvent.Error("Invalid email or password")
                    LogInResult.CONNECTION_ERROR -> LogInEvent.Error("Check internet connection or try again later")
                }
            )
            state.update { it.copy(loading = false) }
        }
    }

    override fun onEmailChange(email: String) {
        state.update {
            it.copy(
                email = email,
                emailError = (if (isEmailValid(email)) "" else "Illegal email format"),
                loginEnabled = isEmailValid(email) && isPasswordValid(it.password)
            )
        }
    }

    override fun onPasswordChange(password: String) {
        state.update {
            it.copy(
                password = password,
                passwordError = (if (isPasswordValid(password)) "" else "Illegal password format"),
                loginEnabled = isPasswordValid(password) && isEmailValid(it.email)
            )
        }
    }

    private fun isEmailValid(email: String) = email.isNotBlank()
    private fun isPasswordValid(password: String) = password.isNotBlank()
}
