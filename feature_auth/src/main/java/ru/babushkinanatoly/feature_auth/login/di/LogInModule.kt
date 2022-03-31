package ru.babushkinanatoly.feature_auth.login.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_auth.login.LogInModel
import ru.babushkinanatoly.feature_auth.login.LogInModelImpl

@Module
internal class LogInModule {

    @LogInScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): LogInModel = LogInModelImpl(scope, stringRes, repo)
}
