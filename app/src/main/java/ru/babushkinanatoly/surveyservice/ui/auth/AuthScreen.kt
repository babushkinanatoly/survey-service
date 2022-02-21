package ru.babushkinanatoly.surveyservice.ui.auth

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.babushkinanatoly.surveyservice.R
import ru.babushkinanatoly.surveyservice.data.RepoImpl
import ru.babushkinanatoly.surveyservice.data.UserAuthData
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme
import ru.babushkinanatoly.surveyservice.util.consumeAsEffect

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(RepoImpl())),
    onEmailChange: (String) -> Unit = authViewModel.authModel::onEmailChange,
    onPasswordChange: (String) -> Unit = authViewModel.authModel::onPasswordChange,
    onLogIn: (UserAuthData) -> Unit = authViewModel.authModel::onLogIn,
    onLogInSuccess: () -> Unit,
) {
    val state by authViewModel.authModel.state.collectAsState()
    val context = LocalContext.current
    // TODO: Lift up the content depending on whether the keyboard is shown or not (insets)
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(vertical = 24.dp),
                fontWeight = FontWeight.Bold,
                text = stringResource(R.string.auth_header),
            )
            // TODO: Lose focus when onLogin
            EmailField(state.email, state.emailError) { onEmailChange(it) }
            PasswordField(state.password, state.passwordError) { onPasswordChange(it) }
            Button(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .sizeIn(minHeight = 56.dp)
                    .fillMaxWidth(),
                enabled = state.loginEnabled,
                onClick = { onLogIn(UserAuthData(state.email, state.password)) }
            ) {
                Text(stringResource(R.string.log_in))
            }
        }
        if (state.loading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface.copy(alpha = ContentAlpha.medium))
                    .pointerInput(Unit) {}
            ) {
                CircularProgressIndicator()
            }
        }
        authViewModel.authModel.loginEvent.consumeAsEffect {
            when (it) {
                is LogInEvent.Error -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                LogInEvent.Success -> onLogInSuccess()
            }
        }
    }
}

@Composable
private fun EmailField(
    text: String,
    error: String,
    onValueChange: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .sizeIn(minHeight = 56.dp)
                .fillMaxWidth(),
            value = text,
            isError = error.isNotBlank(),
            onValueChange = { onValueChange(it) },
            placeholder = { Text(stringResource(R.string.email)) },
            singleLine = true
        )
        ErrorText(
            text = error,
            visible = error.isNotBlank()
        )
    }
}

@Composable
private fun PasswordField(
    text: String,
    error: String,
    onValueChange: (String) -> Unit,
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .sizeIn(minHeight = 56.dp)
                .fillMaxWidth(),
            value = text,
            isError = error.isNotBlank(),
            onValueChange = { onValueChange(it) },
            placeholder = { Text(stringResource(R.string.password)) },
            singleLine = true,
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconToggleButton(
                    checked = showPassword,
                    onCheckedChange = { showPassword = it }
                ) {
                    Icon(
                        painterResource(
                            if (showPassword) R.drawable.ic_show_password else R.drawable.ic_hide_password
                        ),
                        tint = if (showPassword) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                        },
                        contentDescription = null
                    )
                }
            }
        )
        ErrorText(
            text = error,
            visible = error.isNotBlank()
        )
    }
}

@Composable
private fun ErrorText(
    text: String = "Error message",
    visible: Boolean = false,
) {
    val alpha by animateFloatAsState(if (visible) 1f else 0f)
    Text(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .alpha(alpha),
        text = text,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption,
    )
}

@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "AuthScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun AuthScreenPreview() {
    SurveyServiceTheme {
        AuthScreen {}
    }
}
