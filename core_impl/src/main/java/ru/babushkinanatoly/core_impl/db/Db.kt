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

    suspend fun insertUser(user: UserEntity)
    suspend fun insertUserSurveys(userSurveys: List<UserSurveyEntity>)
    suspend fun insertUserVotes(userVotes: List<UserVoteEntity>)

    suspend fun updateUserSurveyTitle(id: String, title: String)
    suspend fun updateUserSurveyDesc(id: String, desc: String)

    suspend fun removeUserVote(surveyId: String)
    suspend fun updateUserVote(surveyId: String, value: Boolean)
}
