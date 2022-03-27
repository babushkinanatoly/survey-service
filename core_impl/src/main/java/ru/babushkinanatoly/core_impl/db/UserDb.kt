package ru.babushkinanatoly.core_impl.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.babushkinanatoly.core_impl.db.dao.UserDao
import ru.babushkinanatoly.core_impl.db.dao.UserSurveyDao
import ru.babushkinanatoly.core_impl.db.dao.UserVoteDao
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import ru.babushkinanatoly.core_impl.db.entity.UserSurveyEntity
import ru.babushkinanatoly.core_impl.db.entity.UserVoteEntity

@Database(
    entities = [
        UserEntity::class,
        UserSurveyEntity::class,
        UserVoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(VotesConverter::class)
internal abstract class UserDb : RoomDatabase() {

    abstract fun user(): UserDao
    abstract fun userSurvey(): UserSurveyDao
    abstract fun userVote(): UserVoteDao
}
