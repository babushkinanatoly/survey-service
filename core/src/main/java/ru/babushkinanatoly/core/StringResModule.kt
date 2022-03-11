package ru.babushkinanatoly.core

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.core_impl.StringResImpl
import javax.inject.Singleton

@Module
class StringResModule {

    @Singleton
    @Provides
    fun provideStringRes(context: Context): StringRes {
        return StringResImpl(context)
    }
}
