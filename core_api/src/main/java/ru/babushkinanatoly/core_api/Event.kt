package ru.babushkinanatoly.core_api

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

typealias Event<T> = ReceiveChannel<T>
typealias MutableEvent<T> = Channel<T>

@Suppress("FunctionName")
fun <T> MutableEvent() = Channel<T>(10)

fun <T> MutableEvent<T>.dispatch(data: T) {
    trySend(data)
}
