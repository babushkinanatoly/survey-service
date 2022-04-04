package ru.babushkinanatoly.core_impl.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity

@Dao
interface UserVoteDao {

    @Insert
    suspend fun insertVotes(votes: List<UserVoteEntity>)

    @Query("SELECT * FROM user_votes")
    fun getVotes(): Flow<List<UserVoteEntity>>

    @Query("SELECT * FROM user_votes WHERE surveyRemoteId = :surveyId")
    suspend fun getVote(surveyId: String): UserVoteEntity?

    @Query("UPDATE user_votes SET value = :value WHERE surveyRemoteId = :surveyId")
    suspend fun updateVote(surveyId: String, value: Boolean)

    @Query("DELETE FROM user_votes WHERE surveyRemoteId = :surveyId")
    suspend fun deleteVote(surveyId: String)
}
