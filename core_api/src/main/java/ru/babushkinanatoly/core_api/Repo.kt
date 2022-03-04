package ru.babushkinanatoly.core_api

interface Repo {
    suspend fun onLogIn(userAuthData: UserAuthData): LogInResult
}
