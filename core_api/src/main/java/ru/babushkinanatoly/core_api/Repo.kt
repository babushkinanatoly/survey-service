package ru.babushkinanatoly.core_api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Repo {
    val currentUser: StateFlow<User?>

    suspend fun signUp(userSignUpData: UserSignUpData): SignUpResult
    suspend fun logIn(userLogInData: UserLogInData): LogInResult
    suspend fun logOut(): Boolean

    suspend fun updateUser(profileData: UserProfileData): Boolean

    suspend fun addSurvey(title: String, desc: String): Boolean

    fun getSurveys(scope: CoroutineScope): PagedFeed

    suspend fun getSurvey(id: String): SurveyResult

    suspend fun deleteSurvey(id: String): Boolean

    suspend fun updateSurveyVote(surveyId: String, value: Boolean?): SurveyResult

    fun getUserSurveys(): Flow<List<UserSurvey>>
    fun getUserSurvey(id: String): Flow<UserSurvey?>
    fun getUserVotes(): Flow<List<Boolean>>

    suspend fun updateUserSurveyTitle(id: String, title: String): SurveyResult
    suspend fun updateUserSurveyDesc(id: String, desc: String): SurveyResult
}
