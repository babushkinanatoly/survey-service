package ru.babushkinanatoly.feature_profile.statistics.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_profile.statistics.StatisticsModel

@StatisticsScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ],
    modules = [StatisticsModule::class]
)
internal interface StatisticsComponent {

    fun provideModel(): StatisticsModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            repoProvider: RepoProvider,
        ): StatisticsComponent
    }
}
