package ru.babushkinanatoly.core_impl.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity

@Dao
interface UserSurveyDao {

    @Insert
    suspend fun insertSurveys(surveys: List<UserSurveyEntity>)

    @Query("SELECT * FROM user_surveys ORDER BY remoteId DESC")
    fun getSurveys(): Flow<List<UserSurveyEntity>>

    @Query("SELECT * FROM user_surveys WHERE remoteId = :id")
    fun getSurvey(id: String): Flow<UserSurveyEntity>

    @Query("UPDATE user_surveys SET title = :title WHERE remoteId = :id")
    suspend fun updateSurveyTitle(id: String, title: String)

    @Query("UPDATE user_surveys SET `desc` = :desc WHERE remoteId = :id")
    suspend fun updateSurveyDesc(id: String, desc: String)
}
