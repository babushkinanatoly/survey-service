package ru.babushkinanatoly.feature_user_surveys

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.Event
import ru.babushkinanatoly.base_feature.util.MutableEvent
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.base_feature.util.dispatch
import ru.babushkinanatoly.feature_user_surveys.usersurveys.di.UserSurveysViewModel

@Composable
fun UserSurveysWorkflow(
    fallbackToRoot: Event<Unit>,
    userSurveyDetailsTitle: String,
    onBack: () -> Unit,
    onNewSurvey: () -> Unit,
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
                viewModel<UserSurveysViewModel>().userSurveysComponent.provideModel(),
                scrollSurveysUp,
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

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "UserSurveysWorkflowPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun UserSurveysWorkflowPreview() {
    SurveyServiceTheme {
        Scaffold {
            UserSurveysWorkflow(MutableEvent(), "User survey details", {}, {})
        }
    }
}
