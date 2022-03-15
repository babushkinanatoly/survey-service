package ru.babushkinanatoly.feature_user_surveys.usersurveys.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_user_surveys.usersurveys.UserSurveysModel

@UserSurveysScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ],
    modules = [UserSurveysModule::class]
)
interface UserSurveysComponent {

    fun provideModel(): UserSurveysModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            repoProvider: RepoProvider,
        ): UserSurveysComponent
    }
}
