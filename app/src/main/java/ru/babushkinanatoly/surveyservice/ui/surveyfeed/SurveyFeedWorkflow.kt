package ru.babushkinanatoly.surveyservice.ui.surveyfeed

import android.content.res.Configuration
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme
import ru.babushkinanatoly.surveyservice.util.Event
import ru.babushkinanatoly.surveyservice.util.MutableEvent
import ru.babushkinanatoly.surveyservice.util.consumeAsEffect
import ru.babushkinanatoly.surveyservice.util.dispatch

@ExperimentalMaterialApi
@Composable
fun SurveyFeedWorkflow(
    fallbackToRoot: Event<Unit>,
    surveyFeedTitle: String,
    surveyDetailsTitle: String,
) {
    val navController = rememberNavController()
    val scrollSurveysUp = remember { MutableEvent<Unit>() }
    NavHost(
        navController,
        startDestination = NavWorkflow.SurveyFeedWorkflow.SurveyFeed.route
    ) {
        composable(NavWorkflow.SurveyFeedWorkflow.SurveyFeed.route) {
            SurveyFeedScreen(
                scrollSurveysUp,
                title = surveyFeedTitle,
                onItem = { navController.navigate(NavWorkflow.SurveyFeedWorkflow.SurveyDetails.route) }
            )
        }
        composable(NavWorkflow.SurveyFeedWorkflow.SurveyDetails.route) {
            SurveyDetailsScreen(
                title = surveyDetailsTitle
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

@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "SurveyFeedWorkflowPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun SurveyFeedWorkflowPreview() {
    SurveyServiceTheme {
        Scaffold {
            SurveyFeedWorkflow(MutableEvent(), "Survey feed", "Survey details")
        }
    }
}
