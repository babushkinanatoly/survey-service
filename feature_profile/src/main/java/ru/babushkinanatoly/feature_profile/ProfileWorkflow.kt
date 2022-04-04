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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow.ProfileWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.core_api.MutableEvent
import ru.babushkinanatoly.feature_profile.profile.ProfileScreen
import ru.babushkinanatoly.feature_profile.profile.di.ProfileViewModel
import ru.babushkinanatoly.feature_profile.statistics.StatisticsScreen
import ru.babushkinanatoly.feature_profile.statistics.di.StatisticsViewModel

@Composable
fun ProfileWorkflow(
    fallbackToRoot: Event<Unit>,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onLogOut: () -> Unit,
) {
    val navController = rememberNavController()
    var backEnabled by rememberSaveable { mutableStateOf(true) }
    BackHandler(enabled = backEnabled) { onBack() }
    NavHost(
        navController = navController,
        startDestination = ProfileWorkflow.Profile.route
    ) {
        composable(ProfileWorkflow.Profile.route) {
            backEnabled = true
            ProfileScreen(
                viewModel<ProfileViewModel>().profileComponent.provideModel(),
                onSettings = onSettings,
                onLogOut = onLogOut,
                onStatistics = { navController.navigate(ProfileWorkflow.Statistics.route) }
            )
        }
        composable(ProfileWorkflow.Statistics.route) {
            backEnabled = false
            StatisticsScreen(viewModel<StatisticsViewModel>().statisticsComponent.provideModel())
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
            ProfileWorkflow(MutableEvent(), {}, {}, {})
        }
    }
}
