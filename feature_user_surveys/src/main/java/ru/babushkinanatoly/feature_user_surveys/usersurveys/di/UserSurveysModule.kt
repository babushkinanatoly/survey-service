package ru.babushkinanatoly.feature_user_surveys.usersurveys.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_user_surveys.usersurveys.UserSurveysModel
import ru.babushkinanatoly.feature_user_surveys.usersurveys.UserSurveysModelImpl

@Module
class UserSurveysModule {

    @UserSurveysScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): UserSurveysModel = UserSurveysModelImpl(scope, stringRes, repo)
}
