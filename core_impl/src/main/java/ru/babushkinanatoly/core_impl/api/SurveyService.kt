package ru.babushkinanatoly.core_impl.api

import com.squareup.moshi.Json
import retrofit2.http.*

interface SurveyService {

    companion object {
        const val AUTH_HEADER = "Auth"
        private const val REQUIRE_AUTH = "$AUTH_HEADER: true"
    }

    @POST("signin")
    suspend fun getUser(@Body data: GetUserRequestData): UserResponseData

    @GET("feed")
    @Headers(REQUIRE_AUTH)
    suspend fun getSurveys(
        @Query("count") count: Int,
        @Query("startAfter") startAfter: String?,
    ): List<SurveyData>

    @GET("survey/{id}")
    @Headers(REQUIRE_AUTH)
    suspend fun getSurvey(@Path("id") id: String): SurveyData

    @POST("vote")
    @Headers(REQUIRE_AUTH)
    suspend fun setSurveyVote(@Body data: SetVoteRequestData): SurveyData

    @PUT("survey/{id}")
    @Headers(REQUIRE_AUTH)
    suspend fun setSurveyTitle(
        @Path("id") id: String,
        @Body data: SetTitleRequestData,
    ): SurveyData

    @PUT("survey/{id}")
    @Headers(REQUIRE_AUTH)
    suspend fun setSurveyDesc(
        @Path("id") id: String,
        @Body data: SetDescRequestData,
    ): SurveyData

    @POST("surveys")
    @Headers(REQUIRE_AUTH)
    suspend fun createSurvey(@Body data: CreateSurveyRequestData): SurveyData

    @DELETE("survey/{id}")
    @Headers(REQUIRE_AUTH)
    suspend fun deleteSurvey(@Path("id") id: String)
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

data class SetVoteRequestData(
    val surveyId: String,
    val vote: String,
)

data class SetTitleRequestData(
    val title: String,
)

data class SetDescRequestData(
    val desc: String,
)

data class CreateSurveyRequestData(
    val title: String,
    val desc: String,
)
