package ru.babushkinanatoly.core_impl.db.entity

data class UserVoteEntity(
    val id: Long,
    val surveyRemoteId: String,
    val value: Boolean,
)
