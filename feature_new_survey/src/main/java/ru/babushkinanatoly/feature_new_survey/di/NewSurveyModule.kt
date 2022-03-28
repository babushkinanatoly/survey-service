package ru.babushkinanatoly.feature_new_survey.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes
import ru.babushkinanatoly.feature_new_survey.NewSurveyModel
import ru.babushkinanatoly.feature_new_survey.NewSurveyModelImpl

@Module
internal class NewSurveyModule {

    @NewSurveyScope
    @Provides
    fun provideModel(
        scope: CoroutineScope,
        stringRes: StringRes,
        repo: Repo,
    ): NewSurveyModel = NewSurveyModelImpl(scope, stringRes, repo)
}
