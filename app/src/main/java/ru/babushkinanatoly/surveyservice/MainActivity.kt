package ru.babushkinanatoly.surveyservice

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Companion.bottomNavItems
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen.*
import ru.babushkinanatoly.surveyservice.ui.theme.SurveyServiceTheme
import ru.babushkinanatoly.surveyservice.util.Event
import ru.babushkinanatoly.surveyservice.util.MutableEvent
import ru.babushkinanatoly.surveyservice.util.consumeAsEffect
import ru.babushkinanatoly.surveyservice.util.dispatch

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
        startDestination = if (shouldShowAuth) Auth.route else NavWorkflow.route,
    ) {
        // TODO: 1/20/2022 Think of how it will be related with data from view models
        composable(Auth.route) {
            AuthScreen {
                navController.navigate(NavWorkflow.route)
            }
        }
        composable(NavWorkflow.route) {
            NavWorkflow(
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
private fun NavWorkflow(
    onNewSurvey: () -> Unit,
    onSettings: () -> Unit
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
                bottomNavItems.forEach { screen ->
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
                SurveyFeedWorkflow(
                    fallbackToSurveyFeedRoot,
                    stringResource(NavWorkflow.SurveyFeedWorkflow.SurveyFeed.resId),
                    stringResource(NavWorkflow.SurveyFeedWorkflow.SurveyDetails.resId)
                )
            }
            composable(NavWorkflow.UserSurveysWorkflow.route) {
                UserSurveysWorkflow(
                    fallbackToUserSurveysRoot,
                    stringResource(NavWorkflow.UserSurveysWorkflow.UserSurveys.resId),
                    stringResource(NavWorkflow.UserSurveysWorkflow.UserSurveyDetails.resId),
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
                    onSettings = onSettings
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
@Composable
fun UserSurveysWorkflow(
    fallbackToRoot: Event<Unit>,
    userSurveysTitle: String,
    userSurveyDetailsTitle: String,
    onBack: () -> Unit,
    onNewSurvey: () -> Unit
) {
    val navController = rememberNavController()
    val scrollSurveysUp = remember { MutableEvent<Unit>() }
    var backEnabled by rememberSaveable { mutableStateOf(true) }
    BackHandler(enabled = backEnabled) { onBack() }
    NavHost(
        navController,
        startDestination = NavWorkflow.SurveyFeedWorkflow.SurveyFeed.route
    ) {
        composable(NavWorkflow.SurveyFeedWorkflow.SurveyFeed.route) {
            backEnabled = true
            UserSurveysScreen(
                scrollSurveysUp,
                title = userSurveysTitle,
                onItem = { navController.navigate(NavWorkflow.UserSurveysWorkflow.UserSurveyDetails.route) },
                onNewSurvey = onNewSurvey
            )
        }
        composable(NavWorkflow.UserSurveysWorkflow.UserSurveyDetails.route) {
            backEnabled = false
            SurveyDetailsScreen(
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

@Composable
private fun AuthScreen(
    onLogIn: () -> Unit
) {
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
    scrollUp: Event<Unit>,
    title: String,
    names: List<String> = List(100) { "$it" },
    onItem: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val surveysState = rememberLazyListState()
    Scaffold(scaffoldState = scaffoldState) {
        TopAppBar(
            title = { Text(title) },
            actions = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(message = "Search")
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Search, stringResource(R.string.new_survey))
                }
            }
        )
        LazyColumn(
            state = surveysState,
            modifier = Modifier.padding(top = 56.dp)
        ) {
            items(items = names) { name ->
                SurveyItem(name = name) { onItem() }
            }
        }
    }
    scrollUp.consumeAsEffect {
        coroutineScope.launch {
            surveysState.animateScrollToItem(index = 0)
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
    scrollUp: Event<Unit>,
    title: String,
    names: List<String> = List(100) { "$it" },
    onItem: () -> Unit,
    onNewSurvey: () -> Unit
) {
    val surveysState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold {
        TopAppBar(
            title = { Text(title) },
            actions = {
                IconButton(onClick = onNewSurvey) {
                    Icon(imageVector = Icons.Filled.Add, stringResource(R.string.new_survey))
                }
            }
        )
        LazyColumn(
            state = surveysState,
            modifier = Modifier.padding(top = 56.dp)
        ) {
            items(items = names) { name ->
                UserSurveyItem(name = name) { onItem() }
            }
        }
    }
    scrollUp.consumeAsEffect {
        coroutineScope.launch {
            surveysState.animateScrollToItem(index = 0)
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
private fun ProfileWorkflow(
    fallbackToRoot: Event<Unit>,
    profileTitle: String,
    statisticsTitle: String,
    onBack: () -> Unit,
    onSettings: () -> Unit
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
private fun SettingsScreen(
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
private fun StatisticsScreen(
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
        NavWorkflow(
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
