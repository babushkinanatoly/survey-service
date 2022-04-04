package ru.babushkinanatoly.feature_profile.statistics

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow.ProfileWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.goBack
import ru.babushkinanatoly.feature_profile.R

@Composable
internal fun StatisticsScreen(
    statisticsModel: StatisticsModel,
) {
    val state by statisticsModel.state.collectAsState()
    val context = LocalContext.current
    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(ProfileWorkflow.Statistics.resId)) },
                    backgroundColor = MaterialTheme.colors.background,
                    navigationIcon = {
                        IconButton(onClick = { context.goBack() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                StatisticsField(stringResource(R.string.surveys_created), state.surveysCount.toString())
                StatisticsField(stringResource(R.string.upvotes_overall), state.upvotesCount.toString())
                StatisticsField(stringResource(R.string.downvotes_overall), state.downvotesCount.toString())
            }
        }
    }
}

@Composable
private fun StatisticsField(
    text: String,
    value: String,
) {
    Text(
        modifier = Modifier.padding(bottom = 16.dp),
        text = "$text: $value",
        fontSize = 18.sp
    )
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "StatisticsScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun StatisticsScreenPreview() {
    SurveyServiceTheme {
        Surface {
            StatisticsScreen(
                object : StatisticsModel {

                    override val state = MutableStateFlow(
                        StatisticsState(
                            surveysCount = 10,
                            upvotesCount = 20,
                            downvotesCount = 30
                        )
                    )
                }
            )
        }
    }
}
