package ru.babushkinanatoly.core_api

import androidx.annotation.StringRes

interface Strings {
    operator fun get(@StringRes resId: Int): String
    fun format(@StringRes resId: Int, vararg args: Any): String
}
