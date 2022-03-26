package ru.babushkinanatoly.surveyservice

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.*
import ru.babushkinanatoly.feature_auth.AuthScreen
import ru.babushkinanatoly.feature_new_survey.NewSurveyScreen
import ru.babushkinanatoly.feature_settings.SettingsScreen

@Composable
fun SurveyServiceApp(loggedIn: Boolean) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = if (!loggedIn) Auth.route else NavWorkflow.route,
    ) {
        composable(Auth.route) {
            AuthScreen {
                navController.navigate(NavWorkflow.route) {
                    popUpTo(Auth.route) {
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
                    navController.navigate(Auth.route) {
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
            SettingsScreen(stringResource(Settings.resId))
        }
    }
}
