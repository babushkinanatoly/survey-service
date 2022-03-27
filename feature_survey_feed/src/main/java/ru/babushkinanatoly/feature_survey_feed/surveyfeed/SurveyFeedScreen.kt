package ru.babushkinanatoly.feature_survey_feed.surveyfeed

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.core_api.MutableEvent
import ru.babushkinanatoly.core_api.Survey
import ru.babushkinanatoly.feature_survey_feed.R

@Composable
internal fun SurveyFeedScreen(
    surveyFeedModel: SurveyFeedModel,
    scrollUp: Event<Unit>,
    onItem: (id: String) -> Unit,
) {
    val state by surveyFeedModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val surveysState = rememberLazyListState()
    val context = LocalContext.current
    Scaffold(scaffoldState = scaffoldState) {
        AppBar(
            refreshing = state.refreshing,
            onRefresh = surveyFeedModel::refresh
        )
        SurveyFeed(
            surveys = state.surveys,
            listState = surveysState,
            loadingMore = state.loadingMore,
            onItem = { onItem(it) },
            onRetry = surveyFeedModel::refresh,
            onLoadMore = surveyFeedModel::loadMore
        )
        if (state.refreshing) {
            FeedProgressBar()
        }
    }
    scrollUp.consumeAsEffect {
        coroutineScope.launch {
            surveysState.animateScrollToItem(0)
        }
    }
    surveyFeedModel.event.consumeAsEffect {
        when (it) {
            is FeedEvent.Error -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun AppBar(
    refreshing: Boolean,
    onRefresh: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(NavWorkflow.SurveyFeedWorkflow.SurveyFeed.resId)) },
        actions = {
            IconButton(
                enabled = !refreshing,
                onClick = onRefresh
            ) {
                Icon(imageVector = Icons.Filled.Refresh, stringResource(R.string.refresh))
            }
        }
    )
}

@Composable
private fun SurveyFeed(
    surveys: List<Survey>?,
    listState: LazyListState,
    loadingMore: Boolean,
    onItem: (id: String) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
) {
    if (surveys == null) {
        LoadingError { onRetry() }
    } else {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 56.dp)
        ) {
            items(surveys) { survey ->
                SurveyItem(survey) { onItem(survey.id) }
            }
            item {
                if (loadingMore) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                } else if (surveys.isNotEmpty()) {
                    // TODO: Hide if no new surveys added
                    Button(
                        modifier = Modifier.padding(24.dp),
                        onClick = onLoadMore
                    ) {
                        Text(stringResource(R.string.load_more))
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedProgressBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp)
            .background(MaterialTheme.colors.surface.copy(alpha = ContentAlpha.medium))
            .pointerInput(Unit) {}
    ) {
        CircularProgressIndicator()
    }
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
                    SurveyFeedState(
                        surveys = buildList {
                            (0..9).toList().map { value ->
                                add(
                                    Survey(
                                        value.toString(),
                                        "Survey $value",
                                        "Survey $value desc",
                                        listOf("1", "2"),
                                        listOf("1", "2", "3", "4"),
                                        null
                                    ),
                                )
                            }
                        },
                        refreshing = false,
                        loadingMore = false
                    )
                )

                override val event: Event<FeedEvent>
                    get() = TODO("Not yet implemented")

                override fun refresh() {
                    TODO("Not yet implemented")
                }

                override fun loadMore() {
                    TODO("Not yet implemented")
                }
            }, MutableEvent()
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

                override val state = MutableStateFlow(
                    SurveyFeedState(
                        surveys = null,
                        refreshing = true,
                        loadingMore = false
                    )
                )

                override val event: Event<FeedEvent>
                    get() = TODO("Not yet implemented")

                override fun refresh() {
                    TODO("Not yet implemented")
                }

                override fun loadMore() {
                    TODO("Not yet implemented")
                }
            }, MutableEvent()
        ) {}
    }
}
