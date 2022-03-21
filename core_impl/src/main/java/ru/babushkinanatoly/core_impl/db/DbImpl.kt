package ru.babushkinanatoly.core_impl.db

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity
import ru.babushkinanatoly.core_impl.db.entity.VoteForUserSurveyEntity

class DbImpl(context: Context) : Db {

    private val _user = MutableStateFlow<UserEntity?>(null)
    private val _userSurveys = MutableStateFlow<List<UserSurveyEntity>>(listOf())
    private val _userVotes = MutableStateFlow<List<UserVoteEntity>>(listOf())
    private val _votesForUserSurveys = MutableStateFlow<List<VoteForUserSurveyEntity>>(listOf())

    override fun getUser() = _user

    override fun getUserSurveys() = _userSurveys.map { userSurveys ->
        userSurveys.map { userSurvey ->
            UserSurveyWithVotesForUserSurvey(
                userSurvey, _votesForUserSurveys.value.filter { it.id == userSurvey.id }
            )
        }
    }

    override fun getUserSurvey(id: Long): Flow<UserSurveyWithVotesForUserSurvey> {
        return _userSurveys.map { userSurveys ->
            val userSurvey = userSurveys.first { it.id == id }
            UserSurveyWithVotesForUserSurvey(
                userSurvey, _votesForUserSurveys.value.filter { it.id == userSurvey.id }
            )
        }
    }

    override fun updateUserSurveyTitle(id: Long, title: String) {
        _userSurveys.update { surveys ->
            surveys.map {
                if (it.id == id) it.copy(title = title) else it
            }
        }
    }

    override fun updateUserSurveyDesc(id: Long, desc: String) {
        _userSurveys.update { surveys ->
            surveys.map {
                if (it.id == id) it.copy(desc = desc) else it
            }
        }
    }

    override fun insertUser(user: UserEntity) = _user.update { user }

    override fun insertUserSurveys(userSurveys: List<UserSurveyEntity>) {
        _userSurveys.update { userSurveys }
    }

    override fun insertUserVotes(userVotes: List<UserVoteEntity>) {
        _userVotes.update { userVotes }
    }

    override fun insertVotesForUserSurveys(votesForUserSurveys: List<VoteForUserSurveyEntity>) {
        _votesForUserSurveys.update { votesForUserSurveys }
    }
}

data class UserSurveyWithVotesForUserSurvey(
    val userSurvey: UserSurveyEntity,
    val votesForUserSurvey: List<VoteForUserSurveyEntity>,
)
