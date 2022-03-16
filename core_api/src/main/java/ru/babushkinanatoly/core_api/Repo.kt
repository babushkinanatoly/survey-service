package ru.babushkinanatoly.core_api

import kotlinx.coroutines.flow.Flow

interface Repo {
    val currentUser: Flow<User?>
    fun getUserSurveys(): Flow<List<UserSurvey>>
    fun getUserSurvey(id: Long): Flow<UserSurvey>

    suspend fun onLogIn(userAuthData: UserAuthData): LogInResult
}
