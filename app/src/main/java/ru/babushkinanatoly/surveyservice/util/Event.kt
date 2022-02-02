package ru.babushkinanatoly.surveyservice.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

typealias Event<T> = ReceiveChannel<T>
typealias MutableEvent<T> = Channel<T>

fun <T> MutableEvent<T>.dispatch(data: T) {
    trySend(data)
}

@Suppress("FunctionName")
fun <T> MutableEvent() = Channel<T>(10)

@SuppressLint("ComposableNaming")
@Composable
fun <T> Event<T>.consumeAsEffect(consumer: (T) -> Unit) {
    LaunchedEffect(Unit) {
        for (item in this@consumeAsEffect) {
            consumer(item)
        }
    }
}
