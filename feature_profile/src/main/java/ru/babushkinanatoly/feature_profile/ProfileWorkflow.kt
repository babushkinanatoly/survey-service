package ru.babushkinanatoly.feature_profile

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.core_api.MutableEvent
import ru.babushkinanatoly.feature_profile.di.ProfileWorkflowViewModel
import ru.babushkinanatoly.feature_profile.profile.ProfileScreen
import ru.babushkinanatoly.feature_profile.statistics.StatisticsScreen

@Composable
fun ProfileWorkflow(
    fallbackToRoot: Event<Unit>,
    profileTitle: String,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onLogOut: () -> Unit,
) {
    val profileWorkflowModel = viewModel<ProfileWorkflowViewModel>().profileWorkflowComponent.provideModel()
    val navController = rememberNavController()
    val context = LocalContext.current
    var backEnabled by rememberSaveable { mutableStateOf(true) }
    val errorMsg = stringResource(R.string.error_general)
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
                onLogOut = profileWorkflowModel::onLogOut,
                onStatistics = { navController.navigate(NavWorkflow.ProfileWorkflow.Statistics.route) }
            )
        }
        composable(NavWorkflow.ProfileWorkflow.Statistics.route) {
            backEnabled = false
            StatisticsScreen()
        }
    }
    fallbackToRoot.consumeAsEffect {
        val startDestination = navController.graph.findStartDestination()
        navController.navigate(startDestination.route!!) {
            popUpTo(startDestination.route!!)
            launchSingleTop = true
        }
    }
    profileWorkflowModel.event.consumeAsEffect {
        when (it) {
            ProfileWorkflowEvent.LOGGED_OUT -> onLogOut()
            ProfileWorkflowEvent.ERROR -> {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
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
            ProfileWorkflow(MutableEvent(), "Profile", {}, {}, {})
        }
    }
}
