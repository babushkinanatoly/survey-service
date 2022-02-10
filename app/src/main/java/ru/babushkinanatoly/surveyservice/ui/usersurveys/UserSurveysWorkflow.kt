package ru.babushkinanatoly.surveyservice.ui.usersurveys

import androidx.activity.compose.BackHandler
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.surveyservice.util.Event
import ru.babushkinanatoly.surveyservice.util.MutableEvent
import ru.babushkinanatoly.surveyservice.util.consumeAsEffect
import ru.babushkinanatoly.surveyservice.util.dispatch

@ExperimentalMaterialApi
@Composable
fun UserSurveysWorkflow(
    fallbackToRoot: Event<Unit>,
    userSurveysTitle: String,
    userSurveyDetailsTitle: String,
    onBack: () -> Unit,
    onNewSurvey: () -> Unit
) {
    val navController = rememberNavController()
    val scrollSurveysUp = remember { MutableEvent<Unit>() }
    var backEnabled by rememberSaveable { mutableStateOf(true) }
    BackHandler(enabled = backEnabled) { onBack() }
    NavHost(
        navController,
        startDestination = NavWorkflow.UserSurveysWorkflow.UserSurveys.route
    ) {
        composable(NavWorkflow.UserSurveysWorkflow.UserSurveys.route) {
            backEnabled = true
            UserSurveysScreen(
                scrollSurveysUp,
                title = userSurveysTitle,
                onItem = { navController.navigate(NavWorkflow.UserSurveysWorkflow.UserSurveyDetails.route) },
                onNewSurvey = onNewSurvey
            )
        }
        composable(NavWorkflow.UserSurveysWorkflow.UserSurveyDetails.route) {
            backEnabled = false
            UserSurveyDetailsScreen(
                title = userSurveyDetailsTitle
            )
        }
    }
    fallbackToRoot.consumeAsEffect {
        val startDestination = navController.graph.findStartDestination()
        if (navController.currentDestination == startDestination) {
            scrollSurveysUp.dispatch(Unit)
        } else {
            navController.navigate(startDestination.route!!) {
                popUpTo(startDestination.route!!)
                launchSingleTop = true
            }
        }
    }
}
