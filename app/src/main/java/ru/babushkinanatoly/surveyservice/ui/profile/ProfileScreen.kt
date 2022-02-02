package ru.babushkinanatoly.surveyservice.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.babushkinanatoly.surveyservice.R

@Composable
fun ProfileScreen(
    title: String,
    onSettings: () -> Unit,
    onStatistics: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(imageVector = Icons.Outlined.Settings, stringResource(R.string.settings))
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
                style = TextStyle(fontSize = 18.sp),
                onClick = { onStatistics() }
            )
        }
    }
}
