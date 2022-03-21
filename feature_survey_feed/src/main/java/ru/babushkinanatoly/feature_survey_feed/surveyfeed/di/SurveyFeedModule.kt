package ru.babushkinanatoly.feature_survey_feed.surveyfeed.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_survey_feed.surveyfeed.SurveyFeedModel
import ru.babushkinanatoly.feature_survey_feed.surveyfeed.SurveyFeedModelImpl

@Module
internal class SurveyFeedModule {

    @SurveyFeedScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): SurveyFeedModel = SurveyFeedModelImpl(scope, stringRes, repo)
}
