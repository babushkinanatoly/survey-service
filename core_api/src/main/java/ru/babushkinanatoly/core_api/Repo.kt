package ru.babushkinanatoly.core_api

import kotlinx.coroutines.flow.Flow

interface Repo {
    val currentUser: Flow<User?>

    // TODO: Pagination...
    suspend fun getSurveys(): SurveysResult

    fun getUserSurveys(): Flow<List<UserSurvey>>
    fun getUserSurvey(id: Long): Flow<UserSurvey>

    fun updateUserSurveyTitle(id: Long, title: String)
    fun updateUserSurveyDesc(id: Long, desc: String)

    suspend fun onLogIn(userAuthData: UserAuthData): LogInResult
}
