package ru.babushkinanatoly.feature_auth.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.feature_auth.R

internal interface LogInModel {
    val state: StateFlow<LogInState>
    val event: Event<LogInEvent>
    fun onLogIn()
    fun onEmailChange(email: String)
    fun onPasswordChange(password: String)
}

internal data class LogInState(
    val email: String,
    val password: String,
    val emailError: String,
    val passwordError: String,
    val loading: Boolean,
) {
    val loginEnabled = isEmailValid(email) && isPasswordValid(password)
}

internal sealed class LogInEvent {
    object Success : LogInEvent()
    data class Error(val msg: String) : LogInEvent()
}

internal class LogInModelImpl(
    private val scope: CoroutineScope,
    private val stringRes: StringRes,
    private val repo: Repo,
) : LogInModel {

    override val state = MutableStateFlow(
        LogInState(
            email = "",
            password = "",
            emailError = "",
            passwordError = "",
            loading = false
        )
    )

    override val event = MutableEvent<LogInEvent>()

    override fun onLogIn() {
        state.update { it.copy(loading = true) }
        scope.launch {
            val loginEvent = when (repo.logIn(UserLogInData(state.value.email, state.value.password))) {
                LogInResult.OK -> LogInEvent.Success
                LogInResult.INVALID_CREDENTIALS -> LogInEvent.Error(stringRes[R.string.error_invalid_credentials])
                LogInResult.CONNECTION_ERROR -> LogInEvent.Error(stringRes[R.string.error_no_connection])
            }
            event.dispatch(loginEvent)
            if (loginEvent !is LogInEvent.Success) {
                state.update { it.copy(loading = false) }
            }
        }
    }

    override fun onEmailChange(email: String) {
        state.update {
            it.copy(
                email = email,
                emailError = if (isEmailValid(email)) "" else stringRes[R.string.error_email_format],
            )
        }
    }

    override fun onPasswordChange(password: String) {
        state.update {
            it.copy(
                password = password,
                passwordError = if (isPasswordValid(password)) "" else stringRes[R.string.error_password_format],
            )
        }
    }
}

private fun isEmailValid(email: String) = email.isNotBlank()
private fun isPasswordValid(password: String) = password.isNotBlank()
