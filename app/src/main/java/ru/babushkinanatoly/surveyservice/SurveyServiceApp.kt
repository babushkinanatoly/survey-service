package ru.babushkinanatoly.surveyservice

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.surveyservice.ui.auth.AuthScreen
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen.*
import ru.babushkinanatoly.surveyservice.ui.nav.NavWorkflow
import ru.babushkinanatoly.surveyservice.ui.newsurvey.NewSurveyScreen
import ru.babushkinanatoly.surveyservice.ui.settings.SettingsScreen

@ExperimentalMaterialApi
@Composable
fun SurveyServiceApp(loggedIn: Boolean) {
    val navController = rememberNavController()
    val shouldShowAuth by rememberSaveable { mutableStateOf(!loggedIn) }
    NavHost(
        navController,
        startDestination = if (shouldShowAuth) Auth.route else NavWorkflow.route,
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
            NewSurveyScreen(stringResource(NewSurvey.resId))
        }
        composable(Settings.route) {
            SettingsScreen(stringResource(Settings.resId))
        }
    }
}
