package ru.babushkinanatoly.feature_auth

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.*

@Composable
fun AuthScreen(
    authModel: AuthModel,
    onLogInSuccess: () -> Unit,
) {
    val state by authModel.state.collectAsState()
    val context = LocalContext.current
    Surface {
        CompositionLocalProvider(
            LocalOverScrollConfiguration provides null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val focusManager = LocalFocusManager.current
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthHeader(
                        text = stringResource(R.string.auth_header)
                    )
                }
                EmailField(
                    text = state.email,
                    error = state.emailError,
                    isError = state.emailError.isNotBlank(),
                    onValueChange = authModel::onEmailChange
                )
                PasswordField(
                    text = state.password,
                    error = state.passwordError,
                    isError = state.passwordError.isNotBlank(),
                    onValueChange = authModel::onPasswordChange
                )
                AuthButton(
                    text = stringResource(R.string.log_in),
                    enabled = state.loginEnabled,
                    onClick = {
                        focusManager.clearFocus()
                        authModel.onLogIn()
                    }
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val signUpText = stringResource(R.string.sign_up)
                    AuthFooter(
                        text = stringResource(R.string.auth_footer),
                        clickableText = signUpText,
                        onClick = {
                            Toast.makeText(context, signUpText, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            if (state.loading) {
                AuthProgressBar()
            }
        }
    }
    authModel.loginEvent.consumeAsEffect {
        when (it) {
            is LogInEvent.Error -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
            LogInEvent.Success -> onLogInSuccess()
        }
    }
}

@Composable
private fun AuthHeader(text: String) {
    Text(
        modifier = Modifier.padding(vertical = 24.dp),
        fontWeight = FontWeight.Bold,
        text = text
    )
}

@Composable
private fun AuthFooter(
    text: String,
    clickableText: String,
    onClick: () -> Unit,
) {
    Text(
        modifier = Modifier.padding(top = 24.dp),
        fontSize = 14.sp,
        text = text
    )
    Text(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .clickable { onClick() },
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colors.primary,
        text = clickableText
    )
}

@Composable
private fun EmailField(
    text: String,
    error: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .sizeIn(minHeight = 56.dp)
                .fillMaxWidth(),
            value = text,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                )
            },
            isError = isError,
            onValueChange = { onValueChange(it) },
            placeholder = { Text(stringResource(R.string.email)) },
            singleLine = true
        )
        ErrorText(
            text = error,
            visible = isError
        )
    }
}

@Composable
private fun PasswordField(
    text: String,
    error: String,
    isError: Boolean,
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
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                )
            },
            isError = isError,
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
            visible = isError
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

@Composable
private fun AuthButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .sizeIn(minHeight = 56.dp)
            .fillMaxWidth(),
        enabled = enabled,
        onClick = onClick
    ) {
        Text(text)
    }
}

@Composable
private fun AuthProgressBar() {
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
        AuthScreen(
            AuthModelImpl(
                CoroutineScope(Dispatchers.IO),
                object : StringRes {
                    override fun get(resId: Int): String {
                        TODO("Not yet implemented")
                    }

                    override fun format(resId: Int, vararg args: Any): String {
                        TODO("Not yet implemented")
                    }
                },
                object : Repo {
                    override val currentUser: Flow<User?>
                        get() = TODO("Not yet implemented")

                    override suspend fun onLogIn(userAuthData: UserAuthData): LogInResult {
                        TODO("Not yet implemented")
                    }
                }
            )
        ) {}
    }
}
