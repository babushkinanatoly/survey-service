package ru.babushkinanatoly.core_impl.db

import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity

interface Db {
    fun getUser(): Flow<UserEntity?>

    fun insertUser(user: UserEntity)
    fun insertUserSurveys(userSurveys: List<UserSurveyEntity>)
    fun insertUserVotes(userVotes: List<UserVoteEntity>)
}
