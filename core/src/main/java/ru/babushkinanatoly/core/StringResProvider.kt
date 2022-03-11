package ru.babushkinanatoly.core

import ru.babushkinanatoly.core_api.StringRes

interface StringResProvider {
    fun provideStringRes(): StringRes
}
