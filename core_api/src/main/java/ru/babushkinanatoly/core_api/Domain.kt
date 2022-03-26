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

sealed class SurveysResult {
    data class Success(val surveys: List<Survey>) : SurveysResult()
    object Error : SurveysResult()
}

sealed class SurveyResult {
    data class Success(val survey: Survey) : SurveyResult()
    object Error : SurveyResult()
}

data class Survey(
    val id: String,
    val title: String,
    val desc: String,
    val upvotes: List<String>,
    val downvotes: List<String>,
)

data class UserSurvey(
    val id: String,
    val title: String,
    val desc: String,
    val upvotes: List<String>,
    val downvotes: List<String>,
)
