package ru.babushkinanatoly.feature_user_surveys.usersurveydetails.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_user_surveys.usersurveydetails.UserSurveyDetailsModel

@UserSurveyDetailsScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ],
    modules = [UserSurveyDetailsModule::class]
)
internal interface UserSurveyDetailsComponent {

    fun provideModel(): UserSurveyDetailsModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance surveyId: Long,
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            repoProvider: RepoProvider,
        ): UserSurveyDetailsComponent
    }
}
