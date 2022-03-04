package ru.babushkinanatoly.surveyservice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.*
import ru.babushkinanatoly.feature_auth.AuthScreen
import ru.babushkinanatoly.feature_auth.AuthViewModel
import ru.babushkinanatoly.feature_new_survey.NewSurveyScreen
import ru.babushkinanatoly.feature_settings.SettingsScreen

@Composable
fun SurveyServiceApp(loggedIn: Boolean) {
    val navController = rememberNavController()
    val shouldShowAuth by rememberSaveable { mutableStateOf(!loggedIn) }
    NavHost(
        navController,
        startDestination = if (shouldShowAuth) Auth.route else NavWorkflow.route,
    ) {
        composable(Auth.route) {
            AuthScreen(viewModel<AuthViewModel>().authComponent.provideModel()) {
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
