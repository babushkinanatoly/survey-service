package ru.babushkinanatoly.core_impl

import kotlinx.coroutines.flow.map
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.core_impl.api.Api
import ru.babushkinanatoly.core_impl.api.RemoteException
import ru.babushkinanatoly.core_impl.db.Db
import ru.babushkinanatoly.core_impl.db.UserSurveyWithVotes
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity
import ru.babushkinanatoly.core_impl.db.entity.VoteEntity

class RepoImpl(
    private val db: Db,
    private val api: Api,
) : Repo {

    private val user = db.getUser()

    override val currentUser = user.map { it?.toUser() }

    override fun getUserSurveys() = db.getUserSurveys().map { surveysWithVotes ->
        surveysWithVotes.map { it.toUserSurvey() }
    }

    override suspend fun onLogIn(userAuthData: UserAuthData): LogInResult = try {
        when (val response = api.logIn(userAuthData)) {
            is LogInResponse.Success -> {
                db.insertUserSurveys(response.remoteUserSurveys.map { it.key.toUserSurveyEntity() })
                db.insertUserVotes(response.remoteUserVotes.map { it.toUserVoteEntity() })
                db.insertVotes(response.remoteUserSurveys.flatMap { it.toVoteEntity() })
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
fun VoteEntity.toVote() = Vote(id, value)

fun UserSurveyWithVotes.toUserSurvey() =
    UserSurvey(userSurvey.id, userSurvey.title, userSurvey.desc, votes.map { it.toVote() })

fun RemoteUser.toUserEntity() = UserEntity(id, email, name)
fun RemoteSurvey.toUserSurveyEntity() = UserSurveyEntity(id, title, desc)
fun Map.Entry<RemoteVote, RemoteSurvey>.toUserVoteEntity() = UserVoteEntity(key.id, key.value, value.id)
fun Map.Entry<RemoteSurvey, List<RemoteVote>>.toVoteEntity() = value.map { VoteEntity(it.id, it.value, key.id) }
