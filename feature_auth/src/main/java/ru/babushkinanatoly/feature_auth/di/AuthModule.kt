package ru.babushkinanatoly.feature_auth.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_auth.AuthModel
import ru.babushkinanatoly.feature_auth.AuthModelImpl

@Module
internal class AuthModule {

    @AuthScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): AuthModel = AuthModelImpl(scope, stringRes, repo)
}
