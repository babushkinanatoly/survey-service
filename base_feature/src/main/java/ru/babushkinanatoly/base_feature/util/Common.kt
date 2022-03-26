package ru.babushkinanatoly.base_feature.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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

fun Context.goBack() = findActivity()?.onBackPressed()

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
