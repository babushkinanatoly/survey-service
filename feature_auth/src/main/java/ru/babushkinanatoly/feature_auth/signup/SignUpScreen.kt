package ru.babushkinanatoly.feature_auth.signup

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.DropDownMenu
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.feature_auth.*
import ru.babushkinanatoly.feature_auth.R

@Composable
internal fun SignUpScreen(
    signUpModel: SignUpModel,
    onLogIn: () -> Unit,
    onSignUpSuccess: () -> Unit,
) {
    val state by signUpModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val ages = context.resources.getStringArray(R.array.age_values).toList()
    val sexes = context.resources.getStringArray(R.array.sex_values).toList()
    val countries = context.resources.getStringArray(R.array.country_values).toList()
    Surface {
        CompositionLocalProvider(
            LocalOverScrollConfiguration provides null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .imePadding()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthHeader(text = stringResource(R.string.sign_up_header))
                    NameField(
                        text = state.name,
                        error = state.nameError,
                        isError = state.nameError.isNotBlank(),
                        onValueChange = signUpModel::onNameChange
                    )
                    EmailField(
                        text = state.email,
                        error = state.emailError,
                        isError = state.emailError.isNotBlank(),
                        onValueChange = signUpModel::onEmailChange
                    )
                    PasswordField(
                        text = state.password,
                        error = state.passwordError,
                        isError = state.passwordError.isNotBlank(),
                        onValueChange = signUpModel::onPasswordChange
                    )
                    Row {
                        DropDownMenu(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 8.dp, bottom = 24.dp, end = 8.dp),
                            expanded = state.ageSelecting,
                            label = stringResource(R.string.age),
                            selectedItem = state.age,
                            items = ages,
                            onMenu = signUpModel::onAge,
                            onItem = signUpModel::onAgeChange,
                            onDismiss = signUpModel::onAgeDismiss
                        )
                        DropDownMenu(
                            modifier = Modifier
                                .weight(1.5f)
                                .padding(top = 8.dp, bottom = 24.dp, start = 4.dp, end = 4.dp),
                            expanded = state.sexSelecting,
                            label = stringResource(R.string.sex),
                            selectedItem = state.sex,
                            items = sexes,
                            onMenu = signUpModel::onSex,
                            onItem = signUpModel::onSexChange,
                            onDismiss = signUpModel::onSexDismiss
                        )
                        DropDownMenu(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 8.dp, bottom = 24.dp, start = 8.dp),
                            expanded = state.countrySelecting,
                            label = stringResource(R.string.country),
                            selectedItem = state.countryCode,
                            items = countries,
                            onMenu = signUpModel::onCountry,
                            onItem = signUpModel::onCountryCodeChange,
                            onDismiss = signUpModel::onCountryDismiss
                        )
                    }
                    AuthButton(
                        text = stringResource(R.string.sign_up),
                        enabled = state.signUpEnabled,
                        onClick = {
                            focusManager.clearFocus()
                            signUpModel.onSignUp()
                        }
                    )
                }
                AuthFooter(
                    modifier = Modifier.padding(24.dp),
                    text = stringResource(R.string.sign_up_footer),
                    clickableText = stringResource(R.string.log_in),
                    onClick = {
                        focusManager.clearFocus()
                        onLogIn()
                    }
                )
            }
            if (state.loading) {
                AuthProgressBar()
            }
        }
    }
    signUpModel.event.consumeAsEffect {
        when (it) {
            is SignUpEvent.Error -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
            SignUpEvent.Success -> onSignUpSuccess()
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "SignUpScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun AuthScreenPreview() {
    SurveyServiceTheme {
        Scaffold {
            SignUpScreen(
                object : SignUpModel {

                    override val state: StateFlow<SignUpState>
                        get() = TODO("Not yet implemented")

                    override val event: Event<SignUpEvent>
                        get() = TODO("Not yet implemented")

                    override fun onSignUp() {}
                    override fun onAge() {}
                    override fun onAgeChange(age: String) {}
                    override fun onAgeDismiss() {}
                    override fun onSex() {}
                    override fun onSexChange(sex: String) {}
                    override fun onSexDismiss() {}
                    override fun onCountry() {}
                    override fun onCountryCodeChange(countryCode: String) {}
                    override fun onCountryDismiss() {}
                    override fun onNameChange(name: String) {}
                    override fun onEmailChange(email: String) {}
                    override fun onPasswordChange(password: String) {}
                }, {}, {}
            )
        }
    }
}
