package ru.babushkinanatoly.feature_user_surveys

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.Event
import ru.babushkinanatoly.base_feature.util.MutableEvent
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.UserSurvey
import ru.babushkinanatoly.core_api.Vote
import ru.babushkinanatoly.feature_user_surveys.usersurveys.UserSurveysModel
import ru.babushkinanatoly.feature_user_surveys.usersurveys.UserSurveysState
import kotlin.random.Random

@Composable
internal fun UserSurveysScreen(
    userSurveysModel: UserSurveysModel,
    scrollUp: Event<Unit>,
    onItem: () -> Unit,
    onNewSurvey: () -> Unit,
) {
    val state by userSurveysModel.state.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold {
        TopAppBar(
            title = { Text(stringResource(NavWorkflow.UserSurveysWorkflow.UserSurveys.resId)) },
            actions = {
                IconButton(onClick = onNewSurvey) {
                    Icon(imageVector = Icons.Filled.Add, stringResource(R.string.new_survey))
                }
            }
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(top = 56.dp)
        ) {
            items(items = state.surveys) { userSurvey ->
                UserSurveyItem(userSurvey.title, userSurvey.desc) { onItem() }
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
private fun UserSurveyItem(
    title: String,
    desc: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = title,
                maxLines = 2,
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = desc,
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
                                index,
                                "Title 1",
                                "Desc 1",
                                (0..20L).toList().map { Vote(it, Random.nextBoolean()) }
                            )
                        }
                    )
                )
            },
            MutableEvent(), {}, {}
        )
    }
}
