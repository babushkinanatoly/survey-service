package ru.babushkinanatoly.core_impl.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.babushkinanatoly.core_api.UserLogInData
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

    override suspend fun logIn(logInData: UserLogInData): LogInResponse = wrapErrors {
        try {
            surveyService.getUser(GetUserRequestData(logInData.email, logInData.password)).apply {
                this@ApiImpl.token = token
            }.toLogInResponse()
        } catch (ex: HttpException) {
            if (ex.code() == 500) LogInResponse.InvalidCredentials else throw RemoteException(ex)
        }
    }

    override suspend fun logOut() {
        token = null
    }

    override suspend fun createSurvey(title: String, desc: String): RemoteSurvey = wrapErrors {
        surveyService.createSurvey(CreateSurveyRequestData(title, desc)).toRemoteSurvey()
    }

    override suspend fun getSurveys(count: Int, startAfter: String?): List<RemoteSurvey> = wrapErrors {
        surveyService.getSurveys(count, startAfter).map { it.toRemoteSurvey() }
    }

    override suspend fun getSurvey(surveyId: String): RemoteSurvey = wrapErrors {
        surveyService.getSurvey(surveyId).toRemoteSurvey()
    }

    override suspend fun deleteSurvey(surveyId: String): Unit = wrapErrors {
        surveyService.deleteSurvey(surveyId)
    }

    override suspend fun updateSurveyVote(surveyId: String, voteValue: Boolean?): RemoteSurvey = wrapErrors {
        surveyService.setSurveyVote(
            SetVoteRequestData(surveyId, voteValue.toRemoteVoteValue())
        ).toRemoteSurvey()
    }

    override suspend fun updateSurveyTitle(surveyId: String, title: String): RemoteSurvey = wrapErrors {
        surveyService.setSurveyTitle(surveyId, SetTitleRequestData(title)).toRemoteSurvey()
    }

    override suspend fun updateSurveyDesc(surveyId: String, desc: String): RemoteSurvey = wrapErrors {
        surveyService.setSurveyDesc(surveyId, SetDescRequestData(desc)).toRemoteSurvey()
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
    } catch (ex: SocketTimeoutException) {
        throw RemoteException(ex, "Server not responding")
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

private fun Boolean?.toRemoteVoteValue() = when (this) {
    true -> "Up"
    false -> "Down"
    null -> "None"
}
