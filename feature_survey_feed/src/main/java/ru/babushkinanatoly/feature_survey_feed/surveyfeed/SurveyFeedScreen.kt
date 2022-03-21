package ru.babushkinanatoly.feature_survey_feed.surveyfeed

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.Event
import ru.babushkinanatoly.base_feature.util.MutableEvent
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.Survey
import ru.babushkinanatoly.core_api.Vote
import ru.babushkinanatoly.feature_survey_feed.R
import kotlin.random.Random

@Composable
internal fun SurveyFeedScreen(
    surveyFeedModel: SurveyFeedModel,
    scrollUp: Event<Unit>,
    onItem: (id: Long) -> Unit,
) {
    val state by surveyFeedModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val surveysState = rememberLazyListState()
    Scaffold(scaffoldState = scaffoldState) {
        AppBar(coroutineScope, scaffoldState)
        when (state) {
            SurveyFeedState.LoadingError -> LoadingError(surveyFeedModel::reloadSurveys)
            SurveyFeedState.Loading -> Loading()
            is SurveyFeedState.Data -> {
                SurveyFeed(
                    surveys = (state as SurveyFeedState.Data).surveys,
                    listState = surveysState,
                    onClick = { onItem(it) }
                )
            }
        }
    }
    scrollUp.consumeAsEffect {
        coroutineScope.launch {
            surveysState.animateScrollToItem(0)
        }
    }
}

@Composable
private fun AppBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
) {
    TopAppBar(
        title = { Text(stringResource(NavWorkflow.SurveyFeedWorkflow.SurveyFeed.resId)) },
        actions = {
            val searchText = stringResource(R.string.search)
            IconButton(
                onClick = {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(searchText)
                    }
                }
            ) {
                Icon(imageVector = Icons.Filled.Search, searchText)
            }
        }
    )
}

@Composable
private fun LoadingError(
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp, start = 24.dp, end = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(vertical = 12.dp),
            text = stringResource(R.string.error_loading),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Text(
            modifier = Modifier.padding(vertical = 12.dp),
            text = stringResource(R.string.error_no_connection),
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier.padding(vertical = 12.dp),
            onClick = onRetry
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun Loading() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SurveyFeed(
    surveys: List<Survey>,
    listState: LazyListState,
    onClick: (id: Long) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.padding(top = 56.dp)
    ) {
        items(surveys) { survey ->
            SurveyItem(survey) { onClick(survey.id) }
        }
    }
}

@Composable
private fun SurveyItem(
    survey: Survey,
    onClick: () -> Unit,
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
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = survey.title,
                    maxLines = 2,
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = survey.desc,
                    maxLines = 4
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "SurveyFeedScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun SurveyFeedScreenPreview() {
    SurveyServiceTheme {
        SurveyFeedScreen(
            object : SurveyFeedModel {

                override val state = MutableStateFlow(
                    SurveyFeedState.Data(
                        buildList {
                            (0..9L).toList().map { value ->
                                add(
                                    Survey(
                                        value,
                                        "Survey $value",
                                        "Survey $value desc",
                                        (0..19L).toList().map {
                                            Vote(it, Random.nextBoolean())
                                        }
                                    ),
                                )
                            }
                        }
                    )
                )

                override fun reloadSurveys() {
                    TODO("Not yet implemented")
                }
            },
            MutableEvent()
        ) {}
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "SurveyFeedScreenLoadingErrorPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun SurveyFeedScreenLoadingErrorPreview() {
    SurveyServiceTheme {
        SurveyFeedScreen(
            object : SurveyFeedModel {

                override val state = MutableStateFlow(SurveyFeedState.LoadingError)

                override fun reloadSurveys() {
                    TODO("Not yet implemented")
                }
            },
            MutableEvent()
        ) {}
    }
}
