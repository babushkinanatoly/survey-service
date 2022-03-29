package ru.babushkinanatoly.feature_profile.profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.feature_profile.R

@Composable
fun ProfileScreen(
    title: String,
    onSettings: () -> Unit,
    onLogOut: () -> Unit,
    onStatistics: () -> Unit,
) {
    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = title) },
                    backgroundColor = MaterialTheme.colors.background,
                    actions = {
                        IconButton(onClick = onSettings) {
                            Icon(Icons.Outlined.Settings, stringResource(R.string.settings))
                        }
                        IconButton(onClick = onLogOut) {
                            Icon(painterResource(R.drawable.ic_log_out), stringResource(R.string.log_out))
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                ClickableText(
                    text = AnnotatedString("Click to view statistics"),
                    style = TextStyle(
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 18.sp
                    ),
                    onClick = { onStatistics() }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "ProfileScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun ProfileScreenPreview() {
    SurveyServiceTheme {
        ProfileScreen("Profile", {}, {}, {})
    }
}
