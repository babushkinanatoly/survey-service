package ru.babushkinanatoly.feature_auth.login

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
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.feature_auth.*
import ru.babushkinanatoly.feature_auth.R

@Composable
internal fun LogInScreen(
    logInModel: LogInModel,
    onSignUp: () -> Unit,
    onLogInSuccess: () -> Unit,
) {
    val state by logInModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
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
                    AuthHeader(text = stringResource(R.string.log_in_header))
                    EmailField(
                        text = state.email,
                        error = state.emailError,
                        isError = state.emailError.isNotBlank(),
                        onValueChange = logInModel::onEmailChange
                    )
                    PasswordField(
                        text = state.password,
                        error = state.passwordError,
                        isError = state.passwordError.isNotBlank(),
                        onValueChange = logInModel::onPasswordChange
                    )
                    AuthButton(
                        text = stringResource(R.string.log_in),
                        enabled = state.loginEnabled,
                        onClick = {
                            focusManager.clearFocus()
                            logInModel.onLogIn()
                        }
                    )
                }
                AuthFooter(
                    modifier = Modifier.padding(24.dp),
                    text = stringResource(R.string.log_in_footer),
                    clickableText = stringResource(R.string.sign_up),
                    onClick = {
                        focusManager.clearFocus()
                        onSignUp()
                    }
                )
            }
            if (state.loading) {
                AuthProgressBar()
            }
        }
    }
    logInModel.event.consumeAsEffect {
        when (it) {
            is LogInEvent.Error -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
            LogInEvent.Success -> onLogInSuccess()
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "LogInScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun AuthScreenPreview() {
    SurveyServiceTheme {
        Scaffold {
            LogInScreen(
                object : LogInModel {

                    override val state: StateFlow<LogInState>
                        get() = TODO("Not yet implemented")

                    override val event: Event<LogInEvent>
                        get() = TODO("Not yet implemented")

                    override fun onLogIn() {}
                    override fun onEmailChange(email: String) {}
                    override fun onPasswordChange(password: String) {}
                }, {}, {}
            )
        }
    }
}
