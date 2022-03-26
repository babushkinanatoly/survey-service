package ru.babushkinanatoly.core_impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.core_impl.api.*
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
                db.insertUserSurveys(response.userSurveys.map { it.toUserSurveyEntity() })
                db.insertUserVotes(
                    response.userVotes.upvotedSurveyIds.map { it.toUserVoteEntity(true) } +
                            response.userVotes.downvotedSurveyIds.map { it.toUserVoteEntity(false) }
                )
                db.insertUser(response.user.toUserEntity())
                LogInResult.OK
            }
            LogInResponse.InvalidCredentials -> LogInResult.INVALID_CREDENTIALS
        }
    } catch (ex: RemoteException) {
        LogInResult.CONNECTION_ERROR
    }

    override fun getSurveys(scope: CoroutineScope) = PagedFeedImpl(scope = scope, db = db, api = api)

    override suspend fun getSurvey(surveyId: String): SurveyResult = try {
        TODO()
    } catch (ex: RemoteException) {
        SurveyResult.Error
    }

    override suspend fun updateSurveyVote(surveyId: String, voteId: Long?, value: Boolean) {
//        when (val result = api.updateSurveyVote(surveyId, voteId, value)) {
//            // TODO: result - ok - write to db
//            is UpdateSurveyVoteResponse.VoteCreated ->
//                db.insertUserVotes(listOf(result.remoteVote.toUserVoteEntity(surveyId)))
//            UpdateSurveyVoteResponse.VoteRemoved ->
//                db.removeUserVotes(listOf(UserVoteEntity(voteId!!, value, surveyId)))
//            UpdateSurveyVoteResponse.VoteValueChanged ->
//                db.updateUserVote(UserVoteEntity(voteId!!, value, surveyId))
//        }
    }

    override fun getUserSurveys() = db.getUserSurveys().map { surveys -> surveys.map { it.toUserSurvey() } }

    override fun getUserSurvey(id: String) = db.getUserSurvey(id).map { it.toUserSurvey() }

    override fun updateUserSurveyTitle(id: String, title: String) = db.updateUserSurveyTitle(id, title)
    override fun updateUserSurveyDesc(id: String, desc: String) = db.updateUserSurveyDesc(id, desc)
}

private fun UserEntity.toUser() = User(id, email, name)
private fun UserSurveyEntity.toUserSurvey() = UserSurvey(remoteId, title, desc, upvotedUserIds, downvotedUserIds)
private fun String.toUserVoteEntity(value: Boolean) = UserVoteEntity(0, this, value)
private fun RemoteUser.toUserEntity() = UserEntity(0, email, name, age, sex, countryCode)

private fun RemoteSurvey.toUserSurveyEntity() =
    UserSurveyEntity(0, id, title, desc, upvotedUserIds, downvotedUserIds)
