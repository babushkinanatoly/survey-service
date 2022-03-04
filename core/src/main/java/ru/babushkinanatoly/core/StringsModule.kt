package ru.babushkinanatoly.core

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.babushkinanatoly.core_api.Strings
import ru.babushkinanatoly.core_impl.StringsImpl
import javax.inject.Singleton

@Module
class StringsModule {

    @Singleton
    @Provides
    fun provideStrings(context: Context): Strings {
        return StringsImpl(context)
    }
}
