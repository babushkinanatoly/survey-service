package ru.babushkinanatoly.core_impl.db.entity

data class UserSurveyEntity(
    val id: Long,
    val remoteId: String,
    val title: String,
    val desc: String,
    val upvotedUserIds: List<String>,
    val downvotedUserIds: List<String>,
)
