package ru.babushkinanatoly.surveyservice.ui.usersurveys

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.babushkinanatoly.surveyservice.R
import ru.babushkinanatoly.surveyservice.util.Event
import ru.babushkinanatoly.surveyservice.util.consumeAsEffect

@ExperimentalMaterialApi
@Composable
fun UserSurveysScreen(
    scrollUp: Event<Unit>,
    title: String,
    names: List<String> = List(100) { "$it" },
    onItem: () -> Unit,
    onNewSurvey: () -> Unit
) {
    val surveysState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold {
        TopAppBar(
            title = { Text(title) },
            actions = {
                IconButton(onClick = onNewSurvey) {
                    Icon(imageVector = Icons.Filled.Add, stringResource(R.string.new_survey))
                }
            }
        )
        LazyColumn(
            state = surveysState,
            modifier = Modifier.padding(top = 56.dp)
        ) {
            items(items = names) { name ->
                UserSurveyItem(name = name) { onItem() }
            }
        }
    }
    scrollUp.consumeAsEffect {
        coroutineScope.launch {
            surveysState.animateScrollToItem(index = 0)
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun UserSurveyItem(
    name: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = "User Survey, ")
                Text(
                    text = name,
                    style = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}