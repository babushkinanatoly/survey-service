package ru.babushkinanatoly.feature_user_surveys.usersurveydetails

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import ru.babushkinanatoly.core_api.UserSurvey
import ru.babushkinanatoly.feature_user_surveys.R

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
                },
                actions = {
                    IconButton(
                        enabled = !state.processing,
                        onClick = userSurveyDetailsModel::onDelete
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, stringResource(R.string.delete))
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
                    editable = state.titleEditable,
                    onEdit = userSurveyDetailsModel::onTitleEdit,
                    onValueChange = userSurveyDetailsModel::onTitleChange,
                    onValueUpdate = userSurveyDetailsModel::onTitleUpdate
                )
                EditableTextField(
                    text = state.userSurvey.desc,
                    editable = state.descEditable,
                    onEdit = userSurveyDetailsModel::onDescEdit,
                    onValueChange = userSurveyDetailsModel::onDescChange,
                    onValueUpdate = userSurveyDetailsModel::onDescUpdate
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    VoteBox(
                        text = stringResource(R.string.yes),
                        count = state.userSurvey.upvotes.size
                    )
                    VoteBox(
                        text = stringResource(R.string.no),
                        count = state.userSurvey.downvotes.size
                    )
                }
            }
            if (state.processing) {
                SurveyUpdatingProgressBar()
            }
        }
    }
    userSurveyDetailsModel.event.consumeAsEffect {
        when (it) {
            is UserSurveyDetailsEvent.Error -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
            UserSurveyDetailsEvent.Deleted -> context.goBack()
        }
    }
}

@Composable
private fun SurveyUpdatingProgressBar() {
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
private fun EditableTextField(
    text: String,
    editable: Boolean,
    onEdit: () -> Unit,
    onValueChange: (String) -> Unit,
    onValueUpdate: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
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
                if (editable) {
                    focusManager.clearFocus()
                    onValueUpdate(text)
                } else {
                    onEdit()
                }
            }
        ) {
            Icon(if (editable) Icons.Filled.Check else Icons.Filled.Edit, stringResource(R.string.edit))
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
                        userSurvey = UserSurvey(
                            "0",
                            "Title",
                            "Desc",
                            listOf("1", "2"),
                            listOf("1", "2", "3", "4")
                        ),
                        titleEditable = false,
                        descEditable = false,
                        processing = false
                    )
                )
                override val event: Event<UserSurveyDetailsEvent>
                    get() = TODO("Not yet implemented")

                override fun onTitleEdit() {}
                override fun onDescEdit() {}
                override fun onTitleChange(title: String) {}
                override fun onDescChange(desc: String) {}
                override fun onTitleUpdate(title: String) {}
                override fun onDescUpdate(desc: String) {}
                override fun onDelete() {}
            }
        )
    }
}
