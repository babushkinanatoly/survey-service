package ru.babushkinanatoly.base_feature.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import ru.babushkinanatoly.core_api.Event
import ru.babushkinanatoly.core_api.Settings.AppTheme

fun NavBackStackEntry.requireString(key: String) =
    requireNotNull(arguments).getString(key) ?: error("No value found for this key: $key")

@SuppressLint("ComposableNaming")
@Composable
inline fun <T> Event<T>.consumeAsEffect(crossinline consumer: (T) -> Unit) {
    LaunchedEffect(Unit) {
        for (item in this@consumeAsEffect) {
            consumer(item)
        }
    }
}

fun Context.goBack() = findActivity()?.onBackPressed()

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun AppTheme.isDarkTheme() = if (this == AppTheme.FOLLOW_SYSTEM) {
    isSystemInDarkTheme()
} else this == AppTheme.DARK

@Composable
fun DropDownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    selectedItem: String,
    label: String,
    items: List<String>,
    onMenu: () -> Unit,
    onItem: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { onMenu() }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedItem,
            singleLine = true,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
        )
        ExposedDropdownMenu(
            modifier = Modifier.requiredHeightIn(max = 160.dp),
            expanded = expanded,
            onDismissRequest = onDismiss
        ) {
            items.forEach {
                DropdownMenuItem(onClick = { onItem(it) }) {
                    Text(it)
                }
            }
        }
    }
}
