package ru.babushkinanatoly.core_impl

import kotlinx.coroutines.delay
import ru.babushkinanatoly.core_api.LogInResult
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.UserAuthData
import ru.babushkinanatoly.core_impl.api.Api
import ru.babushkinanatoly.core_impl.db.Db
import java.util.*

class RepoImpl(
    db: Db,
    api: Api,
) : Repo {

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
