package ru.babushkinanatoly.core_impl.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_surveys")
data class UserSurveyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val remoteId: String,
    val title: String,
    val desc: String,
    val upvotedUserIds: List<String>,
    val downvotedUserIds: List<String>,
)
