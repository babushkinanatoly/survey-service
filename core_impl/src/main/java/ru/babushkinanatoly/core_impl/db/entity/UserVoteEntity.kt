package ru.babushkinanatoly.core_impl.db.entity

data class UserVoteEntity(
    val id: Long,
    val value: Boolean,
    val surveyId: Long,
)