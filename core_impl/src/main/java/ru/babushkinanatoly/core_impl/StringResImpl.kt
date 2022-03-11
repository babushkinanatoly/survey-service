package ru.babushkinanatoly.core_impl

import android.content.Context
import ru.babushkinanatoly.core_api.StringRes

class StringResImpl(private val context: Context) : StringRes {
    override fun get(resId: Int) = context.getString(resId)
    override fun format(resId: Int, vararg args: Any) = context.getString(resId, *args)
}
