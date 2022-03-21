package ru.babushkinanatoly.feature_survey_feed.surveyfeed.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_survey_feed.surveyfeed.SurveyFeedModel

@SurveyFeedScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ],
    modules = [SurveyFeedModule::class]
)
internal interface SurveyFeedComponent {

    fun provideModel(): SurveyFeedModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            repoProvider: RepoProvider,
        ): SurveyFeedComponent
    }
}
