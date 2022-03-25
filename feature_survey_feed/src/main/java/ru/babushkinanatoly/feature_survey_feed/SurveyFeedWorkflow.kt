package ru.babushkinanatoly.feature_survey_feed

import android.content.res.Configuration
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.base_feature.util.requireLong
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.core_api.MutableEvent
import ru.babushkinanatoly.core_api.dispatch
import ru.babushkinanatoly.feature_survey_feed.surveydetails.SurveyDetailsScreen
import ru.babushkinanatoly.feature_survey_feed.surveydetails.di.SurveyDetailsViewModel
import ru.babushkinanatoly.feature_survey_feed.surveyfeed.SurveyFeedScreen
import ru.babushkinanatoly.feature_survey_feed.surveyfeed.di.SurveyFeedViewModel

@Composable
fun SurveyFeedWorkflow(
    fallbackToRoot: Event<Unit>,
) {
    val navController = rememberNavController()
    val scrollSurveysUp = remember { MutableEvent<Unit>() }
    NavHost(
        navController,
        startDestination = NavWorkflow.SurveyFeedWorkflow.SurveyFeed.route
    ) {
        composable(NavWorkflow.SurveyFeedWorkflow.SurveyFeed.route) {
            SurveyFeedScreen(
                viewModel<SurveyFeedViewModel>().surveyFeedComponent.provideModel(),
                scrollSurveysUp,
                onItem = {
                    navController.navigate(
                        NavWorkflow.SurveyFeedWorkflow.SurveyDetails.route + "/$it"
                    )
                }
            )
        }
        composable(NavWorkflow.SurveyFeedWorkflow.SurveyDetails.route + "/{surveyId}") {
            SurveyDetailsScreen(
                viewModel<SurveyDetailsViewModel>()
                    .getSurveyDetailsComponent(it.requireLong("surveyId")).provideModel(),
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
    name = "SurveyFeedWorkflowPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun SurveyFeedWorkflowPreview() {
    SurveyServiceTheme {
        Scaffold {
            SurveyFeedWorkflow(MutableEvent())
        }
    }
}
