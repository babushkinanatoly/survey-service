package ru.babushkinanatoly.core_impl.db

import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity
import ru.babushkinanatoly.core_impl.db.entity.VoteEntity

interface Db {
    fun getUser(): Flow<UserEntity?>
    fun getUserSurveys(): Flow<List<UserSurveyWithVotes>>
    fun getUserSurvey(id: Long): Flow<UserSurveyWithVotes>

    fun updateUserSurveyTitle(id: Long, title: String)
    fun updateUserSurveyDesc(id: Long, desc: String)

    fun insertUser(user: UserEntity)
    fun insertUserSurveys(userSurveys: List<UserSurveyEntity>)
    fun insertUserVotes(userVotes: List<UserVoteEntity>)
    fun insertVotes(votes: List<VoteEntity>)
}
