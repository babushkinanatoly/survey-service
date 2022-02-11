package ru.babushkinanatoly.surveyservice.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.surveyservice.util.Event
import ru.babushkinanatoly.surveyservice.util.consumeAsEffect

@Composable
fun ProfileWorkflow(
    fallbackToRoot: Event<Unit>,
    profileTitle: String,
    statisticsTitle: String,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onLogOut: () -> Unit
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
