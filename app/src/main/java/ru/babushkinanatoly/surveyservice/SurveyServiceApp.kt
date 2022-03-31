package ru.babushkinanatoly.surveyservice

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.*
import ru.babushkinanatoly.feature_auth.AuthWorkflow
import ru.babushkinanatoly.feature_new_survey.NewSurveyScreen
import ru.babushkinanatoly.feature_settings.SettingsScreen

@Composable
fun SurveyServiceApp() {
    val navController = rememberNavController()
    val loggedIn = (LocalContext.current.applicationContext as App).loggedIn
    NavHost(
        navController,
        startDestination = if (!loggedIn) AuthWorkflow.route else NavWorkflow.route,
    ) {
        composable(AuthWorkflow.route) {
            AuthWorkflow {
                navController.navigate(NavWorkflow.route) {
                    popUpTo(AuthWorkflow.route) {
                        inclusive = true
                    }
                }
            }
        }
        composable(NavWorkflow.route) {
            NavWorkflow(
                onNewSurvey = { navController.navigate(NewSurvey.route) },
                onSettings = { navController.navigate(Settings.route) },
                onLogOut = {
                    navController.navigate(AuthWorkflow.route) {
                        popUpTo(NavWorkflow.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(NewSurvey.route) {
            NewSurveyScreen()
        }
        composable(Settings.route) {
            SettingsScreen()
        }
    }
}
