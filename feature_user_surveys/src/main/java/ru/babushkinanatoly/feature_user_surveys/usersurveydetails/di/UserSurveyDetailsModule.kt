package ru.babushkinanatoly.feature_user_surveys.usersurveydetails.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_user_surveys.usersurveydetails.UserSurveyDetailsModel
import ru.babushkinanatoly.feature_user_surveys.usersurveydetails.UserSurveyDetailsModelImpl

@Module
internal class UserSurveyDetailsModule {

    @UserSurveyDetailsScope
    @Provides
    fun provideModel(
        surveyId: Long,
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): UserSurveyDetailsModel = UserSurveyDetailsModelImpl(surveyId, scope, stringRes, repo)
}
