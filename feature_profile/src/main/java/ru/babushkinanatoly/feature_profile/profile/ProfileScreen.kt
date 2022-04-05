package ru.babushkinanatoly.feature_profile.profile

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow.ProfileWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.DropDownMenu
import ru.babushkinanatoly.base_feature.util.consumeAsEffect
import ru.babushkinanatoly.core_api.MutableEvent
import ru.babushkinanatoly.feature_profile.R

@Composable
internal fun ProfileScreen(
    profileModel: ProfileModel,
    onSettings: () -> Unit,
    onLogOut: () -> Unit,
    onStatistics: () -> Unit,
) {
    val state by profileModel.state.collectAsState()
    val context = LocalContext.current
    val criticalErrorMsg = stringResource(R.string.error_general)
    val errorMsg = stringResource(R.string.error_no_connection)
    val ages = context.resources.getStringArray(R.array.age_values).toList()
    val sexes = context.resources.getStringArray(R.array.sex_values).toList()
    val countries = context.resources.getStringArray(R.array.country_values).toList()
    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(ProfileWorkflow.Profile.resId)) },
                    backgroundColor = MaterialTheme.colors.background,
                    actions = {
                        if (state.dataChanged) {
                            IconButton(onClick = profileModel::onClearChanges) {
                                Icon(Icons.Outlined.Clear, stringResource(R.string.clear))
                            }
                        }
                        IconButton(onClick = onSettings) {
                            Icon(Icons.Outlined.Settings, stringResource(R.string.settings))
                        }
                        IconButton(onClick = profileModel::onLogOut) {
                            Icon(painterResource(R.drawable.ic_log_out), stringResource(R.string.log_out))
                        }
                    }
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = state.dataChanged,
                    enter = scaleIn(),
                    exit = scaleOut(),
                ) {
                    FloatingActionButton(
                        onClick = { if (!state.saving) profileModel.onSaveChanges() }
                    ) {
                        Icon(Icons.Default.Done, stringResource(R.string.save))
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                EmailField(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    text = state.email
                )
                ProfileField(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    text = state.name,
                    label = stringResource(R.string.name),
                    onValueChanged = profileModel::onNameChange
                )
                Row(modifier = Modifier.padding(start = 24.dp, end = 24.dp)) {
                    DropDownMenu(
                        modifier = Modifier
                            .padding(bottom = 16.dp, end = 8.dp)
                            .weight(1f),
                        expanded = state.ageSelecting,
                        label = stringResource(R.string.age),
                        selectedItem = state.age,
                        items = ages,
                        onMenu = profileModel::onAge,
                        onItem = profileModel::onAgeChange,
                        onDismiss = profileModel::onAgeDismiss
                    )
                    DropDownMenu(
                        modifier = Modifier
                            .padding(bottom = 16.dp, start = 8.dp)
                            .weight(1f),
                        expanded = state.sexSelecting,
                        label = stringResource(R.string.sex),
                        selectedItem = state.sex,
                        items = sexes,
                        onMenu = profileModel::onSex,
                        onItem = profileModel::onSexChange,
                        onDismiss = profileModel::onSexDismiss
                    )
                }
                Row(
                    modifier = Modifier.padding(
                        start = 24.dp, end = 24.dp, bottom = if (state.dataChanged) 72.dp else 16.dp
                    )
                ) {
                    DropDownMenu(
                        modifier = Modifier
                            .padding(bottom = 16.dp, end = 8.dp)
                            .weight(1f),
                        expanded = state.countrySelecting,
                        label = stringResource(R.string.country),
                        selectedItem = state.countryCode,
                        items = countries,
                        onMenu = profileModel::onCountry,
                        onItem = profileModel::onCountryCodeChange,
                        onDismiss = profileModel::onCountryDismiss
                    )
                    OutlinedButton(
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 16.dp, top = 8.dp)
                            .weight(1f),
                        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.375f)),
                        onClick = onStatistics
                    ) {
                        Text(
                            modifier = Modifier.padding(
                                start = 12.dp, end = 12.dp, top = 10.85.dp, bottom = 10.85.dp
                            ),
                            text = stringResource(R.string.statistics),
                            fontSize = 16.sp
                        )
                    }
                }
            }
            if (state.saving) {
                ProfileProgressBar()
            }
        }
    }
    profileModel.event.consumeAsEffect {
        when (it) {
            ProfileEvent.LOGGED_OUT -> onLogOut()
            ProfileEvent.CRITICAL_ERROR -> {
                Toast.makeText(context, criticalErrorMsg, Toast.LENGTH_SHORT).show()
            }
            ProfileEvent.ERROR -> {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun ProfileProgressBar() {
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
private fun EmailField(
    modifier: Modifier = Modifier,
    text: String,
) {
    OutlinedTextField(
        modifier = modifier.sizeIn(minHeight = 64.dp),
        value = text,
        label = { Text(stringResource(R.string.email)) },
        singleLine = true,
        enabled = false,
        onValueChange = {}
    )
}

@Composable
private fun ProfileField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    onValueChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier.sizeIn(minHeight = 64.dp),
        value = text,
        label = { Text(label) },
        singleLine = true,
        onValueChange = onValueChanged
    )
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "ProfileScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun ProfileScreenPreview() {
    SurveyServiceTheme {
        ProfileScreen(
            object : ProfileModel {

                override val state: StateFlow<ProfileState>
                    get() = TODO("Not yet implemented")

                override val event = MutableEvent<ProfileEvent>()

                override fun onLogOut() {}
                override fun onSaveChanges() {}
                override fun onClearChanges() {}
                override fun onAge() {}
                override fun onAgeChange(age: String) {}
                override fun onAgeDismiss() {}
                override fun onSex() {}
                override fun onSexChange(sex: String) {}
                override fun onSexDismiss() {}
                override fun onCountry() {}
                override fun onCountryCodeChange(countryCode: String) {}
                override fun onCountryDismiss() {}
                override fun onNameChange(name: String) {}
            }, {}, {}, {}
        )
    }
}
