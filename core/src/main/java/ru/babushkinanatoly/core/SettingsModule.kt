package ru.babushkinanatoly.core

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.babushkinanatoly.core_api.Settings
import ru.babushkinanatoly.core_impl.SettingsImpl
import javax.inject.Singleton

@Module
class SettingsModule {

    @Singleton
    @Provides
    fun provideSettings(context: Context): Settings {
        return SettingsImpl(context)
    }
}
