package ru.babushkinanatoly.core_impl.db.entity

data class VoteEntity(
    val id: Long,
    val value: Boolean,
    val userSurveyId: Long,
)
