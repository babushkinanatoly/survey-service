package ru.babushkinanatoly.core_impl.api

import android.content.Context
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.babushkinanatoly.core_api.UserAuthData
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiImpl(private val context: Context) : Api {

    companion object {
        private const val BASE_URL = "https://survey4all.herokuapp.com/"
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor {
            var request = it.request()
            if (request.header(SurveyService.AUTH_HEADER) != null) {
                request = request.newBuilder()
                    .removeHeader(SurveyService.AUTH_HEADER)
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }
            it.proceed(request)
        }
        .build()

    private val surveyService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(SurveyService::class.java)

    private var token: String?
        get() {
            return context.getSharedPreferences("user", Context.MODE_PRIVATE)
                .getString("token", null)
        }
        set(value) {
            context.getSharedPreferences("user", Context.MODE_PRIVATE).edit()
                .putString("token", value).apply()
        }

    override suspend fun logIn(authData: UserAuthData): LogInResponse = wrapErrors {
        try {
            surveyService.getUser(GetUserRequestData(authData.email, authData.password)).apply {
                this@ApiImpl.token = token
            }.toLogInResponse()
        } catch (ex: HttpException) {
            if (ex.code() == 500) LogInResponse.InvalidCredentials else throw RemoteException(ex)
        }
    }

    override suspend fun getSurveys(count: Int, startAfter: String?): List<RemoteSurvey> = wrapErrors {
        surveyService.getSurveys(count, startAfter).map { it.toRemoteSurvey() }
    }

    override suspend fun getSurvey(surveyId: Long): SurveyResponse {
        delay(1000)
//        return if (isServerError) {
        throw RemoteException(SocketTimeoutException())
//        } else {
//            SurveyResponse(
//                surveys.value.filter { it.key.id == surveyRemoteId }.toList().first()
//            )
//        }
    }

    override suspend fun updateSurveyVote(
        surveyId: Long, voteId: Long?, voteValue: Boolean,
    ) {
//        delay(500)
//        var remoteVote: RemoteVote? = null
//        var secondValue = false
//        surveys.update { surveys ->
//            surveys.map { survey ->
//                val surveyPair = survey.toPair()
//                if (survey.key.id == surveyRemoteId) {
//                    if (voteId != null) {
//                        val vote = surveyPair.second.first { it.id == voteId }
//                        val votes = surveyPair.second.toMutableList()
//                        if (voteValue) {
//                            if (vote.value) {
//                                // remove vote
//                                votes.remove(vote)
//                                secondValue = true
//                                surveyPair.copy(second = votes)
//                            } else {
//                                // change value
//                                secondValue = false
//                                surveyPair.copy(
//                                    second = surveyPair.second.map {
//                                        if (it.id == voteId) it.copy(
//                                            value = voteValue
//                                        ) else it
//                                    }
//                                )
//                            }
//                        } else {
//                            if (vote.value) {
//                                // change value
//                                secondValue = false
//                                surveyPair.copy(
//                                    second = surveyPair.second.map {
//                                        if (it.id == voteId) it.copy(
//                                            value = voteValue
//                                        ) else it
//                                    }
//                                )
//                            } else {
//                                // remove vote
//                                votes.remove(vote)
//                                secondValue = true
//                                surveyPair.copy(second = votes)
//                            }
//                        }
//                    } else {
//                        remoteVote = RemoteVote(
//                            surveyPair.second.last().id + 1, voteValue
//                        )
//                        surveyPair.copy(
//                            second = surveyPair.second + remoteVote!!
//                        )
//                    }
//                } else surveyPair
//            }.toMap()
//        }
//        return when {
//            remoteVote != null -> UpdateSurveyVoteResponse.VoteCreated(remoteVote!!)
//            secondValue -> UpdateSurveyVoteResponse.VoteRemoved
//            else -> UpdateSurveyVoteResponse.VoteValueChanged
//        }
    }

    private inline fun <T> wrapErrors(call: () -> T): T = try {
        call()
    } catch (ex: UnknownHostException) {
        throw RemoteException(ex, "Unknown host (no connection)")
    } catch (ex: HttpException) {
        if (ex.code() == 404) {
            throw ResourceNotFoundException(ex, "Not found (404)")
        } else {
            throw RemoteException(ex, "Non 2XX response: ${ex.code()}")
        }
    }
}

private fun UserResponseData.toLogInResponse(): LogInResponse {
    val remoteUser = profileData.userData.toRemoteUser()
    val remoteUserVotes = profileData.userVotesData.toRemoteUserVotes()
    val remoteUserSurveys = profileData.userSurveysData.map { it.toRemoteSurvey() }
    return LogInResponse.Success(remoteUser, remoteUserVotes, remoteUserSurveys)
}

private fun UserData.toRemoteUser() = RemoteUser(email, name, age, sex, countryCode)
private fun UserVotesData.toRemoteUserVotes() = RemoteUserVotes(upvotedSurveyIds, downvotedSurveyIds)

private fun SurveyData.toRemoteSurvey() =
    RemoteSurvey(id, ownerId, data.title, data.desc, upvotedUserIds, downvotedUserIds)
