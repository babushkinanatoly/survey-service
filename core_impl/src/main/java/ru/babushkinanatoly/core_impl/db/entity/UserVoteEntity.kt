package ru.babushkinanatoly.core_impl.db.entity

class UserVoteEntity(
    val id: Long,
    val value: Boolean,
    val surveyId: Long,
)
