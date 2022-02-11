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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme

@Composable
fun AuthScreen(
    onLogIn: () -> Unit
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
            var usernameText by rememberSaveable { mutableStateOf("") }
            AuthTextField(usernameText, "Username") { usernameText = it }
            var passwordText by rememberSaveable { mutableStateOf("") }
            AuthTextField(passwordText, "Password") { passwordText = it }
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
private fun AuthTextField(
    text: String,
    placeholder: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .sizeIn(minHeight = 56.dp)
            .fillMaxWidth(),
        value = text,
        onValueChange = { onTextChange(it) },
        singleLine = true,
        placeholder = { Text(placeholder) }
    )
}

@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "AuthPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun AuthPreview() {
    SurveyServiceTheme {
        AuthScreen(onLogIn = {})
    }
}
