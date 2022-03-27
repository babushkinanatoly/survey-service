package ru.babushkinanatoly.core_impl.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_votes")
data class UserVoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val surveyRemoteId: String,
    val value: Boolean,
)
