package ru.babushkinanatoly.surveyservice.data

data class UserAuthData(
    val email: String,
    val password: String,
)

enum class LogInResult {
    OK, INVALID_CREDENTIALS, CONNECTION_ERROR
}
