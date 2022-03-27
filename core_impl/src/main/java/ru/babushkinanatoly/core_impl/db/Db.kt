package ru.babushkinanatoly.core_impl.db

import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity

interface Db {
    fun getUser(): Flow<UserEntity?>
    fun getUserSurveys(): Flow<List<UserSurveyEntity>>
    fun getUserSurvey(id: String): Flow<UserSurveyEntity>

    suspend fun getUserVotes(): List<UserVoteEntity>
    suspend fun getUserVote(surveyId: String): UserVoteEntity?

    fun updateUserSurveyTitle(id: String, title: String)
    fun updateUserSurveyDesc(id: String, desc: String)

    fun insertUser(user: UserEntity)
    fun insertUserSurveys(userSurveys: List<UserSurveyEntity>)
    fun insertUserVotes(userVotes: List<UserVoteEntity>)

    fun removeUserVote(surveyId: String)
    fun updateUserVote(surveyId: String, value: Boolean)
}
