package ru.babushkinanatoly.core_impl.api

import ru.babushkinanatoly.core_api.UserLogInData
import ru.babushkinanatoly.core_api.UserProfileData
import ru.babushkinanatoly.core_api.UserSignUpData

interface Api {
    suspend fun signUp(userSignUpData: UserSignUpData): SignUpResponse
    suspend fun logIn(logInData: UserLogInData): LogInResponse
    suspend fun logOut()

    suspend fun createSurvey(title: String, desc: String): RemoteSurvey

    suspend fun getSurveys(count: Int, startAfter: String? = null): List<RemoteSurvey>
    suspend fun getSurvey(surveyId: String): RemoteSurvey

    suspend fun deleteSurvey(surveyId: String)

    suspend fun updateUser(profileData: UserProfileData): RemoteUser

    suspend fun updateSurveyVote(surveyId: String, voteValue: Boolean?): RemoteSurvey
    suspend fun updateSurveyTitle(surveyId: String, title: String): RemoteSurvey
    suspend fun updateSurveyDesc(surveyId: String, desc: String): RemoteSurvey
}

open class RemoteException(cause: Throwable?, message: String? = null) : RuntimeException(message, cause)

class ResourceNotFoundException(cause: Throwable?, message: String? = null) : RemoteException(cause, message)

sealed class LogInResponse {
    data class Success(
        val user: RemoteUser,
        val userVotes: RemoteUserVotes,
        val userSurveys: List<RemoteSurvey>,
    ) : LogInResponse()

    object InvalidCredentials : LogInResponse()
}

sealed class SignUpResponse {
    data class Success(val user: RemoteUser) : SignUpResponse()
    object UserAlreadyExists : SignUpResponse()
}

data class RemoteUser(
    val email: String,
    val name: String,
    val age: Int,
    val sex: String,
    val countryCode: String,
)

data class RemoteUserVotes(
    val upvotedSurveyIds: List<String>,
    val downvotedSurveyIds: List<String>,
)

data class RemoteSurvey(
    val id: String,
    val ownerId: String,
    val title: String,
    val desc: String,
    val upvotedUserIds: List<String>,
    val downvotedUserIds: List<String>,
)
