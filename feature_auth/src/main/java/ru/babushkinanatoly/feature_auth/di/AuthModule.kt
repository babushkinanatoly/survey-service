package ru.babushkinanatoly.feature_auth.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.Strings
import ru.babushkinanatoly.feature_auth.AuthModel
import ru.babushkinanatoly.feature_auth.AuthModelImpl

@Module
class AuthModule {

    @AuthScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        strings: Strings,
        repo: Repo,
    ): AuthModel = AuthModelImpl(scope, strings, repo)
}
