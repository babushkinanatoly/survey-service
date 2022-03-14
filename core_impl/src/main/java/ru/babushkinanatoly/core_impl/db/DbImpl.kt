package ru.babushkinanatoly.core_impl.db

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity

class DbImpl(context: Context) : Db {

    private val _user = MutableStateFlow<UserEntity?>(null)
    private val _userSurveys = MutableStateFlow<List<UserSurveyEntity>>(listOf())
    private val _userVotes = MutableStateFlow<List<UserVoteEntity>>(listOf())

    override fun getUser(): Flow<UserEntity?> = _user

    override fun insertUser(user: UserEntity) = _user.update { user }

    override fun insertUserSurveys(userSurveys: List<UserSurveyEntity>) {
        _userSurveys.update { userSurveys }
    }

    override fun insertUserVotes(userVotes: List<UserVoteEntity>) {
        _userVotes.update { userVotes }
    }
}
