package ru.babushkinanatoly.core_api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface Repo {
    val currentUser: Flow<User?>

    suspend fun logIn(userAuthData: UserAuthData): LogInResult

    fun getSurveys(scope: CoroutineScope): PagedFeed

    suspend fun getSurvey(surveyId: String): SurveyResult

    suspend fun updateSurveyVote(surveyId: String, value: Boolean?): SurveyResult

    fun getUserSurveys(): Flow<List<UserSurvey>>
    fun getUserSurvey(id: String): Flow<UserSurvey>

    suspend fun updateUserSurveyTitle(id: String, title: String): SurveyResult
    suspend fun updateUserSurveyDesc(id: String, desc: String): SurveyResult
}
