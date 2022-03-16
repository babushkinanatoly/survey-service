package ru.babushkinanatoly.feature_user_surveys.usersurveydetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.core_api.UserSurvey
import ru.babushkinanatoly.core_api.Vote
import ru.babushkinanatoly.feature_user_surveys.R
import kotlin.random.Random

@Composable
internal fun UserSurveyDetailsScreen(
    userSurveyDetailsModel: UserSurveyDetailsModel,
) {
    val state by userSurveyDetailsModel.state.collectAsState()
    Surface {
        Scaffold {
            TopAppBar(
                title = { Text(stringResource(R.string.survey_details)) },
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
            Text(state.userSurvey.title)
            Text(state.userSurvey.desc)
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "UserSurveyDetailsScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun UserSurveyDetailsScreenPreview() {
    SurveyServiceTheme {
        UserSurveyDetailsScreen(
            object : UserSurveyDetailsModel {
                override val state = MutableStateFlow(
                    UserSurveyDetailsState(
                        UserSurvey(
                            0, "Title", "Desc",
                            (1..9L).toList().map {
                                Vote(it, Random.nextBoolean())
                            }
                        )
                    )
                )
            }
        )
    }
}
