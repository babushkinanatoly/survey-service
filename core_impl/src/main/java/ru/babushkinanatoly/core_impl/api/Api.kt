package ru.babushkinanatoly.core_impl.api

import ru.babushkinanatoly.core_api.LogInResponse
import ru.babushkinanatoly.core_api.SurveysResponse
import ru.babushkinanatoly.core_api.UserAuthData

interface Api {
    suspend fun getSurveys(): SurveysResponse

    suspend fun logIn(userAuthData: UserAuthData): LogInResponse
}

class RemoteException(cause: Throwable?, message: String? = null) : RuntimeException(message, cause)
