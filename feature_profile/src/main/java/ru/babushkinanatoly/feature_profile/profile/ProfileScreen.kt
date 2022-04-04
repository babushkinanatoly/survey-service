package ru.babushkinanatoly.feature_profile.profile

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.NavWorkflow.ProfileWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
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
    val context = LocalContext.current
    val errorMsg = stringResource(R.string.error_general)
    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(ProfileWorkflow.Profile.resId)) },
                    backgroundColor = MaterialTheme.colors.background,
                    actions = {
                        IconButton(onClick = onSettings) {
                            Icon(Icons.Outlined.Settings, stringResource(R.string.settings))
                        }
                        IconButton(onClick = profileModel::onLogOut) {
                            Icon(painterResource(R.drawable.ic_log_out), stringResource(R.string.log_out))
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                ClickableText(
                    text = AnnotatedString("Click to view statistics"),
                    style = TextStyle(
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 18.sp
                    ),
                    onClick = { onStatistics() }
                )
            }
        }
    }
    profileModel.event.consumeAsEffect {
        when (it) {
            ProfileEvent.LOGGED_OUT -> onLogOut()
            ProfileEvent.ERROR -> {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
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

                override val event = MutableEvent<ProfileEvent>()

                override fun onLogOut() {}
            },
            {}, {}, {}
        )
    }
}
