package ru.babushkinanatoly.surveyservice.data

import kotlinx.coroutines.delay
import java.util.*

interface Repo {
    suspend fun onLogIn(userAuthData: UserAuthData): LogInResult
}

class RepoImpl : Repo {

    private val fakeUserData = "Email" to "Password"

    override suspend fun onLogIn(userAuthData: UserAuthData): LogInResult {
        delay(2000)
        val isServerError = Random().nextBoolean()
        val isValidCredentials =
            userAuthData.email == fakeUserData.first && userAuthData.password == fakeUserData.second
        return when {
            isServerError -> LogInResult.CONNECTION_ERROR
            !isValidCredentials -> LogInResult.INVALID_CREDENTIALS
            else -> LogInResult.OK
        }
    }
}
