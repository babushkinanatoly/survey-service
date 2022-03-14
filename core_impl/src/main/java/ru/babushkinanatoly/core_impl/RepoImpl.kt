package ru.babushkinanatoly.core_impl

import kotlinx.coroutines.flow.map
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.core_impl.api.Api
import ru.babushkinanatoly.core_impl.api.RemoteException
import ru.babushkinanatoly.core_impl.db.Db
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity

class RepoImpl(
    private val db: Db,
    private val api: Api,
) : Repo {

    private val user = db.getUser()

    override val currentUser = user.map { it?.toUser() }

    override suspend fun onLogIn(userAuthData: UserAuthData): LogInResult = try {
        when (val response = api.logIn(userAuthData)) {
            is LogInResponse.Success -> {
                db.insertUserSurveys(response.remoteUserSurveys.map { it.key.toUserSurveyEntity() })
                db.insertUserVotes(response.remoteUserVotes.map { it.toUserVoteEntity() })
                db.insertUser(response.remoteUser.toUserEntity())
                LogInResult.OK
            }
            LogInResponse.InvalidCredentials -> LogInResult.INVALID_CREDENTIALS
        }
    } catch (ex: RemoteException) {
        LogInResult.CONNECTION_ERROR
    }
}

fun UserEntity.toUser() = User(id, email, name)

fun RemoteUser.toUserEntity() = UserEntity(id, email, name)
fun RemoteSurvey.toUserSurveyEntity() = UserSurveyEntity(id, title, desc)
fun Map.Entry<RemoteVote, RemoteSurvey>.toUserVoteEntity() = UserVoteEntity(key.id, key.value, value.id)
