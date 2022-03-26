package ru.babushkinanatoly.feature_user_surveys.usersurveydetails

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.goBack
import ru.babushkinanatoly.core_api.UserSurvey
import ru.babushkinanatoly.core_api.Vote
import ru.babushkinanatoly.feature_user_surveys.R
import kotlin.random.Random

@Composable
internal fun UserSurveyDetailsScreen(
    userSurveyDetailsModel: UserSurveyDetailsModel,
) {
    val state by userSurveyDetailsModel.state.collectAsState()
    val context = LocalContext.current
    Surface {
        Scaffold {
            TopAppBar(
                title = { Text(stringResource(R.string.survey_details)) },
                navigationIcon = {
                    IconButton(onClick = { context.goBack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
            Column(
                modifier = Modifier
                    .padding(top = 56.dp, start = 8.dp, end = 8.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EditableTextField(
                    text = state.userSurvey.title,
                    onValueChange = userSurveyDetailsModel::onTitleChange,
                    onValueUpdate = userSurveyDetailsModel::onTitleUpdate
                )
                EditableTextField(
                    text = state.userSurvey.desc,
                    onValueChange = userSurveyDetailsModel::onDescChange,
                    onValueUpdate = userSurveyDetailsModel::onDescUpdate
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    VoteBox(
                        text = stringResource(R.string.yes),
                        count = state.userSurvey.votes.filter { it.value }.size
                    )
                    VoteBox(
                        text = stringResource(R.string.no),
                        count = state.userSurvey.votes.filter { !it.value }.size
                    )
                }
            }
        }
    }
}

@Composable
private fun EditableTextField(
    text: String,
    onValueChange: (String) -> Unit,
    onValueUpdate: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var editable by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            enabled = editable,
            value = text,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        IconButton(
            onClick = {
                editable = !editable
                if (!editable) {
                    focusManager.clearFocus()
                    onValueUpdate(text)
                }
            }
        ) {
            Icon(
                if (editable) {
                    Icons.Filled.Check
                } else {
                    Icons.Filled.Edit
                },
                stringResource(R.string.edit)
            )
        }
        // TODO: Commented out, because we have an internal compose crash after the second attempt
        //       to edit the field.
//        DisposableEffect(editable) {
//            if (editable) {
//                focusRequester.requestFocus()
//            }
//            onDispose {}
//        }
    }
}

@Composable
private fun RowScope.VoteBox(
    text: String,
    count: Int,
) {
    Text(
        text = "$text\n$count",
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier = Modifier
            .weight(1f)
            .padding(8.dp)
            .border(width = 4.dp, color = MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
            .padding(16.dp)
    )
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

                override fun onTitleChange(title: String) {}
                override fun onDescChange(desc: String) {}
                override fun onTitleUpdate(title: String) {}
                override fun onDescUpdate(desc: String) {}
            }
        )
    }
}
