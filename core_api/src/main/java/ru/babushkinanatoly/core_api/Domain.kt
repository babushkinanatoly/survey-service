package ru.babushkinanatoly.core_api

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

sealed class SurveyResult {
    data class Success(val survey: Survey) : SurveyResult()
    data class Error(val msg: String) : SurveyResult()
}

data class Survey(
    val id: String,
    val title: String,
    val desc: String,
    val upvotes: List<String>,
    val downvotes: List<String>,
    val userVote: Boolean?,
)

data class UserSurvey(
    val id: String,
    val title: String,
    val desc: String,
    val upvotes: List<String>,
    val downvotes: List<String>,
)
