package ru.babushkinanatoly.core_impl.api

import ru.babushkinanatoly.core_api.LogInResponse
import ru.babushkinanatoly.core_api.UserAuthData

interface Api {
    suspend fun logIn(userAuthData: UserAuthData): LogInResponse
}

class RemoteException(cause: Throwable?, message: String? = null) : RuntimeException(message, cause)
