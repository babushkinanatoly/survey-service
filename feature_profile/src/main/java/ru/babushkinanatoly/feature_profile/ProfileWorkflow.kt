package ru.babushkinanatoly.feature_profile

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.Event
import ru.babushkinanatoly.base_feature.util.MutableEvent
import ru.babushkinanatoly.base_feature.util.consumeAsEffect

@Composable
fun ProfileWorkflow(
    fallbackToRoot: Event<Unit>,
    profileTitle: String,
    statisticsTitle: String,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onLogOut: () -> Unit,
) {
    val navController = rememberNavController()
    var backEnabled by rememberSaveable { mutableStateOf(true) }
    BackHandler(enabled = backEnabled) { onBack() }
    NavHost(
        navController,
        startDestination = NavWorkflow.ProfileWorkflow.Profile.route
    ) {
        composable(NavWorkflow.ProfileWorkflow.Profile.route) {
            backEnabled = true
            ProfileScreen(
                title = profileTitle,
                onSettings = onSettings,
                onLogOut = onLogOut,
                onStatistics = { navController.navigate(NavWorkflow.ProfileWorkflow.Statistics.route) }
            )
        }
        composable(NavWorkflow.ProfileWorkflow.Statistics.route) {
            backEnabled = false
            StatisticsScreen(
                title = statisticsTitle
            )
        }
    }
    fallbackToRoot.consumeAsEffect {
        val startDestination = navController.graph.findStartDestination()
        navController.navigate(startDestination.route!!) {
            popUpTo(startDestination.route!!)
            launchSingleTop = true
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "ProfileWorkflowPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun ProfileWorkflowPreview() {
    SurveyServiceTheme {
        Scaffold {
            ProfileWorkflow(MutableEvent(), "Profile", "Statistics", {}, {}, {})
        }
    }
}
