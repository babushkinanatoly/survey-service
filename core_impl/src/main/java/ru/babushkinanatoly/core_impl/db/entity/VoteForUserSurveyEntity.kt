package ru.babushkinanatoly.core_impl.db.entity

data class VoteForUserSurveyEntity(
    val id: Long,
    val value: Boolean,
    val userSurveyId: Long,
)
