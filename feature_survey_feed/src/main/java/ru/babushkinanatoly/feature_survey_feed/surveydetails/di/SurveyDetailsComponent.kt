package ru.babushkinanatoly.feature_survey_feed.surveydetails.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_survey_feed.surveydetails.SurveyDetailsModel

@SurveyDetailsScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ],
    modules = [SurveyDetailsModule::class]
)
internal interface SurveyDetailsComponent {

    fun provideModel(): SurveyDetailsModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance surveyId: String,
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            repoProvider: RepoProvider,
        ): SurveyDetailsComponent
    }
}
