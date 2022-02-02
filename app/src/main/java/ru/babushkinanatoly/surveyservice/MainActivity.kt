package ru.babushkinanatoly.surveyservice

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.surveyservice.ui.auth.AuthScreen
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen.*
import ru.babushkinanatoly.surveyservice.ui.nav.NavWorkflow
import ru.babushkinanatoly.surveyservice.ui.newsurvey.NewSurveyScreen
import ru.babushkinanatoly.surveyservice.ui.settings.SettingsScreen
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
@Composable
private fun SurveyServiceApp(loggedIn: Boolean) {
    val navController = rememberNavController()
    val shouldShowAuth by rememberSaveable { mutableStateOf(!loggedIn) }
    NavHost(
        navController,
        startDestination = if (shouldShowAuth) Auth.route else NavWorkflow.route,
    ) {
        composable(Auth.route) {
            AuthScreen {
                navController.navigate(NavWorkflow.route)
            }
        }
        composable(NavWorkflow.route) {
            NavWorkflow(
                onNewSurvey = { navController.navigate(NewSurvey.route) },
                onSettings = { navController.navigate(Settings.route) }
            )
        }
        composable(NewSurvey.route) {
            NewSurveyScreen(stringResource(NewSurvey.resId))
        }
        composable(Settings.route) {
            SettingsScreen(stringResource(Settings.resId))
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
