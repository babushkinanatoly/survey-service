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

sealed class SurveysResult {
    data class Success(val surveys: List<Survey>) : SurveysResult()
    object Error : SurveysResult()
}

sealed class SurveyResult {
    data class Success(val survey: Survey) : SurveyResult()
    object Error : SurveyResult()
}

data class Survey(
    val id: Long,
    val title: String,
    val desc: String,
    val votes: List<Vote>,
    val userVote: Vote?,
)

data class UserSurvey(
    val id: Long,
    val title: String,
    val desc: String,
    val votes: List<Vote>,
)

data class Vote(
    val id: Long,
    val value: Boolean,
)

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

data class SurveysResponse(val surveys: Map<RemoteSurvey, List<RemoteVote>>)
data class SurveyResponse(val survey: Pair<RemoteSurvey, List<RemoteVote>>)

// TODO: Temporary solution just to fake the result in current implementation, remove later
sealed class UpdateSurveyVoteResponse {
    data class VoteCreated(val remoteVote: RemoteVote) : UpdateSurveyVoteResponse()
    object VoteRemoved : UpdateSurveyVoteResponse()
    object VoteValueChanged : UpdateSurveyVoteResponse()
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
