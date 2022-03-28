package ru.babushkinanatoly.feature_new_survey.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_new_survey.NewSurveyModel

@NewSurveyScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ],
    modules = [NewSurveyModule::class]
)
internal interface NewSurveyComponent {

    fun provideModel(): NewSurveyModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            repoProvider: RepoProvider,
        ): NewSurveyComponent
    }
}
