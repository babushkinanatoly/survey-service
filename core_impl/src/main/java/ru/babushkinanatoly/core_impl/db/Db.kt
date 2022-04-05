package ru.babushkinanatoly.core_impl.db

import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity

interface Db {
    suspend fun logOut()

    suspend fun insertUser(user: UserEntity)
    suspend fun insertUserSurveys(userSurveys: List<UserSurveyEntity>)
    suspend fun insertUserVotes(userVotes: List<UserVoteEntity>)

    suspend fun updateUser(user: UserEntity)

    fun getUser(): Flow<UserEntity?>
    fun getUserSurveys(): Flow<List<UserSurveyEntity>>
    fun getUserSurvey(id: String): Flow<UserSurveyEntity?>
    fun getUserVotes(): Flow<List<UserVoteEntity>>

    suspend fun deleteUserSurvey(id: String)

    suspend fun getUserVote(surveyId: String): UserVoteEntity?

    suspend fun updateUserSurveyTitle(id: String, title: String)
    suspend fun updateUserSurveyDesc(id: String, desc: String)

    suspend fun updateUserVote(surveyId: String, value: Boolean)
    suspend fun deleteUserVote(surveyId: String)
}
