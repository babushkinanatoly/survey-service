package ru.babushkinanatoly.feature_profile.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_profile.ProfileWorkflowModel
import ru.babushkinanatoly.feature_profile.ProfileWorkflowModelImpl

@Module
internal class ProfileWorkflowModule {

    @ProfileWorkflowScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): ProfileWorkflowModel = ProfileWorkflowModelImpl(scope, stringRes, repo)
}
