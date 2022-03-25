package ru.babushkinanatoly.core_api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface Repo {
    val currentUser: Flow<User?>

    fun getSurveys(scope: CoroutineScope): PagedFeed
    suspend fun getSurvey(surveyId: Long): SurveyResult

    // TODO: remove voteId later
    suspend fun updateSurveyVote(surveyId: Long, voteId: Long?, value: Boolean)

    fun getUserSurveys(): Flow<List<UserSurvey>>
    fun getUserSurvey(id: Long): Flow<UserSurvey>

    fun updateUserSurveyTitle(id: Long, title: String)
    fun updateUserSurveyDesc(id: Long, desc: String)

    suspend fun onLogIn(userAuthData: UserAuthData): LogInResult
}
