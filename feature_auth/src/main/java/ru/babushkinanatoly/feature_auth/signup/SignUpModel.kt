package ru.babushkinanatoly.feature_auth.signup

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.feature_auth.R

internal interface SignUpModel {
    val state: StateFlow<SignUpState>
    val event: Event<SignUpEvent>
    fun onSignUp()
    fun onAge()
    fun onAgeChange(age: String)
    fun onAgeDismiss()
    fun onSex()
    fun onSexChange(sex: String)
    fun onSexDismiss()
    fun onCountry()
    fun onCountryChange(country: String)
    fun onCountryDismiss()
    fun onNameChange(name: String)
    fun onEmailChange(email: String)
    fun onPasswordChange(password: String)
}

internal data class SignUpState(
    val name: String,
    val email: String,
    val password: String,
    val age: String,
    val ageSelecting: Boolean,
    val sex: String,
    val sexSelecting: Boolean,
    val country: String,
    val countrySelecting: Boolean,
    val nameError: String,
    val emailError: String,
    val passwordError: String,
    val loading: Boolean,
) {
    val signUpEnabled = isNameValid(name) && isEmailValid(email) && isPasswordValid(password)
}

internal sealed class SignUpEvent {
    object Success : SignUpEvent()
    data class Error(val msg: String) : SignUpEvent()
}

internal class SignUpModelImpl(
    private val scope: CoroutineScope,
    private val stringRes: StringRes,
    private val repo: Repo,
) : SignUpModel {

    private val signUpData
        get() = UserSignUpData(
            name = state.value.name,
            email = state.value.email,
            password = state.value.password,
            age = state.value.age.toInt(),
            sex = state.value.sex,
            country = state.value.country
        )

    override val state = MutableStateFlow(
        SignUpState(
            name = "",
            email = "",
            password = "",
            age = "18",
            ageSelecting = false,
            sex = "Male",
            sexSelecting = false,
            country = "RU",
            countrySelecting = false,
            nameError = "",
            emailError = "",
            passwordError = "",
            loading = false
        )
    )

    override val event = MutableEvent<SignUpEvent>()

    override fun onSignUp() {
        state.update { it.copy(loading = true) }
        scope.launch {
            val signUpEvent = when (repo.signUp(signUpData)) {
                SignUpResult.OK -> SignUpEvent.Success
                SignUpResult.USER_ALREADY_EXISTS ->
                    SignUpEvent.Error(stringRes[R.string.error_user_already_exists])
                SignUpResult.CONNECTION_ERROR ->
                    SignUpEvent.Error(stringRes[R.string.error_no_connection])
            }
            event.dispatch(signUpEvent)
            if (signUpEvent !is SignUpEvent.Success) {
                state.update { it.copy(loading = false) }
            }
        }
    }

    override fun onAge() {
        state.update { it.copy(ageSelecting = !it.ageSelecting) }
    }

    override fun onAgeChange(age: String) {
        state.update {
            it.copy(
                age = age,
                ageSelecting = false
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
        state.update {
            it.copy(
                sex = sex,
                sexSelecting = false
            )
        }
    }

    override fun onSexDismiss() {
        state.update { it.copy(sexSelecting = false) }
    }

    override fun onCountry() {
        state.update { it.copy(countrySelecting = !it.countrySelecting) }
    }

    override fun onCountryChange(country: String) {
        state.update {
            it.copy(
                country = country,
                countrySelecting = false
            )
        }
    }

    override fun onCountryDismiss() {
        state.update { it.copy(countrySelecting = false) }
    }

    override fun onNameChange(name: String) {
        state.update {
            it.copy(
                name = name,
                nameError = if (isNameValid(name)) "" else stringRes[R.string.error_name_format],
            )
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

private fun isNameValid(name: String) = name.isNotBlank()
private fun isEmailValid(email: String) = email.isNotBlank()
private fun isPasswordValid(password: String) = password.isNotBlank()
