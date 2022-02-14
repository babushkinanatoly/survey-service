package ru.babushkinanatoly.surveyservice.ui.newsurvey

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.babushkinanatoly.surveyservice.R
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen.NewSurvey
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme

@Composable
fun NewSurveyScreen(
    title: String
) {
    Surface {
        Scaffold {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = {}) {
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

@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "NewSurveyPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun NewSurveyPreview() {
    SurveyServiceTheme {
        NewSurveyScreen(stringResource(NewSurvey.resId))
    }
}
