package ru.babushkinanatoly.feature_survey_feed.surveydetails

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.base_feature.util.goBack
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.core_api.Survey
import ru.babushkinanatoly.feature_survey_feed.R

@Composable
internal fun SurveyDetailsScreen(
    surveyDetailsModel: SurveyDetailsModel,
) {
    val state by surveyDetailsModel.state.collectAsState()
    val context = LocalContext.current
    Surface {
        Scaffold {
            AppBar { context.goBack() }
            when (state) {
                is SurveyDetailsState.Data -> {
                    val data = state as SurveyDetailsState.Data
                    SurveyDetails(
                        survey = data.survey,
                        voting = data.voting,
                        onYes = surveyDetailsModel::onYes,
                        onNo = surveyDetailsModel::onNo
                    )
                }
                SurveyDetailsState.Loading -> Loading()
                SurveyDetailsState.LoadingError -> LoadingError(onRetry = surveyDetailsModel::onReloadSurvey)
            }
        }
    }
    surveyDetailsModel.event.consumeAsEffect {
        when (it) {
            is SurveyDetailsEvent.Error -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun AppBar(
    onBack: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.survey_details)) },
        backgroundColor = MaterialTheme.colors.background,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
            }
        }
    )
}

@Composable
private fun SurveyDetails(
    survey: Survey,
    voting: Boolean,
    onYes: () -> Unit,
    onNo: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(top = 56.dp, start = 8.dp, end = 8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            text = survey.title,
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            text = survey.desc
        )
        Row(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            VoteBox(
                text = stringResource(R.string.yes),
                count = survey.upvotes.size,
                voted = survey.userVote == true,
                onClick = onYes
            )
            VoteBox(
                text = stringResource(R.string.no),
                count = survey.downvotes.size,
                voted = survey.userVote == false,
                onClick = onNo
            )
        }
    }
    if (voting) {
        SurveyDetailsProgressBar()
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
        Button(
            modifier = Modifier.padding(vertical = 12.dp),
            onClick = onRetry
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun RowScope.VoteBox(
    text: String,
    count: Int,
    voted: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = "$text\n$count",
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier = Modifier
            .weight(1f)
            .padding(8.dp)
            .background(if (voted) MaterialTheme.colors.primary else Color.Transparent, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(width = 4.dp, color = MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
            .padding(16.dp)
    )
}

@Composable
private fun SurveyDetailsProgressBar() {
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

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "SurveyDetailsScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun SurveyDetailsScreenPreview() {
    SurveyServiceTheme {
        SurveyDetailsScreen(
            object : SurveyDetailsModel {

                override val state = MutableStateFlow(
                    SurveyDetailsState.Data(
                        Survey(
                            "0",
                            "Title",
                            "Desc",
                            listOf("1", "2"),
                            listOf("1", "2", "3", "4"),
                            true
                        ),
                        false
                    )
                )

                override val event: Event<SurveyDetailsEvent>
                    get() = TODO("Not yet implemented")

                override fun onReloadSurvey() {
                    TODO("Not yet implemented")
                }

                override fun onYes() {
                    TODO("Not yet implemented")
                }

                override fun onNo() {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "SurveyDetailsScreenLoadingErrorPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun SurveyDetailsLoadingErrorScreenPreview() {
    SurveyServiceTheme {
        SurveyDetailsScreen(
            object : SurveyDetailsModel {

                override val state = MutableStateFlow(SurveyDetailsState.LoadingError)

                override val event: Event<SurveyDetailsEvent>
                    get() = TODO("Not yet implemented")

                override fun onReloadSurvey() {
                    TODO("Not yet implemented")
                }

                override fun onYes() {
                    TODO("Not yet implemented")
                }

                override fun onNo() {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}
