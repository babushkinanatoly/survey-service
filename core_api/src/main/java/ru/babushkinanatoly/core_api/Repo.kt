package ru.babushkinanatoly.core_api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface Repo {
    val currentUser: Flow<User?>

    suspend fun onLogIn(userAuthData: UserAuthData): LogInResult

    fun getSurveys(scope: CoroutineScope): PagedFeed
    suspend fun getSurvey(surveyId: String): SurveyResult

    suspend fun updateSurveyVote(surveyId: String, voteId: Long?, value: Boolean)

    fun getUserSurveys(): Flow<List<UserSurvey>>

    fun getUserSurvey(id: String): Flow<UserSurvey>
    fun updateUserSurveyTitle(id: String, title: String)

    fun updateUserSurveyDesc(id: String, desc: String)
}
