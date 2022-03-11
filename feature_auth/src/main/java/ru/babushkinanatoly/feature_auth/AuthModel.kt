package ru.babushkinanatoly.feature_auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.base_feature.util.Event
import ru.babushkinanatoly.base_feature.util.MutableEvent
import ru.babushkinanatoly.base_feature.util.dispatch
import ru.babushkinanatoly.core_api.LogInResult
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.core_api.UserAuthData

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

sealed class LogInEvent {
    object Success : LogInEvent()
    data class Error(val msg: String) : LogInEvent()
}

class AuthModelImpl(
    private val scope: CoroutineScope,
    private val stringRes: StringRes,
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
                LogInResult.INVALID_CREDENTIALS -> LogInEvent.Error(stringRes[R.string.error_invalid_credentials])
                LogInResult.CONNECTION_ERROR -> LogInEvent.Error(stringRes[R.string.error_no_connection])
            }
            loginEvent.dispatch(event)
            if (event !is LogInEvent.Success) {
                state.update { it.copy(loading = false) }
            }
        }
    }

    override fun onEmailChange(email: String) {
        state.update {
            it.copy(
                email = email,
                emailError = (if (isEmailValid(email)) "" else stringRes[R.string.error_email_format]),
            )
        }
    }

    override fun onPasswordChange(password: String) {
        state.update {
            it.copy(
                password = password,
                passwordError = (if (isPasswordValid(password)) "" else stringRes[R.string.error_password_format]),
            )
        }
    }
}

private fun isEmailValid(email: String) = email.isNotBlank()
private fun isPasswordValid(password: String) = password.isNotBlank()
