package ru.babushkinanatoly.feature_auth.signup.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_auth.signup.SignUpModel
import ru.babushkinanatoly.feature_auth.signup.SignUpModelImpl

@Module
internal class SignUpModule {

    @SignUpScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): SignUpModel = SignUpModelImpl(scope, stringRes, repo)
}
