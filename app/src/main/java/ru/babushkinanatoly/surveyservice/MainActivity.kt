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
import ru.babushkinanatoly.surveyservice.AppNavigation.Screen
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme

private open class AppNavigation(val route: String, @StringRes val resId: Int) {

    companion object {

        val bottomNavItems = listOf(
            Screen.NavFlow.SurveyFeed,
            Screen.NavFlow.UserSurveys,
            Screen.NavFlow.ProfileFlow
        )
    }

    sealed class Screen(screenRoute: String, screenResId: Int) : AppNavigation(screenRoute, screenResId) {

        object Auth : Screen("auth", R.string.auth)
        object NewSurvey : Screen("newsurvey", R.string.new_survey)
        object Settings : Screen("settings", R.string.settings)
        object SurveyDetails : Screen("surveydetails", R.string.survey_details)
        object UserSurveyDetails : Screen("usersurveydetails", R.string.user_survey_details)

        object NavFlow : Screen("navflow", R.string.navigation) {
            object SurveyFeed : Screen("surveyfeed", R.string.survey_feed)
            object UserSurveys : Screen("usersurveys", R.string.user_surveys)

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
private fun SurveyServiceApp(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val shouldShowAuth by rememberSaveable { mutableStateOf(!isLoggedIn) }
    NavHost(
        navController,
        startDestination = if (shouldShowAuth) Screen.Auth.route else Screen.NavFlow.route,
    ) {
        // TODO: 1/20/2022 Think of how it will be related with data from view models
        composable(Screen.Auth.route) {
            AuthScreen {
                navController.navigate(Screen.NavFlow.route)
            }
        }
        composable(Screen.NavFlow.route) {
            NavFlow(
                onFeedItem = { navController.navigate(Screen.SurveyDetails.route) },
                onUserSurveysItem = { navController.navigate(Screen.UserSurveyDetails.route) },
                onNewSurvey = { navController.navigate(Screen.NewSurvey.route) },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.SurveyDetails.route) {
            SurveyDetailsScreen(stringResource(Screen.SurveyDetails.resId))
        }
        composable(Screen.UserSurveyDetails.route) {
            UserSurveyDetailsScreen(stringResource(Screen.UserSurveyDetails.resId))
        }
        composable(Screen.NewSurvey.route) {
            UserSurveyDetailsScreen(stringResource(Screen.NewSurvey.resId))
        }
        composable(Screen.Settings.route) {
            SettingsScreen(stringResource(Screen.Settings.resId))
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun NavFlow(
    onFeedItem: () -> Unit,
    onUserSurveysItem: () -> Unit,
    onNewSurvey: () -> Unit,
    onSettings: () -> Unit
) {
    val navController = rememberNavController()
    // TODO: declare an event and children will consume
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
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select bottomNavItems
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                // TODO: ...
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
            startDestination = Screen.NavFlow.SurveyFeed.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.NavFlow.SurveyFeed.route) {
                SurveyFeedScreen {
                    onFeedItem()
                }
            }
            composable(Screen.NavFlow.UserSurveys.route) {
                UserSurveysScreen(
                    onItem = onUserSurveysItem,
                    onNewSurvey = onNewSurvey
                )
            }
            composable(Screen.NavFlow.ProfileFlow.route) {
                ProfileFlow(
                    stringResource(Screen.NavFlow.ProfileFlow.Profile.resId),
                    stringResource(Screen.NavFlow.ProfileFlow.Statistics.resId),
                    onSettings = onSettings
                )
            }
        }
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
    is Screen.NavFlow.SurveyFeed -> R.drawable.ic_feed to resId
    is Screen.NavFlow.UserSurveys -> R.drawable.ic_user_surveys to resId
    is Screen.NavFlow.ProfileFlow -> R.drawable.ic_profile to resId
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
    names: List<String> = List(100) { "$it" },
    onItem: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(scaffoldState = scaffoldState) {
        TopAppBar(
            title = { Text(text = "Survey Feed") },
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
    names: List<String> = List(100) { "$it" },
    onItem: () -> Unit,
    onNewSurvey: () -> Unit
) {
    Scaffold {
        TopAppBar(
            title = { Text(text = "User Surveys") },
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
    NavHost(
        navController,
        startDestination = Screen.NavFlow.ProfileFlow.Profile.route
    ) {
        composable(Screen.NavFlow.ProfileFlow.Profile.route) {
            ProfileScreen(
                title = profileTitle,
                onSettings = onSettings,
                onStatistics = { navController.navigate(Screen.NavFlow.ProfileFlow.Statistics.route) }
            )
        }
        composable(Screen.NavFlow.ProfileFlow.Statistics.route) {
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
private fun SurveyDetailsScreen(title: String) {
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
private fun UserSurveyDetailsScreen(title: String) {
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
            onFeedItem = { /*TODO*/ },
            onUserSurveysItem = { /*TODO*/ },
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
