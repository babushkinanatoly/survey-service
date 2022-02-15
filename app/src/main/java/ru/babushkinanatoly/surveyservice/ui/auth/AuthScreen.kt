package ru.babushkinanatoly.surveyservice.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.babushkinanatoly.surveyservice.R
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme

@Composable
fun AuthScreen(
    onLogIn: () -> Unit,
) {
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
                text = "Please log in to continue",
            )
            UsernameField()
            PasswordField()
            Button(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .sizeIn(minHeight = 56.dp)
                    .fillMaxWidth(),
                onClick = onLogIn
            ) {
                Text("Log in")
            }
        }
    }
}

@Composable
private fun UsernameField() {
    var text by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .sizeIn(minHeight = 56.dp)
            .fillMaxWidth(),
        value = text,
        onValueChange = { text = it },
        placeholder = { Text("Username") },
        singleLine = true
    )
}

@Composable
private fun PasswordField() {
    var text by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .sizeIn(minHeight = 56.dp)
            .fillMaxWidth(),
        value = text,
        onValueChange = { text = it },
        placeholder = { Text("Password") },
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
