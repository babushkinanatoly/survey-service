package ru.babushkinanatoly.feature_settings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.Settings
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.base_feature.util.goBack
import ru.babushkinanatoly.base_feature.util.isDarkTheme
import ru.babushkinanatoly.core_api.Settings.AppTheme
import ru.babushkinanatoly.core_api.Settings.AppTheme.*
import ru.babushkinanatoly.feature_settings.di.SettingsViewModel

@Composable
fun SettingsScreen() {
    val model = viewModel<SettingsViewModel>().settingsComponent.provideModel()
    val state by model.state.collectAsState()
    val context = LocalContext.current
    Surface {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Settings.resId)) },
                    backgroundColor = MaterialTheme.colors.background,
                    navigationIcon = {
                        IconButton(onClick = { context.goBack() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                GroupTitle(
                    text = stringResource(R.string.appearance)
                )
                SelectableSetting(
                    title = stringResource(R.string.theme),
                    desc = state.appThemeSettingsDesc,
                    onClick = model::onAppTheme
                )
            }
            if (state.themeSelecting) {
                ThemeDialog(
                    title = stringResource(R.string.theme),
                    appTheme = state.appTheme,
                    onFollowSystem = { model.onAppThemeChange(FOLLOW_SYSTEM) },
                    onLight = { model.onAppThemeChange(LIGHT) },
                    onDark = { model.onAppThemeChange(DARK) },
                    onDismiss = model::onAppThemeDismiss
                )
            }
        }
    }
}

@Composable
private fun GroupTitle(
    text: String,
) {
    Text(
        modifier = Modifier.padding(start = 72.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = MaterialTheme.colors.primary
    )
}

@Composable
private fun SelectableSetting(
    title: String,
    desc: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            modifier = Modifier.padding(start = 72.dp, end = 16.dp, top = 16.dp),
            text = title,
            fontSize = 16.sp,
        )
        Text(
            modifier = Modifier.padding(start = 72.dp, end = 16.dp, bottom = 16.dp),
            text = desc,
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ThemeDialog(
    title: String,
    appTheme: AppTheme,
    onFollowSystem: () -> Unit,
    onDark: () -> Unit,
    onLight: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(
                    if (appTheme.isDarkTheme()) {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colors.surface
                    },
                    RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text(
                modifier = Modifier.padding(
                    top = 18.dp, bottom = 12.dp, start = 24.dp, end = 24.dp
                ),
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
            Column {
                ThemeVariant(
                    text = stringResource(R.string.follow_system),
                    selected = appTheme == FOLLOW_SYSTEM,
                    onClick = onFollowSystem
                )
                ThemeVariant(
                    text = stringResource(R.string.light),
                    selected = appTheme == LIGHT,
                    onClick = onLight
                )
                ThemeVariant(
                    text = stringResource(R.string.dark),
                    selected = appTheme == DARK,
                    onClick = onDark
                )
            }
        }
    }
}

@Composable
private fun ThemeVariant(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
            selected = selected,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary),
            onClick = null
        )
        Text(
            text = text,
            fontSize = 16.sp
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "SettingsScreenPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun SettingsScreenPreview() {
    SurveyServiceTheme {
        SettingsScreen()
    }
}
