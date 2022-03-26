package ru.babushkinanatoly.core_impl.api

import ru.babushkinanatoly.core_api.UserAuthData

interface Api {
    suspend fun logIn(authData: UserAuthData): LogInResponse

    suspend fun getSurveys(count: Int, startAfter: Long? = null): SurveysResponse
    suspend fun getSurvey(surveyId: Long): SurveyResponse

    suspend fun updateSurveyVote(surveyId: Long, voteId: Long?, voteValue: Boolean)
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
