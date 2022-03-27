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

    override suspend fun logIn(userAuthData: UserAuthData): LogInResult = try {
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
        val remoteSurvey = api.getSurvey(surveyId)
        val survey = remoteSurvey.toSurvey(
            db.getUserVotes().find { it.surveyRemoteId == remoteSurvey.id }?.value
        )
        SurveyResult.Success(survey)
    } catch (ex: RemoteException) {
        SurveyResult.Error(ex.message ?: "Unknown message")
    }

    override suspend fun updateSurveyVote(surveyId: String, value: Boolean?): SurveyResult = try {
        val remoteSurvey = api.updateSurveyVote(surveyId, value)
        if (value == null) {
            db.removeUserVote(surveyId)
        } else {
            db.getUserVote(surveyId)?.let {
                db.updateUserVote(surveyId, value)
            } ?: db.insertUserVotes(listOf(surveyId.toUserVoteEntity(value)))
        }
        SurveyResult.Success(
            remoteSurvey.toSurvey(
                db.getUserVotes().find { it.surveyRemoteId == remoteSurvey.id }?.value
            )
        )
    } catch (ex: RemoteException) {
        SurveyResult.Error(ex.message ?: "Unknown message")
    }

    override fun getUserSurveys() = db.getUserSurveys().map { surveys -> surveys.map { it.toUserSurvey() } }

    override fun getUserSurvey(id: String) = db.getUserSurvey(id).map { it.toUserSurvey() }

    override suspend fun updateUserSurveyTitle(id: String, title: String): SurveyResult = try {
        val remoteSurvey = api.updateSurveyTitle(id, title)
        db.updateUserSurveyTitle(id, title)
        SurveyResult.Success(
            remoteSurvey.toSurvey(db.getUserVotes().find { it.surveyRemoteId == remoteSurvey.id }?.value)
        )
    } catch (ex: RemoteException) {
        SurveyResult.Error(ex.message ?: "Unknown message")
    }

    override suspend fun updateUserSurveyDesc(id: String, desc: String): SurveyResult = try {
        val remoteSurvey = api.updateSurveyDesc(id, desc)
        db.updateUserSurveyDesc(id, desc)
        SurveyResult.Success(
            remoteSurvey.toSurvey(db.getUserVotes().find { it.surveyRemoteId == remoteSurvey.id }?.value)
        )
    } catch (ex: RemoteException) {
        SurveyResult.Error(ex.message ?: "Unknown message")
    }
}

private fun UserEntity.toUser() = User(id, email, name)
private fun UserSurveyEntity.toUserSurvey() = UserSurvey(remoteId, title, desc, upvotedUserIds, downvotedUserIds)
private fun String.toUserVoteEntity(value: Boolean) = UserVoteEntity(0, this, value)
private fun RemoteUser.toUserEntity() = UserEntity(0, email, name, age, sex, countryCode)

private fun RemoteSurvey.toUserSurveyEntity() =
    UserSurveyEntity(0, id, title, desc, upvotedUserIds, downvotedUserIds)

fun RemoteSurvey.toSurvey(userVote: Boolean?) =
    Survey(id, title, desc, upvotedUserIds, downvotedUserIds, userVote)
