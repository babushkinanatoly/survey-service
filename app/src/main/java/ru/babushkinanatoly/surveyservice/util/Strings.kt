package ru.babushkinanatoly.surveyservice.util

import android.content.Context
import androidx.annotation.StringRes

interface Strings {
    operator fun get(@StringRes resId: Int): String
    fun format(@StringRes resId: Int, vararg args: Any): String
}

class StringsImp(private val context: Context) : Strings {
    override fun get(resId: Int) = context.getString(resId)
    override fun format(resId: Int, vararg args: Any) = context.getString(resId, *args)
}
