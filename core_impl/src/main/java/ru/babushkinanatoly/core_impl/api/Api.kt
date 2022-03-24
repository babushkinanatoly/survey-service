package ru.babushkinanatoly.core_impl.api

import ru.babushkinanatoly.core_api.*

interface Api {
    suspend fun getSurveys(): SurveysResponse
    suspend fun getSurvey(surveyId: Long): SurveyResponse

    // TODO: remove voteId later
    suspend fun updateSurveyVote(surveyId: Long, voteId: Long?, voteValue: Boolean): UpdateSurveyVoteResponse

    suspend fun logIn(userAuthData: UserAuthData): LogInResponse
}

class RemoteException(cause: Throwable?, message: String? = null) : RuntimeException(message, cause)
