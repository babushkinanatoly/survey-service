package ru.babushkinanatoly.core_impl.api

import android.content.Context
import kotlinx.coroutines.delay
import ru.babushkinanatoly.core_api.*
import java.net.SocketTimeoutException
import kotlin.random.Random

class ApiImpl(context: Context) : Api {

    private val fakeUserData = "Email" to "Password"

    private val surveys = createFakeSurveys()

    private val isServerError get() = Random.nextBoolean()

    override suspend fun getSurveys(): SurveysResponse {
        delay(2000)
        return if (isServerError) {
            throw RemoteException(SocketTimeoutException())
        } else {
            SurveysResponse(surveys)
        }
    }

    override suspend fun getSurvey(surveyId: Long): SurveyResponse {
        delay(1000)
        return if (isServerError) {
            throw RemoteException(SocketTimeoutException())
        } else {
            SurveyResponse(
                surveys
                    .filter { it.key.id == surveyId }
                    .toList()
                    .first()
            )
        }
    }

    override suspend fun logIn(userAuthData: UserAuthData): LogInResponse {
        delay(2000)
        val isServerError = Random.nextBoolean()
        val isValidCredentials =
            userAuthData.email == fakeUserData.first && userAuthData.password == fakeUserData.second
        return when {
            isServerError -> throw RemoteException(SocketTimeoutException())
            !isValidCredentials -> LogInResponse.InvalidCredentials
            else -> {
                var surveyId = 1L
                var voteId = 1L
                LogInResponse.Success(
                    RemoteUser(0, "Email", "User"),
                    buildMap {
                        (0..9L).toList().map {
                            put(
                                RemoteSurvey(
                                    surveyId,
                                    "User survey $surveyId",
                                    "User survey $surveyId desc"
                                ),
                                ((0..19L).toList().map { RemoteVote(voteId, Random.nextBoolean()) })
                            )
                            surveyId += 1
                            voteId += 1
                        }
                    },
                    buildMap {
                        (0..9L).toList().map {
                            put(
                                RemoteVote(voteId, Random.nextBoolean()),
                                RemoteSurvey(
                                    surveyId,
                                    "User survey $surveyId",
                                    "User survey $surveyId desc"
                                ),
                            )
                        }
                        surveyId += 1
                        voteId += 1
                    }
                )
            }
        }
    }

    private fun createFakeSurveys(): Map<RemoteSurvey, List<RemoteVote>> {
        var surveyId = 1L
        var voteId = 1L
        return buildMap {
            (0..9L).toList().map {
                put(
                    RemoteSurvey(
                        surveyId,
                        "Survey $surveyId",
                        "Survey $surveyId desc"
                    ),
                    ((0..19L).toList().map { RemoteVote(voteId, Random.nextBoolean()) })
                )
                surveyId += 1
                voteId += 1
            }
        }
    }
}
