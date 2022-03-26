package ru.babushkinanatoly.feature_settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.Settings
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.goBack

@Composable
fun SettingsScreen(
) {
    val title = stringResource(Settings.resId)
    val context = LocalContext.current
    Surface {
        Scaffold {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { context.goBack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "SettingsScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun SettingsScreenPreview() {
    SurveyServiceTheme {
        SettingsScreen()
    }
}
