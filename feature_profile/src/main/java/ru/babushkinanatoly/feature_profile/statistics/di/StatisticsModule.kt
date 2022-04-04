package ru.babushkinanatoly.feature_profile.statistics.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_profile.statistics.StatisticsModel
import ru.babushkinanatoly.feature_profile.statistics.StatisticsModelImpl

@Module
internal class StatisticsModule {

    @StatisticsScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): StatisticsModel = StatisticsModelImpl(scope, stringRes, repo)
}
