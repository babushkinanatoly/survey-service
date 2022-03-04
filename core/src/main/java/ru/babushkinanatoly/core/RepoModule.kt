package ru.babushkinanatoly.core

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_impl.RepoImpl
import ru.babushkinanatoly.core_impl.api.ApiImpl
import ru.babushkinanatoly.core_impl.db.DbImpl
import javax.inject.Singleton

@Module
class RepoModule {

    @Singleton
    @Provides
    fun provideRepo(context: Context): Repo {
        return RepoImpl(DbImpl(context), ApiImpl(context))
    }
}
