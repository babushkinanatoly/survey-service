package ru.babushkinanatoly.feature_profile.profile.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_profile.profile.ProfileModel
import ru.babushkinanatoly.feature_profile.profile.ProfileModelImpl

@Module
internal class ProfileModule {

    @ProfileScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): ProfileModel = ProfileModelImpl(scope, stringRes, repo)
}
