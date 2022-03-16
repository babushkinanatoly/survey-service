package ru.babushkinanatoly.base_feature.util

import androidx.navigation.NavBackStackEntry

fun NavBackStackEntry.requireLong(key: String) =
    requireNotNull(arguments).getString(key)?.toLong() ?: error("No value found for this key: $key")
