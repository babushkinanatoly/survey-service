package ru.babushkinanatoly.feature_survey_feed.surveyfeed

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.Event
import ru.babushkinanatoly.base_feature.util.MutableEvent
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.feature_survey_feed.R

@Composable
internal fun SurveyFeedScreen(
    scrollUp: Event<Unit>,
    names: List<String> = List(100) { "$it" },
    onItem: () -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val surveysState = rememberLazyListState()
    Scaffold(scaffoldState = scaffoldState) {
        TopAppBar(
            title = { Text(stringResource(NavWorkflow.SurveyFeedWorkflow.SurveyFeed.resId)) },
            actions = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(message = "Search")
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Search, stringResource(R.string.search))
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

@Composable
private fun SurveyItem(
    name: String,
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
        SurveyFeedScreen(MutableEvent(), List(100) { "$it" }) {}
    }
}
