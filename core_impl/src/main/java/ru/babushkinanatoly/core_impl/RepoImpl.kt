package ru.babushkinanatoly.core_impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.core_impl.api.Api
import ru.babushkinanatoly.core_impl.api.RemoteException
import ru.babushkinanatoly.core_impl.db.Db
import ru.babushkinanatoly.core_impl.db.UserSurveyWithVotesForUserSurvey
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity
import ru.babushkinanatoly.core_impl.db.entity.VoteForUserSurveyEntity

class RepoImpl(
    private val db: Db,
    private val api: Api,
) : Repo {

    private val user = db.getUser()

    override val currentUser = user.map { it?.toUser() }

    override fun getSurveys(scope: CoroutineScope) = PagedFeedImpl(scope = scope, db = db, api = api)

    override suspend fun getSurvey(surveyId: Long): SurveyResult = try {
        val remoteSurvey = api.getSurvey(surveyId).survey
        val userVotes = db.getUserVotes().map { it.id }
        val userVote = remoteSurvey.second.find { userVotes.contains(it.id) }?.toVote()
        SurveyResult.Success(remoteSurvey.toSurvey(userVote))
    } catch (ex: RemoteException) {
        SurveyResult.Error
    }

    override suspend fun updateSurveyVote(surveyId: Long, voteId: Long?, value: Boolean) {
        when (val result = api.updateSurveyVote(surveyId, voteId, value)) {
            // TODO: result - ok - write to db
            is UpdateSurveyVoteResponse.VoteCreated ->
                db.insertUserVotes(listOf(result.remoteVote.toUserVoteEntity(surveyId)))
            UpdateSurveyVoteResponse.VoteRemoved ->
                db.removeUserVotes(listOf(UserVoteEntity(voteId!!, value, surveyId)))
            UpdateSurveyVoteResponse.VoteValueChanged ->
                db.updateUserVote(UserVoteEntity(voteId!!, value, surveyId))
        }
    }

    override fun getUserSurveys() = db.getUserSurveys().map { surveysWithVotes ->
        surveysWithVotes.map { it.toUserSurvey() }
    }

    override fun getUserSurvey(id: Long) = db.getUserSurvey(id).map { it.toUserSurvey() }

    override fun updateUserSurveyTitle(id: Long, title: String) = db.updateUserSurveyTitle(id, title)
    override fun updateUserSurveyDesc(id: Long, desc: String) = db.updateUserSurveyDesc(id, desc)

    override suspend fun onLogIn(userAuthData: UserAuthData): LogInResult = try {
        when (val response = api.logIn(userAuthData)) {
            is LogInResponse.Success -> {
                db.insertUserSurveys(response.remoteUserSurveys.map { it.key.toUserSurveyEntity() })
                db.insertUserVotes(response.remoteUserVotes.map { it.toUserVoteEntity() })
                db.insertVotesForUserSurveys(response.remoteUserSurveys.flatMap { it.toVoteForUserSurveyEntity() })
                db.insertUser(response.remoteUser.toUserEntity())
                LogInResult.OK
            }
            LogInResponse.InvalidCredentials -> LogInResult.INVALID_CREDENTIALS
        }
    } catch (ex: RemoteException) {
        LogInResult.CONNECTION_ERROR
    }
}

private fun UserEntity.toUser() = User(id, email, name)
private fun VoteForUserSurveyEntity.toVote() = Vote(id, value)

private fun UserSurveyWithVotesForUserSurvey.toUserSurvey() =
    UserSurvey(userSurvey.id, userSurvey.title, userSurvey.desc, votesForUserSurvey.map { it.toVote() })

fun RemoteVote.toVote() = Vote(id, value)
private fun RemoteVote.toUserVoteEntity(surveyId: Long) = UserVoteEntity(id, value, surveyId)
private fun RemoteUser.toUserEntity() = UserEntity(id, email, name)
private fun RemoteSurvey.toUserSurveyEntity() = UserSurveyEntity(id, title, desc)
private fun Map.Entry<RemoteVote, RemoteSurvey>.toUserVoteEntity() = UserVoteEntity(key.id, key.value, value.id)

private fun Map.Entry<RemoteSurvey, List<RemoteVote>>.toVoteForUserSurveyEntity() =
    value.map { VoteForUserSurveyEntity(it.id, it.value, key.id) }

fun Map.Entry<RemoteSurvey, List<RemoteVote>>.toSurvey(userVote: Vote?) =
    Survey(key.id, key.title, key.desc, value.map { it.toVote() }, userVote)

private fun Pair<RemoteSurvey, List<RemoteVote>>.toSurvey(userVote: Vote?) =
    Survey(first.id, first.title, first.desc, second.map { it.toVote() }, userVote)
