package ru.babushkinanatoly.surveyservice

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.babushkinanatoly.surveyservice.ui.auth.AuthScreen
import ru.babushkinanatoly.surveyservice.ui.nav.NavWorkflow
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurveyServiceTheme {
                Surface(color = MaterialTheme.colors.background) {
                    SurveyServiceApp(false)
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun NavPreview() {
    SurveyServiceTheme {
        NavWorkflow(
            onNewSurvey = { /*TODO*/ },
            onSettings = { /*TODO*/ }
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 760)
@Composable
fun AuthPreview() {
    SurveyServiceTheme {
        AuthScreen(onLogIn = {})
    }
}
