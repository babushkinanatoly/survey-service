package ru.babushkinanatoly.core_impl.db

import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity
import ru.babushkinanatoly.core_impl.db.entity.VoteForUserSurveyEntity

interface Db {
    fun getUser(): Flow<UserEntity?>
    fun getUserSurveys(): Flow<List<UserSurveyWithVotesForUserSurvey>>
    fun getUserSurvey(id: Long): Flow<UserSurveyWithVotesForUserSurvey>

    suspend fun getUserVotes(): List<UserVoteEntity>

    fun updateUserSurveyTitle(id: Long, title: String)
    fun updateUserSurveyDesc(id: Long, desc: String)

    fun insertUser(user: UserEntity)
    fun insertUserSurveys(userSurveys: List<UserSurveyEntity>)
    fun insertUserVotes(userVotes: List<UserVoteEntity>)
    fun insertVotesForUserSurveys(votesForUserSurveys: List<VoteForUserSurveyEntity>)

    fun removeUserVotes(userVotes: List<UserVoteEntity>)
    fun updateUserVote(userVote: UserVoteEntity)
}
