package ru.babushkinanatoly.core_impl.db

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity
import ru.babushkinanatoly.core_impl.db.entity.VoteEntity

class DbImpl(context: Context) : Db {

    private val _user = MutableStateFlow<UserEntity?>(null)
    private val _userSurveys = MutableStateFlow<List<UserSurveyEntity>>(listOf())
    private val _userVotes = MutableStateFlow<List<UserVoteEntity>>(listOf())
    private val _votes = MutableStateFlow<List<VoteEntity>>(listOf())

    override fun getUser() = _user

    override fun getUserSurveys() = _userSurveys.map { userSurveys ->
        userSurveys.map { userSurvey ->
            UserSurveyWithVotes(userSurvey, _votes.value.filter { it.id == userSurvey.id })
        }
    }

    override fun getUserSurvey(id: Long): Flow<UserSurveyWithVotes> {
        return _userSurveys.map { userSurveys ->
            val userSurvey = userSurveys.first { it.id == id }
            UserSurveyWithVotes(userSurvey, _votes.value.filter { it.id == userSurvey.id })
        }
    }

    override fun insertUser(user: UserEntity) = _user.update { user }

    override fun insertUserSurveys(userSurveys: List<UserSurveyEntity>) {
        _userSurveys.update { userSurveys }
    }

    override fun insertUserVotes(userVotes: List<UserVoteEntity>) {
        _userVotes.update { userVotes }
    }

    override fun insertVotes(votes: List<VoteEntity>) {
        _votes.update { votes }
    }
}

data class UserSurveyWithVotes(
    val userSurvey: UserSurveyEntity,
    val votes: List<VoteEntity>,
)
