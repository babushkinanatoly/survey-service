package ru.babushkinanatoly.core_impl.api

import android.content.Context
import kotlinx.coroutines.delay
import ru.babushkinanatoly.core_api.*
import java.net.SocketTimeoutException
import java.util.*

class ApiImpl(context: Context) : Api {

    private val fakeUserData = "Email" to "Password"

    override suspend fun logIn(userAuthData: UserAuthData): LogInResponse {
        delay(2000)
        val isServerError = Random().nextBoolean()
        val isValidCredentials =
            userAuthData.email == fakeUserData.first && userAuthData.password == fakeUserData.second
        return when {
            isServerError -> throw RemoteException(SocketTimeoutException())
            !isValidCredentials -> LogInResponse.InvalidCredentials
            else -> LogInResponse.Success(
                RemoteUser(0, "Email", "User"),
                mapOf(
                    RemoteSurvey(0, "User survey 1", "User survey 1 desc") to
                            listOf(
                                RemoteVote(0, true),
                                RemoteVote(1, true),
                                RemoteVote(2, false),
                                RemoteVote(3, false),
                                RemoteVote(4, false),
                            ),
                    RemoteSurvey(1, "User survey 2", "User survey 2 desc") to
                            listOf(
                                RemoteVote(5, true),
                                RemoteVote(6, true),
                                RemoteVote(7, true),
                                RemoteVote(8, false),
                                RemoteVote(9, false),
                            ),
                    RemoteSurvey(2, "User survey 3", "User survey 3 desc") to
                            listOf(
                                RemoteVote(10, true),
                                RemoteVote(11, true),
                                RemoteVote(12, true),
                                RemoteVote(13, true),
                                RemoteVote(14, false),
                            ),
                ),
                mapOf(
                    RemoteVote(15, true)
                            to RemoteSurvey(3, "User survey 4", "User survey 4 desc"),
                    RemoteVote(16, false)
                            to RemoteSurvey(4, "User survey 5", "User survey 5 desc"),
                )
            )
        }
    }
}
