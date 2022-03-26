package ru.babushkinanatoly.core_impl.db

import android.content.Context
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

    override fun getUser() = _user
    override fun getUserSurveys() = _userSurveys

    override fun getUserSurvey(id: String) =
        _userSurveys.map { userSurveys -> userSurveys.first { it.remoteId == id } }

    override suspend fun getUserVotes() = _userVotes.value

    override fun updateUserSurveyTitle(id: String, title: String) {
        _userSurveys.update { surveys ->
            surveys.map {
                if (it.remoteId == id) it.copy(title = title) else it
            }
        }
    }

    override fun updateUserSurveyDesc(id: String, desc: String) {
        _userSurveys.update { surveys ->
            surveys.map {
                if (it.remoteId == id) it.copy(desc = desc) else it
            }
        }
    }

    override fun insertUser(user: UserEntity) = _user.update { user }

    override fun insertUserSurveys(userSurveys: List<UserSurveyEntity>) {
        _userSurveys.update { userSurveys }
    }

    override fun insertUserVotes(userVotes: List<UserVoteEntity>) {
        _userVotes.update { it + userVotes }
    }

    override fun removeUserVotes(userVotes: List<UserVoteEntity>) {
        _userVotes.update { it - userVotes }
    }

    override fun updateUserVote(userVote: UserVoteEntity) {
        _userVotes.update { userVotes ->
            userVotes.map { if (it.id == userVote.id) userVote else it }
        }
    }
}

data class UserSurveyWithVotesForUserSurvey(
    val userSurvey: UserSurveyEntity,
    val votesForUserSurvey: List<VoteForUserSurveyEntity>,
)
