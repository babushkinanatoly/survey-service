package ru.babushkinanatoly.base_feature.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

typealias Event<T> = ReceiveChannel<T>
typealias MutableEvent<T> = Channel<T>

@Suppress("FunctionName")
fun <T> MutableEvent() = Channel<T>(10)

fun <T> MutableEvent<T>.dispatch(data: T) {
    trySend(data)
}

@SuppressLint("ComposableNaming")
@Composable
inline fun <T> Event<T>.consumeAsEffect(crossinline consumer: (T) -> Unit) {
    LaunchedEffect(Unit) {
        for (item in this@consumeAsEffect) {
            consumer(item)
        }
    }
}
