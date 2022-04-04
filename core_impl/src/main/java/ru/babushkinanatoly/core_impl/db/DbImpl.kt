package ru.babushkinanatoly.core_impl.db

import android.content.Context
import androidx.room.Room
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity

class DbImpl(context: Context) : Db {

    private val db = Room.databaseBuilder(context, UserDb::class.java, "user.db")
        .fallbackToDestructiveMigration()
        .build()

    override suspend fun logOut() {
        db.clearAllTables()
    }

    override suspend fun insertUser(user: UserEntity) = db.user().insertUser(user)

    override suspend fun insertUserSurveys(userSurveys: List<UserSurveyEntity>) =
        db.userSurvey().insertSurveys(userSurveys)

    override suspend fun insertUserVotes(userVotes: List<UserVoteEntity>) =
        db.userVote().insertVotes(userVotes)

    override fun getUser() = db.user().getUser()
    override fun getUserSurveys() = db.userSurvey().getSurveys()
    override fun getUserSurvey(id: String) = db.userSurvey().getSurvey(id)
    override fun getUserVotes() = db.userVote().getVotes()

    override suspend fun deleteUserSurvey(id: String) = db.userSurvey().deleteSurvey(id)

    override suspend fun getUserVote(surveyId: String) = db.userVote().getVote(surveyId)

    override suspend fun updateUserSurveyTitle(id: String, title: String) =
        db.userSurvey().updateSurveyTitle(id, title)

    override suspend fun updateUserSurveyDesc(id: String, desc: String) =
        db.userSurvey().updateSurveyDesc(id, desc)

    override suspend fun updateUserVote(surveyId: String, value: Boolean) =
        db.userVote().updateVote(surveyId, value)

    override suspend fun deleteUserVote(surveyId: String) = db.userVote().deleteVote(surveyId)
}
