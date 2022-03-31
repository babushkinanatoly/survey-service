package ru.babushkinanatoly.feature_new_survey

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NewSurvey
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.base_feature.util.goBack
import ru.babushkinanatoly.feature_new_survey.di.NewSurveyViewModel

@Composable
fun NewSurveyScreen() {
    val model = viewModel<NewSurveyViewModel>().newSurveyComponent.provideModel()
    val state by model.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val errorMsg = stringResource(R.string.error_no_connection)
    Surface {
        Scaffold(
            modifier = Modifier
                .systemBarsPadding()
                .imePadding(),
            topBar = {
                AppBar(
                    createEnabled = state.createEnabled,
                    onBack = {
                        focusManager.clearFocus()
                        context.goBack()
                    },
                    onCreate = {
                        focusManager.clearFocus()
                        model.onCreate()
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, start = 18.dp, end = 18.dp, bottom = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DataField(
                    modifier = Modifier.requiredSizeIn(minHeight = 96.dp),
                    text = state.title,
                    maxLines = 2,
                    placeholder = stringResource(R.string.title),
                    error = state.titleError,
                    isError = state.titleError.isNotBlank(),
                    onValueChange = model::onTitleChange
                )
                DataField(
                    modifier = Modifier.requiredSizeIn(minHeight = 212.dp),
                    text = state.desc,
                    maxLines = 8,
                    placeholder = stringResource(R.string.desc),
                    error = state.descError,
                    isError = state.descError.isNotBlank(),
                    onValueChange = model::onDescChange
                )
            }
            if (state.creating) {
                NewSurveyProgressBar()
            }
        }
    }
    model.event.consumeAsEffect {
        when (it) {
            NewSurveyEvent.CREATED -> context.goBack()
            NewSurveyEvent.ERROR -> Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun NewSurveyProgressBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface.copy(alpha = ContentAlpha.medium))
            .pointerInput(Unit) {}
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AppBar(
    createEnabled: Boolean,
    onBack: () -> Unit,
    onCreate: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(NewSurvey.resId)) },
        backgroundColor = MaterialTheme.colors.background,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
            }
        },
        actions = {
            IconButton(
                enabled = createEnabled,
                onClick = onCreate
            ) {
                Icon(imageVector = Icons.Filled.Done, stringResource(R.string.create))
            }
        }
    )
}

@Composable
private fun DataField(
    modifier: Modifier,
    text: String,
    maxLines: Int,
    placeholder: String,
    error: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            modifier = modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            value = text,
            maxLines = maxLines,
            isError = isError,
            onValueChange = onValueChange,
            label = { Text(placeholder) },
        )
        ErrorText(
            text = error,
            visible = isError
        )
    }
}

@Composable
private fun ErrorText(
    text: String = "Error message",
    visible: Boolean = false,
) {
    val alpha by animateFloatAsState(if (visible) 1f else 0f)
    Text(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .alpha(alpha),
        text = text,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption,
    )
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "NewSurveyPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun NewSurveyPreview() {
    SurveyServiceTheme {
        NewSurveyScreen()
    }
}
