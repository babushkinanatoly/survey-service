package ru.babushkinanatoly.feature_auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.base_feature.util.Event
import ru.babushkinanatoly.base_feature.util.MutableEvent
import ru.babushkinanatoly.base_feature.util.dispatch
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringsProvider
import ru.babushkinanatoly.core_api.LogInResult
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.Strings
import ru.babushkinanatoly.core_api.UserAuthData
import ru.babushkinanatoly.feature_auth.di.DaggerAuthComponent

interface AuthModel {
    val state: StateFlow<AuthState>
    val loginEvent: Event<LogInEvent>
    fun onLogIn()
    fun onEmailChange(email: String)
    fun onPasswordChange(password: String)
}

data class AuthState(
    val email: String,
    val password: String,
    val emailError: String,
    val passwordError: String,
    val loading: Boolean,
) {
    val loginEnabled = isEmailValid(email) && isPasswordValid(password)
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    val authComponent = DaggerAuthComponent.factory()
        .create(viewModelScope, (app as StringsProvider), (app as RepoProvider))
}

sealed class LogInEvent {
    object Success : LogInEvent()
    data class Error(val msg: String) : LogInEvent()
}

class AuthModelImpl(
    private val scope: CoroutineScope,
    private val strings: Strings,
    private val repo: Repo,
) : AuthModel {

    override val state = MutableStateFlow(
        AuthState(
            email = "",
            password = "",
            emailError = "",
            passwordError = "",
            loading = false
        )
    )

    override val loginEvent = MutableEvent<LogInEvent>()

    override fun onLogIn() {
        state.update { it.copy(loading = true) }
        scope.launch {
            val event = when (repo.onLogIn(UserAuthData(state.value.email, state.value.password))) {
                LogInResult.OK -> LogInEvent.Success
                LogInResult.INVALID_CREDENTIALS -> LogInEvent.Error(strings[R.string.error_invalid_credentials])
                LogInResult.CONNECTION_ERROR -> LogInEvent.Error(strings[R.string.error_no_connection])
            }
            loginEvent.dispatch(event)
            state.update { it.copy(loading = false) }
        }
    }

    override fun onEmailChange(email: String) {
        state.update {
            it.copy(
                email = email,
                emailError = (if (isEmailValid(email)) "" else strings[R.string.error_email_format]),
            )
        }
    }

    override fun onPasswordChange(password: String) {
        state.update {
            it.copy(
                password = password,
                passwordError = (if (isPasswordValid(password)) "" else strings[R.string.error_password_format]),
            )
        }
    }
}

private fun isEmailValid(email: String) = email.isNotBlank()
private fun isPasswordValid(password: String) = password.isNotBlank()
