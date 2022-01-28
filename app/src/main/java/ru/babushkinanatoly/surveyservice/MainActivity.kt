package ru.babushkinanatoly.surveyservice

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ru.babushkinanatoly.surveyservice.AppNavigation.Companion.bottomNavItems
import ru.babushkinanatoly.surveyservice.AppNavigation.Screen.*
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme

private open class AppNavigation(val route: String, @StringRes val resId: Int) {

    companion object {
        val bottomNavItems = listOf(
            NavFlow.SurveyFeedFlow,
            NavFlow.UserSurveysFlow,
            NavFlow.ProfileFlow
        )
    }

    sealed class Screen(screenRoute: String, screenResId: Int) : AppNavigation(screenRoute, screenResId) {
        object Auth : Screen("auth", R.string.auth)
        object NewSurvey : Screen("newsurvey", R.string.new_survey)
        object Settings : Screen("settings", R.string.settings)

        object NavFlow : Screen("navflow", R.string.navigation) {
            object SurveyFeedFlow : Screen("surveyfeedflow", R.string.survey_feed) {
                object SurveyFeed : Screen("surveyfeed", R.string.survey_feed)
                object SurveyDetails : Screen("surveydetails", R.string.survey_details)
            }

            object UserSurveysFlow : Screen("usersurveysflow", R.string.user_surveys) {
                object UserSurveys : Screen("usersurveys", R.string.user_surveys)
                object UserSurveyDetails : Screen("usersurveydetails", R.string.user_survey_details)
            }

            object ProfileFlow : Screen("profileflow", R.string.profile) {
                object Profile : Screen("profile", R.string.profile)
                object Statistics : Screen("statistics", R.string.statistics)
            }
        }
    }
}

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurveyServiceTheme {
                Surface(color = MaterialTheme.colors.background) {
                    SurveyServiceApp(false)
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SurveyServiceApp(loggedIn: Boolean) {
    val navController = rememberNavController()
    val shouldShowAuth by rememberSaveable { mutableStateOf(!loggedIn) }
    NavHost(
        navController,
        startDestination = if (shouldShowAuth) Auth.route else NavFlow.route,
    ) {
        // TODO: 1/20/2022 Think of how it will be related with data from view models
        composable(Auth.route) {
            AuthScreen {
                navController.navigate(NavFlow.route)
            }
        }
        composable(NavFlow.route) {
            NavFlow(
                onNewSurvey = { navController.navigate(NewSurvey.route) },
                onSettings = { navController.navigate(Settings.route) }
            )
        }
        composable(NewSurvey.route) {
            UserSurveyDetailsScreen(stringResource(NewSurvey.resId))
        }
        composable(Settings.route) {
            SettingsScreen(stringResource(Settings.resId))
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun NavFlow(
    onNewSurvey: () -> Unit,
    onSettings: () -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    BottomNavigationItem(
                        icon = { GetIcon(screen) },
                        label = { Text(stringResource(screen.resId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            val profileReselected =
                                currentDestination?.route == NavFlow.ProfileFlow.route
                                        && screen.route == NavFlow.ProfileFlow.route

                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = !profileReselected
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = NavFlow.SurveyFeedFlow.route,
            Modifier.padding(innerPadding)
        ) {
            composable(NavFlow.SurveyFeedFlow.route) {
                SurveyFeedFlow(
                    stringResource(NavFlow.SurveyFeedFlow.SurveyFeed.resId),
                    stringResource(NavFlow.SurveyFeedFlow.SurveyDetails.resId),
                )
            }
            composable(NavFlow.UserSurveysFlow.route) {
                UserSurveysFlow(
                    stringResource(NavFlow.UserSurveysFlow.UserSurveys.resId),
                    stringResource(NavFlow.UserSurveysFlow.UserSurveyDetails.resId),
                    onNewSurvey = onNewSurvey
                )
            }
            composable(NavFlow.ProfileFlow.route) {
                ProfileFlow(
                    stringResource(NavFlow.ProfileFlow.Profile.resId),
                    stringResource(NavFlow.ProfileFlow.Statistics.resId),
                    onSettings = onSettings
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SurveyFeedFlow(
    surveyFeedTitle: String,
    surveyDetailsTitle: String,
) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = NavFlow.SurveyFeedFlow.SurveyFeed.route
    ) {
        composable(NavFlow.SurveyFeedFlow.SurveyFeed.route) {
            SurveyFeedScreen(
                title = surveyFeedTitle,
                onItem = { navController.navigate(NavFlow.SurveyFeedFlow.SurveyDetails.route) }
            )
        }
        composable(NavFlow.SurveyFeedFlow.SurveyDetails.route) {
            SurveyDetailsScreen(
                title = surveyDetailsTitle
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun UserSurveysFlow(
    userSurveysTitle: String,
    userSurveyDetailsTitle: String,
    onNewSurvey: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = NavFlow.SurveyFeedFlow.SurveyFeed.route
    ) {
        composable(NavFlow.SurveyFeedFlow.SurveyFeed.route) {
            UserSurveysScreen(
                title = userSurveysTitle,
                onItem = { navController.navigate(NavFlow.UserSurveysFlow.UserSurveyDetails.route) },
                onNewSurvey = onNewSurvey
            )
        }
        composable(NavFlow.UserSurveysFlow.UserSurveyDetails.route) {
            SurveyDetailsScreen(
                title = userSurveyDetailsTitle
            )
        }
    }
}

@Composable
private fun GetIcon(screen: AppNavigation.Screen) {
    val iconRes = screen.getIconRes()
    Icon(
        painter = painterResource(iconRes.first),
        contentDescription = stringResource(iconRes.second)
    )
}

private fun AppNavigation.Screen.getIconRes() = when (this) {
    is NavFlow.SurveyFeedFlow -> R.drawable.ic_feed to resId
    is NavFlow.UserSurveysFlow -> R.drawable.ic_user_surveys to resId
    is NavFlow.ProfileFlow -> R.drawable.ic_profile to resId
    else -> error("No resources provided for this screen: $this")
}

@Composable
private fun AuthScreen(onLogIn: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Please log in to continue")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onLogIn
            ) {
                Text("Log in")
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SurveyFeedScreen(
    title: String,
    names: List<String> = List(100) { "$it" },
    onItem: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(scaffoldState = scaffoldState) {
        TopAppBar(
            title = { Text(title) },
            actions = {
                IconButton(onClick = {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(message = "Search")
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Search, stringResource(R.string.new_survey))
                }
            }
        )
        LazyColumn(modifier = Modifier.padding(top = 56.dp)) {
            items(items = names) { name ->
                SurveyItem(name = name) { onItem() }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SurveyItem(
    name: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = "Survey, ")
                Text(
                    text = name,
                    style = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun UserSurveysScreen(
    title: String,
    names: List<String> = List(100) { "$it" },
    onItem: () -> Unit,
    onNewSurvey: () -> Unit
) {
    Scaffold {
        TopAppBar(
            title = { Text(title) },
            actions = {
                IconButton(onClick = onNewSurvey) {
                    Icon(imageVector = Icons.Filled.Add, stringResource(R.string.new_survey))
                }
            }
        )
        LazyColumn(modifier = Modifier.padding(top = 56.dp)) {
            items(items = names) { name ->
                UserSurveyItem(name = name) { onItem() }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun UserSurveyItem(
    name: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = "User Survey, ")
                Text(
                    text = name,
                    style = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileFlow(
    profileTitle: String,
    statisticsTitle: String,
    onSettings: () -> Unit
) {
    val navController = rememberNavController()
    // TODO: Move this to SurveyFeedFlow 
    NavHost(
        navController,
        startDestination = NavFlow.ProfileFlow.Profile.route
    ) {
        composable(NavFlow.ProfileFlow.Profile.route) {
            ProfileScreen(
                title = profileTitle,
                onSettings = onSettings,
                onStatistics = { navController.navigate(NavFlow.ProfileFlow.Statistics.route) }
            )
        }
        composable(NavFlow.ProfileFlow.Statistics.route) {
            StatisticsScreen(
                title = statisticsTitle
            )
        }
    }
}

@Composable
private fun ProfileScreen(
    title: String,
    onSettings: () -> Unit,
    onStatistics: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(imageVector = Icons.Outlined.Settings, stringResource(R.string.settings))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            ClickableText(
                text = AnnotatedString("Click to view statistics"),
                style = TextStyle(fontSize = 18.sp),
                onClick = { onStatistics() }
            )
        }
    }
}

@Composable
private fun SurveyDetailsScreen(
    title: String
) {
    Surface {
        Scaffold {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
        }
    }
}

@Composable
private fun UserSurveyDetailsScreen(
    title: String
) {
    Surface {
        Scaffold {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
        }
    }
}

@Composable
private fun SettingsScreen(title: String) {
    Surface {
        Scaffold {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
        }
    }
}

@Composable
private fun StatisticsScreen(title: String) {
    Surface {
        Scaffold {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
        }
    }
}

@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun NavPreview() {
    SurveyServiceTheme {
        NavFlow(
            onNewSurvey = { /*TODO*/ },
            onSettings = { /*TODO*/ }
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 760)
@Composable
fun AuthPreview() {
    SurveyServiceTheme {
        AuthScreen(onLogIn = {})
    }
}
