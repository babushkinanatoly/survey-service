package ru.babushkinanatoly.base_feature.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import ru.babushkinanatoly.core_api.Event

fun NavBackStackEntry.requireLong(key: String) =
    requireNotNull(arguments).getString(key)?.toLong() ?: error("No value found for this key: $key")

@SuppressLint("ComposableNaming")
@Composable
inline fun <T> Event<T>.consumeAsEffect(crossinline consumer: (T) -> Unit) {
    LaunchedEffect(Unit) {
        for (item in this@consumeAsEffect) {
            consumer(item)
        }
    }
}
