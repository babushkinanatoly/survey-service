package ru.babushkinanatoly.core_api

/**
 * Local data
 */
data class User(
    val id: Long,
    val email: String,
    val name: String,
)

data class UserAuthData(
    val email: String,
    val password: String,
)

enum class LogInResult {
    OK, INVALID_CREDENTIALS, CONNECTION_ERROR
}

/**
 * Remote data
 */
sealed class LogInResponse {
    data class Success(
        val remoteUser: RemoteUser,
        val remoteUserSurveys: Map<RemoteSurvey, List<RemoteVote>>,
        val remoteUserVotes: Map<RemoteVote, RemoteSurvey>,
    ) : LogInResponse()

    object InvalidCredentials : LogInResponse()
}

data class RemoteUser(
    val id: Long,
    val email: String,
    val name: String,
)

data class RemoteSurvey(
    val id: Long,
    val title: String,
    val desc: String,
)

data class RemoteVote(
    val id: Long,
    val value: Boolean,
)
