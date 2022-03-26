package ru.babushkinanatoly.core_impl.api

import com.squareup.moshi.Json
import retrofit2.http.Body
import retrofit2.http.POST

interface SurveyService {

    companion object {
        const val AUTH_HEADER = "Auth"
        private const val REQUIRE_AUTH = "$AUTH_HEADER: true"
    }

    @POST("signin")
    suspend fun getUser(@Body data: GetUserRequestData): UserResponseData
}

data class UserResponseData(
    @field:Json(name = "token")
    val token: String,
    @field:Json(name = "profile")
    val profileData: ProfileData,
)

data class ProfileData(
    @field:Json(name = "userData")
    val userData: UserData,
    @field:Json(name = "userVotesData")
    val userVotesData: UserVotesData,
    @field:Json(name = "userSurveys")
    val userSurveysData: List<SurveyData>,
)

data class UserData(
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "email")
    val email: String,
    @field:Json(name = "age")
    val age: Int,
    @field:Json(name = "sex")
    val sex: String,
    @field:Json(name = "countryCode")
    val countryCode: String,
)

data class UserVotesData(
    @field:Json(name = "up")
    val upvotedSurveyIds: List<String>,
    @field:Json(name = "down")
    val downvotedSurveyIds: List<String>,
)

data class SurveyData(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "ownerId")
    val ownerId: String,
    @field:Json(name = "data")
    val data: SurveyMainData,
    @field:Json(name = "upVotes")
    val upvotedUserIds: List<String>,
    @field:Json(name = "downVotes")
    val downvotedUserIds: List<String>,
)

data class SurveyMainData(
    @field:Json(name = "title")
    val title: String,
    @field:Json(name = "desc")
    val desc: String,
)

data class GetUserRequestData(
    val email: String,
    val password: String,
)


/////////////////////////////////////////////////////////////////////////////////////////


data class SurveysResponse(val surveys: Map<RemoteSurvey, List<String>>)
data class SurveyResponse(val survey: Pair<RemoteSurvey, List<String>>)
