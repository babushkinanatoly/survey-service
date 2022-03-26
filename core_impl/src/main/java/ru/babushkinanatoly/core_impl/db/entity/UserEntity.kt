package ru.babushkinanatoly.core_impl.db.entity

data class UserEntity(
    val id: Long,
    val email: String,
    val name: String,
    val age: Int,
    val sex: String,
    val countryCode: String,
)
