package ru.babushkinanatoly.core_impl

import kotlinx.coroutines.flow.map
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.core_impl.api.Api
import ru.babushkinanatoly.core_impl.api.RemoteException
import ru.babushkinanatoly.core_impl.db.Db
import ru.babushkinanatoly.core_impl.db.entity.UserEntity

class RepoImpl(
    db: Db,
    private val api: Api,
) : Repo {

    private val user = db.getUser()

    override val currentUser = user.map {
        it?.toUser()
    }

    override suspend fun onLogIn(userAuthData: UserAuthData): LogInResult = try {
        when (api.logIn(userAuthData)) {
            is LogInResponse.Success -> {
                // TODO: Write the response data to db
                LogInResult.OK
            }
            LogInResponse.InvalidCredentials -> LogInResult.INVALID_CREDENTIALS
        }
    } catch (ex: RemoteException) {
        LogInResult.CONNECTION_ERROR
    }
}

fun UserEntity.toUser() = User(id, email, name)
