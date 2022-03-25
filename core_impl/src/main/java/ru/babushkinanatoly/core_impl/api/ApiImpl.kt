package ru.babushkinanatoly.core_impl.api

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.babushkinanatoly.core_api.*
import java.net.SocketTimeoutException
import kotlin.random.Random

class ApiImpl(context: Context) : Api {

    private val fakeUserData = "Email" to "Password"
    private val surveys = createFakeSurveys()
    private val isServerError get() = Random.nextBoolean()

    override suspend fun getSurveys(count: Int, startAfter: Long?): SurveysResponse {
        delay(2000)
        return if (isServerError) {
            throw RemoteException(SocketTimeoutException())
        } else if (startAfter != null) {
            val surveyList = surveys.value.filter { it.key.id > startAfter }
                .toList()
                .toMutableList()
            val surveyListSize = surveyList.size
            SurveysResponse(
                surveyList.subList(0, if (count > surveyListSize) surveyListSize else count).toMap()
            )
        } else {
            SurveysResponse(
                if (count > surveys.value.size) {
                    surveys.value
                } else {
                    surveys.value
                        .toList()
                        .toMutableList()
                        .subList(0, count)
                        .toMap()
                }
            )
        }
    }

    override suspend fun getSurvey(surveyId: Long): SurveyResponse {
        delay(1000)
        return if (isServerError) {
            throw RemoteException(SocketTimeoutException())
        } else {
            SurveyResponse(
                surveys.value.filter { it.key.id == surveyId }.toList().first()
            )
        }
    }

    override suspend fun updateSurveyVote(
        surveyId: Long, voteId: Long?, voteValue: Boolean,
    ): UpdateSurveyVoteResponse {
        delay(500)
        var remoteVote: RemoteVote? = null
        var secondValue = false
        surveys.update { surveys ->
            surveys.map { survey ->
                val surveyPair = survey.toPair()
                if (survey.key.id == surveyId) {
                    if (voteId != null) {
                        val vote = surveyPair.second.first { it.id == voteId }
                        val votes = surveyPair.second.toMutableList()
                        if (voteValue) {
                            if (vote.value) {
                                // remove vote
                                votes.remove(vote)
                                secondValue = true
                                surveyPair.copy(second = votes)
                            } else {
                                // change value
                                secondValue = false
                                surveyPair.copy(
                                    second = surveyPair.second.map {
                                        if (it.id == voteId) it.copy(
                                            value = voteValue
                                        ) else it
                                    }
                                )
                            }
                        } else {
                            if (vote.value) {
                                // change value
                                secondValue = false
                                surveyPair.copy(
                                    second = surveyPair.second.map {
                                        if (it.id == voteId) it.copy(
                                            value = voteValue
                                        ) else it
                                    }
                                )
                            } else {
                                // remove vote
                                votes.remove(vote)
                                secondValue = true
                                surveyPair.copy(second = votes)
                            }
                        }
                    } else {
                        remoteVote = RemoteVote(
                            surveyPair.second.last().id + 1, voteValue
                        )
                        surveyPair.copy(
                            second = surveyPair.second + remoteVote!!
                        )
                    }
                } else surveyPair
            }.toMap()
        }
        return when {
            remoteVote != null -> UpdateSurveyVoteResponse.VoteCreated(remoteVote!!)
            secondValue -> UpdateSurveyVoteResponse.VoteRemoved
            else -> UpdateSurveyVoteResponse.VoteValueChanged
        }
    }

    override suspend fun logIn(userAuthData: UserAuthData): LogInResponse {
        delay(2000)
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

    private fun createFakeSurveys(): MutableStateFlow<Map<RemoteSurvey, List<RemoteVote>>> {
        var surveyId = 1L
        var voteId = 1L
        return MutableStateFlow(
            buildMap {
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
        )
    }
}

// TODO: Wrap server calls
//private inline fun <T> wrapErrors(call: () -> T): T = try {
//    call()
//} catch (e: UnknownHostException) {
//    throw RemoteException(e, "Unknown host (no connection)")
//} catch (e: HttpException) {
//    if (e.code() == 404) {
//        throw ResourceNotFoundException(e, "Not found (404)")
//    } else {
//        throw RemoteException(e, "Non 2XX response: ${e.code()}")
//    }
//}
