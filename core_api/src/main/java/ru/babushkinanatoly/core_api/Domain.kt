package ru.babushkinanatoly.core_api

data class UserAuthData(
    val email: String,
    val password: String,
)

enum class LogInResult {
    OK, INVALID_CREDENTIALS, CONNECTION_ERROR
}
