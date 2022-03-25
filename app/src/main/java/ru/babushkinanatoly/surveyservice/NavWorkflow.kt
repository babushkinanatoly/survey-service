package ru.babushkinanatoly.surveyservice

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation
import ru.babushkinanatoly.base_feature.AppNavigation.Screen
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.core_api.MutableEvent
import ru.babushkinanatoly.core_api.dispatch
import ru.babushkinanatoly.feature_profile.ProfileWorkflow
import ru.babushkinanatoly.feature_survey_feed.SurveyFeedWorkflow
import ru.babushkinanatoly.feature_user_surveys.UserSurveysWorkflow

@Composable
fun NavWorkflow(
    onNewSurvey: () -> Unit,
    onSettings: () -> Unit,
    onLogOut: () -> Unit,
) {
    val navController = rememberNavController()
    val fallbackToSurveyFeedRoot = remember { MutableEvent<Unit>() }
    val fallbackToUserSurveysRoot = remember { MutableEvent<Unit>() }
    val fallbackToProfileRoot = remember { MutableEvent<Unit>() }
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                AppNavigation.bottomNavItems.forEach { screen ->
                    val surveyFeedReselected = {
                        currentDestination?.route == NavWorkflow.SurveyFeedWorkflow.route
                                && screen.route == NavWorkflow.SurveyFeedWorkflow.route
                    }
                    val userSurveysReselected = {
                        currentDestination?.route == NavWorkflow.UserSurveysWorkflow.route
                                && screen.route == NavWorkflow.UserSurveysWorkflow.route
                    }
                    val profileReselected = {
                        currentDestination?.route == NavWorkflow.ProfileWorkflow.route
                                && screen.route == NavWorkflow.ProfileWorkflow.route
                    }
                    BottomNavigationItem(
                        icon = { GetIcon(screen) },
                        label = { Text(stringResource(screen.resId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigateAndPopUpToStart(screen.route)
                            when {
                                surveyFeedReselected() -> fallbackToSurveyFeedRoot.dispatch(Unit)
                                userSurveysReselected() -> fallbackToUserSurveysRoot.dispatch(Unit)
                                profileReselected() -> fallbackToProfileRoot.dispatch(Unit)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = NavWorkflow.SurveyFeedWorkflow.route,
            Modifier.padding(innerPadding)
        ) {
            composable(NavWorkflow.SurveyFeedWorkflow.route) {
                SurveyFeedWorkflow(fallbackToSurveyFeedRoot)
            }
            composable(NavWorkflow.UserSurveysWorkflow.route) {
                UserSurveysWorkflow(
                    fallbackToUserSurveysRoot,
                    onBack = {
                        navController.navigateAndPopUpToStart(navController.graph.findStartDestination().route!!)
                    },
                    onNewSurvey = onNewSurvey
                )
            }
            composable(NavWorkflow.ProfileWorkflow.route) {
                ProfileWorkflow(
                    fallbackToProfileRoot,
                    stringResource(NavWorkflow.ProfileWorkflow.Profile.resId),
                    stringResource(NavWorkflow.ProfileWorkflow.Statistics.resId),
                    onBack = {
                        navController.navigateAndPopUpToStart(navController.graph.findStartDestination().route!!)
                    },
                    onSettings = onSettings,
                    onLogOut = onLogOut
                )
            }
        }
    }
}

private fun NavHostController.navigateAndPopUpToStart(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun GetIcon(screen: Screen) {
    val iconRes = screen.getIconRes()
    Icon(
        painter = painterResource(iconRes.first),
        contentDescription = stringResource(iconRes.second)
    )
}

private fun Screen.getIconRes() = when (this) {
    is NavWorkflow.SurveyFeedWorkflow -> R.drawable.ic_feed to resId
    is NavWorkflow.UserSurveysWorkflow -> R.drawable.ic_user_surveys to resId
    is NavWorkflow.ProfileWorkflow -> R.drawable.ic_profile to resId
    else -> error("No resources provided for this screen: $this")
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "NavWorkflowPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun NavWorkflowPreview() {
    SurveyServiceTheme {
        NavWorkflow({}, {}, {})
    }
}
