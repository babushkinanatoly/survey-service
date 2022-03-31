package ru.babushkinanatoly.feature_user_surveys.usersurveys

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.core_api.MutableEvent
import ru.babushkinanatoly.core_api.UserSurvey
import ru.babushkinanatoly.feature_user_surveys.R

@Composable
internal fun UserSurveysScreen(
    userSurveysModel: UserSurveysModel,
    scrollUp: Event<Unit>,
    onItem: (id: String) -> Unit,
    onNewSurvey: () -> Unit,
) {
    val state by userSurveysModel.state.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(NavWorkflow.UserSurveysWorkflow.UserSurveys.resId)) },
                backgroundColor = MaterialTheme.colors.background,
                actions = {
                    IconButton(onClick = onNewSurvey) {
                        Icon(imageVector = Icons.Filled.Add, stringResource(R.string.new_survey))
                    }
                }
            )
        }
    ) {
        if (state.userSurveys.isEmpty()) {
            UserSurveysEmptyView()
        } else {
            LazyColumn(state = listState) {
                itemsIndexed(items = state.userSurveys) { index, userSurvey ->
                    UserSurveyItem(
                        isFirst = index == 0,
                        isLast = index == state.userSurveys.size - 1,
                        userSurvey = userSurvey
                    ) { onItem(it) }
                }
            }
        }
    }
    scrollUp.consumeAsEffect {
        coroutineScope.launch {
            listState.animateScrollToItem(index = 0)
        }
    }
}

@Composable
private fun UserSurveysEmptyView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.user_surveys_empty_view),
            fontSize = 18.sp
        )
    }
}

@Composable
private fun UserSurveyItem(
    isFirst: Boolean,
    isLast: Boolean,
    userSurvey: UserSurvey,
    onClick: (id: String) -> Unit,
) {
    Card(
        modifier = Modifier.padding(
            top = if (isFirst) 8.dp else 4.dp,
            bottom = if (isLast) 8.dp else 4.dp,
            start = 8.dp,
            end = 8.dp
        ),
        backgroundColor = MaterialTheme.colors.background,
        elevation = 8.dp,
        onClick = { onClick(userSurvey.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = userSurvey.title,
                maxLines = 2,
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = userSurvey.desc,
                maxLines = 4
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "UserSurveysScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun UserSurveysScreenPreview() {
    SurveyServiceTheme {
        UserSurveysScreen(
            object : UserSurveysModel {
                override val state = MutableStateFlow(
                    UserSurveysState(
                        (0..20L).toList().map { index ->
                            UserSurvey(
                                index.toString(),
                                "Title 1",
                                "Desc 1",
                                listOf(),
                                listOf()
                            )
                        }
                    )
                )
            },
            MutableEvent(), {}, {}
        )
    }
}
