package ru.babushkinanatoly.feature_survey_feed.surveydetails.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_survey_feed.surveydetails.SurveyDetailsModel
import ru.babushkinanatoly.feature_survey_feed.surveydetails.SurveyDetailsModelImpl

@Module
internal class SurveyDetailsModule {

    @SurveyDetailsScope
    @Provides
    fun provideModel(
        surveyId: Long,
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): SurveyDetailsModel = SurveyDetailsModelImpl(surveyId, scope, stringRes, repo)
}
